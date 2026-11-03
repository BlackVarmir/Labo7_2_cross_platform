package ua.chnu.recipes.ui.main

import androidx.lifecycle.ViewModel
import ua.chnu.recipes.domain.usecase.IsOnboardingCompletedUseCase
import ua.chnu.recipes.domain.usecase.IsUserAuthorizedUseCase
import ua.chnu.recipes.navigation.AuthRoute
import ua.chnu.recipes.navigation.HomeRoute
import ua.chnu.recipes.navigation.OnboardingRoute
import ua.chnu.recipes.navigation.Route

/**
 * ViewModel of the main (root) screen. It decides which screen the app opens first:
 *  - the onboarding if the user has not passed it yet,
 *  - the home screen if the user is signed in (the tokens are stored on the device),
 *  - the sign-in screen otherwise.
 */
class MainViewModel(
    isOnboardingCompleted: IsOnboardingCompletedUseCase,
    isUserAuthorized: IsUserAuthorizedUseCase,
) : ViewModel() {
    val startRoute: Route = when {
        !isOnboardingCompleted() -> OnboardingRoute
        isUserAuthorized() -> HomeRoute
        else -> AuthRoute
    }
}
