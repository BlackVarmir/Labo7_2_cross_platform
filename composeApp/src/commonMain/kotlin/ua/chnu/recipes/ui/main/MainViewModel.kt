package ua.chnu.recipes.ui.main

import androidx.lifecycle.ViewModel
import ua.chnu.recipes.domain.usecase.IsOnboardingCompletedUseCase
import ua.chnu.recipes.navigation.OnboardingRoute
import ua.chnu.recipes.navigation.RecipeListRoute
import ua.chnu.recipes.navigation.Route

/**
 * ViewModel of the main (root) screen. It decides which screen the app opens first:
 * the main screen if the user has already passed the onboarding, the onboarding otherwise.
 */
class MainViewModel(
    isOnboardingCompleted: IsOnboardingCompletedUseCase,
) : ViewModel() {
    val startRoute: Route = if (isOnboardingCompleted()) RecipeListRoute else OnboardingRoute
}
