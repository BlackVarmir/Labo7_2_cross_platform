package ua.chnu.recipes.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import ua.chnu.recipes.data.Recipe

/** Navigation 3 destinations. Every route is a [NavKey] stored in the back stack. */
@Serializable
sealed interface Route : NavKey

@Serializable
data object OnboardingRoute : Route

@Serializable
data object AuthRoute : Route

/** Empty home page the user lands on after a successful sign-in. */
@Serializable
data object HomeRoute : Route

@Serializable
data object RecipeListRoute : Route

@Serializable
data object SettingsRoute : Route

@Serializable
data class RecipeDetailRoute(val recipe: Recipe) : Route
