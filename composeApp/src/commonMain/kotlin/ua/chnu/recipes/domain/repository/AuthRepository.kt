package ua.chnu.recipes.domain.repository

import kotlinx.coroutines.flow.StateFlow
import ua.chnu.recipes.domain.model.AuthTokens

interface AuthRepository {
    /** `true` while the device holds tokens of a signed-in user. Becomes `false` after sign-out or when the session expires. */
    val isAuthorized: StateFlow<Boolean>

    /** Signs in and stores the received tokens. Fails with [ua.chnu.recipes.domain.model.AuthException]. */
    suspend fun login(username: String, password: String): Result<Unit>

    /**
     * Exchanges the stored refresh token for a new pair of tokens and stores it.
     * Returns the new tokens, or `null` if they could not be renewed. When the server rejects the
     * refresh token the session is over and the user becomes unauthorized.
     */
    suspend fun refreshTokens(): AuthTokens?

    /** Removes the stored tokens. */
    suspend fun logout()
}
