package ua.chnu.recipes.data

import kotlinx.serialization.Serializable

/** Response of GET https://dummyjson.com/recipes */
@Serializable
data class RecipesResponse(
    val recipes: List<Recipe> = emptyList(),
    val total: Int = 0,
    val skip: Int = 0,
    val limit: Int = 0,
)

@Serializable
data class Recipe(
    val id: Int,
    val name: String,
    val ingredients: List<String> = emptyList(),
    val instructions: List<String> = emptyList(),
    val prepTimeMinutes: Int = 0,
    val cookTimeMinutes: Int = 0,
    val servings: Int = 0,
    val difficulty: String = "",
    val cuisine: String = "",
    val caloriesPerServing: Int = 0,
    val tags: List<String> = emptyList(),
    val image: String = "",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val mealType: List<String> = emptyList(),
) {
    val totalMinutes: Int get() = prepTimeMinutes + cookTimeMinutes
}
