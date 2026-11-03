package ua.chnu.recipes.domain.model

/** Pair of tokens issued by the server: a short-living access token and a refresh token to renew it. */
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
)
