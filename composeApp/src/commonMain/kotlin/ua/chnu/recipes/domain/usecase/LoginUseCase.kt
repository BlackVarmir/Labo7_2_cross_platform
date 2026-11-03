package ua.chnu.recipes.domain.usecase

import ua.chnu.recipes.domain.repository.AuthRepository

/** Signs the user in with a username and a password. */
class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(username: String, password: String): Result<Unit> =
        repository.login(username.trim(), password)
}
