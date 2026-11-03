package ua.chnu.recipes.data

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/** Thin REST client over the open dummyjson "recipes" API. */
class RecipesApi(private val client: HttpClient) {
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
