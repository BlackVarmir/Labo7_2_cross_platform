package ua.chnu.recipes.data.storage

import ua.chnu.recipes.domain.model.AuthTokens

/** Persistent place for the tokens of the signed-in user. */
interface TokenStorage {
    fun getTokens(): AuthTokens?
    fun saveTokens(tokens: AuthTokens)
    fun clear()
}
