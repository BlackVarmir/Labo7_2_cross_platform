package ua.chnu.recipes.data.auth

import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException
import ua.chnu.recipes.domain.model.AuthException
import ua.chnu.recipes.domain.model.AuthFailure

/** Maps a network/Ktor error to the domain [AuthException] the UI knows how to explain. */
internal fun Throwable.toAuthException(): AuthException = when (this) {
    is AuthException -> this
    is ClientRequestException -> when (response.status) {
        // dummyjson answers 400 "Invalid credentials" for a wrong username/password.
        HttpStatusCode.BadRequest, HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden ->
            AuthException(AuthFailure.InvalidCredentials, this)
        else -> AuthException(AuthFailure.Unknown, this)
    }
    is ServerResponseException -> AuthException(AuthFailure.Server, this)
    is HttpRequestTimeoutException, is ConnectTimeoutException, is SocketTimeoutException, is IOException ->
        AuthException(AuthFailure.NoConnection, this)
    else -> AuthException(AuthFailure.Unknown, this)
}
