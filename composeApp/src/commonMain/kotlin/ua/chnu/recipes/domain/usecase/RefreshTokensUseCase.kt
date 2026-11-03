package ua.chnu.recipes.domain.usecase

import ua.chnu.recipes.domain.model.AuthTokens
import ua.chnu.recipes.domain.repository.AuthRepository

/** Renews the access token with the stored refresh token. Called by the HTTP client when a request gets 401. */
class RefreshTokensUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): AuthTokens? = repository.refreshTokens()
}
