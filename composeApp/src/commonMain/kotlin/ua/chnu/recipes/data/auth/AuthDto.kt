package ua.chnu.recipes.data.auth

import kotlinx.serialization.Serializable

/** Lifetime of the access token requested from the server, in minutes. */
const val ACCESS_TOKEN_LIFETIME_MINUTES = 30

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
    val expiresInMins: Int = ACCESS_TOKEN_LIFETIME_MINUTES,
)

/** Response of `POST /auth/login` (the user profile fields are ignored). */
@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
)

@Serializable
data class RefreshRequest(
    val refreshToken: String,
    val expiresInMins: Int = ACCESS_TOKEN_LIFETIME_MINUTES,
)

/** Response of `POST /auth/refresh`. */
@Serializable
data class RefreshResponse(
    val accessToken: String,
    val refreshToken: String,
)
