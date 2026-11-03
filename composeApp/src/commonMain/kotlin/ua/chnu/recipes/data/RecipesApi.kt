package ua.chnu.recipes.data

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val BASE_URL = "https://dummyjson.com/"

/** Thin REST client over the open dummyjson "recipes" API. */
class RecipesApi(private val client: HttpClient = createHttpClient()) {
    private val log = Logger.withTag("RecipesApi")

    /** All recipes, or the ones matching [query] when it is not blank. */
    suspend fun getRecipes(query: String = ""): List<Recipe> {
        log.d { "getRecipes(query='$query')" }
        val response: RecipesResponse = if (query.isBlank()) {
            client.get("recipes") { parameter("limit", 0) }.body()
        } else {
            client.get("recipes/search") {
                parameter("q", query.trim())
                parameter("limit", 0)
            }.body()
        }
        log.d { "loaded ${response.recipes.size} recipes" }
        return response.recipes
    }

    suspend fun getRecipe(id: Int): Recipe {
        log.d { "getRecipe($id)" }
        return client.get("recipes/$id").body()
    }
}

private val kermitHttpLogger = Logger.withTag("Ktor")

fun createHttpClient(): HttpClient = HttpClient {
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
