package ua.chnu.recipes.data.network

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ua.chnu.recipes.data.storage.TokenStorage
import ua.chnu.recipes.domain.model.AuthTokens

const val BASE_URL = "https://dummyjson.com/"
private const val BASE_HOST = "dummyjson.com"

private val kermitHttpLogger = Logger.withTag("Ktor")

private fun HttpClientConfig<*>.configureCommon() {
    expectSuccess = true
    defaultRequest { url(BASE_URL) }
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        })
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 15_000
        connectTimeoutMillis = 10_000
    }
    install(Logging) {
        level = LogLevel.INFO
        logger = object : io.ktor.client.plugins.logging.Logger {
            override fun log(message: String) {
                kermitHttpLogger.d { message }
            }
        }
    }
}

private fun newClient(engine: HttpClientEngine?, block: HttpClientConfig<*>.() -> Unit): HttpClient =
    if (engine != null) HttpClient(engine, block) else HttpClient(block)

/** Client without authorization: used by the login and refresh requests. */
fun createHttpClient(engine: HttpClientEngine? = null): HttpClient = newClient(engine) { configureCommon() }

/**
 * Client for the requests of a signed-in user. The bearer plugin:
 *  - takes the tokens from [tokenStorage] and puts the access token into the `Authorization` header,
 *  - on `401 Unauthorized` calls [refreshTokens] once, repeats the failed request with the new tokens
 *    and keeps the new tokens for the next requests (simultaneous 401s share one refresh).
 */
fun createAuthorizedHttpClient(
    tokenStorage: TokenStorage,
    refreshTokens: suspend () -> AuthTokens?,
    engine: HttpClientEngine? = null,
): HttpClient = newClient(engine) {
    configureCommon()
    install(Auth) {
        bearer {
            loadTokens {
                tokenStorage.getTokens()?.toBearerTokens()
            }
            refreshTokens {
                refreshTokens()?.toBearerTokens()
            }
            // dummyjson answers 401 without a WWW-Authenticate challenge, so the token is sent right away.
            sendWithoutRequest { request -> request.url.host == BASE_HOST }
        }
    }
}

private fun AuthTokens.toBearerTokens() = BearerTokens(accessToken, refreshToken)
