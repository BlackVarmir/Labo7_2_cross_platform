package ua.chnu.recipes.data.auth

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Authorization endpoints of dummyjson. It works on a client WITHOUT the bearer plugin:
 * login and refresh must never carry (or try to refresh) an access token themselves.
 */
class AuthApi(private val client: HttpClient) {
    private val log = Logger.withTag("AuthApi")

    suspend fun login(username: String, password: String): LoginResponse {
        log.d { "login(username='$username')" }
        return client.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(username, password))
        }.body()
    }

    suspend fun refresh(refreshToken: String): RefreshResponse {
        log.d { "refresh()" }
        return client.post("auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(RefreshRequest(refreshToken))
        }.body()
    }
}
