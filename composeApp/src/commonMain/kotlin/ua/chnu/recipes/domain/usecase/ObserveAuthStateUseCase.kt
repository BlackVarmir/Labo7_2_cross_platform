package ua.chnu.recipes.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import ua.chnu.recipes.domain.repository.AuthRepository

/** Emits `false` as soon as the user signs out or the session expires, so the app can go back to the sign-in screen. */
class ObserveAuthStateUseCase(private val repository: AuthRepository) {
    operator fun invoke(): StateFlow<Boolean> = repository.isAuthorized
}
