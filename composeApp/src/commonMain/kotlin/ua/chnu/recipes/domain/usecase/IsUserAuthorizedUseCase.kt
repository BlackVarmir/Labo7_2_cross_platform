package ua.chnu.recipes.domain.usecase

import ua.chnu.recipes.domain.repository.AuthRepository

/** Whether the user is signed in right now (used to choose the start screen). */
class IsUserAuthorizedUseCase(private val repository: AuthRepository) {
    operator fun invoke(): Boolean = repository.isAuthorized.value
}
