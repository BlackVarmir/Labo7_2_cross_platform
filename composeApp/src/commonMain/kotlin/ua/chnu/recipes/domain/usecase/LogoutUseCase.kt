package ua.chnu.recipes.domain.usecase

import ua.chnu.recipes.domain.repository.AuthRepository

/** Signs the user out: the stored tokens are removed. */
class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.logout()
}
