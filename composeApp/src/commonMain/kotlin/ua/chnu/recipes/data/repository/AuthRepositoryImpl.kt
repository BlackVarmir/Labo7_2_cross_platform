package ua.chnu.recipes.data.repository

import co.touchlab.kermit.Logger
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ua.chnu.recipes.data.auth.AuthApi
import ua.chnu.recipes.data.auth.toAuthException
import ua.chnu.recipes.data.storage.TokenStorage
import ua.chnu.recipes.domain.model.AuthTokens
import ua.chnu.recipes.domain.repository.AuthRepository

/**
 * [AuthRepository] over [AuthApi] (network) and [TokenStorage] (KSafe).
 *
 * @param onSessionChanged called after sign-in / sign-out so that the HTTP client forgets the tokens it has cached.
 */
class AuthRepositoryImpl(
    private val api: AuthApi,
    private val storage: TokenStorage,
    private val onSessionChanged: () -> Unit = {},
) : AuthRepository {
    private val log = Logger.withTag("AuthRepository")

    private val _isAuthorized = MutableStateFlow(storage.getTokens() != null)
    override val isAuthorized: StateFlow<Boolean> = _isAuthorized.asStateFlow()

    override suspend fun login(username: String, password: String): Result<Unit> = try {
        val response = api.login(username, password)
        storage.saveTokens(AuthTokens(response.accessToken, response.refreshToken))
        onSessionChanged()
        _isAuthorized.value = true
        log.i { "signed in as '$username'" }
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        log.w(e) { "sign-in failed" }
        Result.failure(e.toAuthException())
    }

    override suspend fun refreshTokens(): AuthTokens? {
        val current = storage.getTokens() ?: return null
        return try {
            val response = api.refresh(current.refreshToken)
            AuthTokens(response.accessToken, response.refreshToken).also {
                storage.saveTokens(it)
                log.i { "tokens refreshed" }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: ClientRequestException) {
            // The server does not accept the refresh token any more: the session is over.
            log.w(e) { "refresh token rejected, signing out" }
            logout()
            null
        } catch (e: Exception) {
            // Network problem: keep the tokens, the next request will try again.
            log.w(e) { "could not refresh tokens" }
            null
        }
    }

    override suspend fun logout() {
        storage.clear()
        onSessionChanged()
        _isAuthorized.value = false
        log.i { "signed out" }
    }
}
