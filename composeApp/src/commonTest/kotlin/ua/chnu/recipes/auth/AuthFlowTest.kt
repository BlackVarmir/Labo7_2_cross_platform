package ua.chnu.recipes.auth

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import ua.chnu.recipes.data.RecipesApi
import ua.chnu.recipes.data.auth.AuthApi
import ua.chnu.recipes.data.network.createAuthorizedHttpClient
import ua.chnu.recipes.data.network.createHttpClient
import ua.chnu.recipes.data.repository.AuthRepositoryImpl
import ua.chnu.recipes.data.storage.TokenStorage
import ua.chnu.recipes.domain.model.AuthException
import ua.chnu.recipes.domain.model.AuthFailure
import ua.chnu.recipes.domain.model.AuthTokens
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private typealias Handler = suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData

private class FakeTokenStorage(private var tokens: AuthTokens? = null) : TokenStorage {
    override fun getTokens() = tokens
    override fun saveTokens(tokens: AuthTokens) { this.tokens = tokens }
    override fun clear() { tokens = null }
}

/** Auth stack wired on top of a [MockEngine], the same way Koin wires it in the app. */
private class Fixture(initialTokens: AuthTokens? = null, handler: Handler) {
    val storage = FakeTokenStorage(initialTokens)
    val repository: AuthRepositoryImpl
    val recipesApi: RecipesApi

    init {
        val engine = MockEngine(handler)
        lateinit var authorizedClient: HttpClient
        repository = AuthRepositoryImpl(
            api = AuthApi(createHttpClient(engine)),
            storage = storage,
            onSessionChanged = { authorizedClient.authProvider<BearerAuthProvider>()?.clearToken() },
        )
        authorizedClient = createAuthorizedHttpClient(storage, { repository.refreshTokens() }, engine)
        recipesApi = RecipesApi(authorizedClient)
    }
}

private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
private const val EMPTY_RECIPES = """{"recipes":[],"total":0,"skip":0,"limit":0}"""
private fun tokensJson(access: String, refresh: String) =
    """{"id":1,"username":"emilys","accessToken":"$access","refreshToken":"$refresh"}"""

class AuthFlowTest {

    @Test
    fun loginStoresTokensAndMarksUserAuthorized() = runTest {
        val f = Fixture { request ->
            assertEquals("/auth/login", request.url.encodedPath)
            respond(tokensJson("access-1", "refresh-1"), HttpStatusCode.OK, jsonHeaders)
        }
        assertFalse(f.repository.isAuthorized.value)

        val result = f.repository.login("emilys", "emilyspass")

        assertTrue(result.isSuccess)
        assertTrue(f.repository.isAuthorized.value)
        assertEquals(AuthTokens("access-1", "refresh-1"), f.storage.getTokens())
    }

    @Test
    fun wrongCredentialsAreReportedAsInvalidCredentials() = runTest {
        val f = Fixture { respondError(HttpStatusCode.BadRequest, """{"message":"Invalid credentials"}""", jsonHeaders) }

        val error = assertFailsWith<AuthException> { f.repository.login("emilys", "wrong-pass1").getOrThrow() }

        assertEquals(AuthFailure.InvalidCredentials, error.failure)
        assertFalse(f.repository.isAuthorized.value)
        assertNull(f.storage.getTokens())
    }

    @Test
    fun serverErrorIsReportedAsServerFailure() = runTest {
        val f = Fixture { respondError(HttpStatusCode.InternalServerError) }

        val error = assertFailsWith<AuthException> { f.repository.login("emilys", "emilyspass").getOrThrow() }

        assertEquals(AuthFailure.Server, error.failure)
    }

    @Test
    fun unauthorizedRequestRefreshesTokensAndIsRepeated() = runTest {
        val sentAuthHeaders = mutableListOf<String?>()
        val f = Fixture(AuthTokens("old-access", "old-refresh")) { request ->
            when (request.url.encodedPath) {
                "/auth/refresh" -> respond(tokensJson("new-access", "new-refresh"), HttpStatusCode.OK, jsonHeaders)
                else -> {
                    sentAuthHeaders += request.headers[HttpHeaders.Authorization]
                    if (request.headers[HttpHeaders.Authorization] == "Bearer new-access") {
                        respond(EMPTY_RECIPES, HttpStatusCode.OK, jsonHeaders)
                    } else {
                        // dummyjson answers an expired token with 401 and no WWW-Authenticate header
                        respondError(HttpStatusCode.Unauthorized, """{"message":"Token Expired!"}""", jsonHeaders)
                    }
                }
            }
        }

        val recipes = f.recipesApi.getRecipes()

        assertTrue(recipes.isEmpty())
        assertEquals(listOf<String?>("Bearer old-access", "Bearer new-access"), sentAuthHeaders)
        assertEquals(AuthTokens("new-access", "new-refresh"), f.storage.getTokens())
        assertTrue(f.repository.isAuthorized.value)

        // the next request goes out with the new token at once, without another refresh
        sentAuthHeaders.clear()
        f.recipesApi.getRecipes()
        assertEquals(listOf<String?>("Bearer new-access"), sentAuthHeaders)
    }

    @Test
    fun rejectedRefreshTokenEndsTheSession() = runTest {
        val f = Fixture(AuthTokens("old-access", "old-refresh")) { request ->
            when (request.url.encodedPath) {
                "/auth/refresh" ->
                    respondError(HttpStatusCode.Unauthorized, """{"message":"Invalid/Expired Token!"}""", jsonHeaders)
                else -> respondError(HttpStatusCode.Unauthorized, """{"message":"Token Expired!"}""", jsonHeaders)
            }
        }
        assertTrue(f.repository.isAuthorized.value)

        runCatching { f.recipesApi.getRecipes() }

        assertFalse(f.repository.isAuthorized.value)
        assertNull(f.storage.getTokens())
    }

    @Test
    fun tokensAreUsedRightAfterLoginAndForgottenAfterLogout() = runTest {
        val sentAuthHeaders = mutableListOf<String?>()
        val f = Fixture { request ->
            if (request.url.encodedPath == "/auth/login") {
                respond(tokensJson("access-1", "refresh-1"), HttpStatusCode.OK, jsonHeaders)
            } else {
                sentAuthHeaders += request.headers[HttpHeaders.Authorization]
                respond(EMPTY_RECIPES, HttpStatusCode.OK, jsonHeaders)
            }
        }

        f.recipesApi.getRecipes() // signed out
        f.repository.login("emilys", "emilyspass")
        f.recipesApi.getRecipes() // signed in
        f.repository.logout()
        f.recipesApi.getRecipes() // signed out again

        assertEquals(listOf<String?>(null, "Bearer access-1", null), sentAuthHeaders)
        assertFalse(f.repository.isAuthorized.value)
    }
}
