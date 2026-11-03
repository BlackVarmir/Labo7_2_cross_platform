package ua.chnu.recipes.data.storage

import eu.anifantakis.lib.ksafe.KSafe
import ua.chnu.recipes.domain.model.AuthTokens

/**
 * [TokenStorage] on top of KSafe. KSafe encrypts every value with AES-256-GCM; the key lives in
 * Android Keystore / iOS Keychain / the OS secret store on desktop, so the tokens are never kept in plain text.
 */
class KSafeTokenStorage(private val ksafe: KSafe) : TokenStorage {

    override fun getTokens(): AuthTokens? {
        val access = ksafe.getDirect(KEY_ACCESS_TOKEN, "")
        val refresh = ksafe.getDirect(KEY_REFRESH_TOKEN, "")
        return if (access.isNotEmpty() && refresh.isNotEmpty()) AuthTokens(access, refresh) else null
    }

    override fun saveTokens(tokens: AuthTokens) {
        ksafe.putDirect(KEY_ACCESS_TOKEN, tokens.accessToken)
        ksafe.putDirect(KEY_REFRESH_TOKEN, tokens.refreshToken)
    }

    override fun clear() {
        ksafe.deleteDirect(KEY_ACCESS_TOKEN)
        ksafe.deleteDirect(KEY_REFRESH_TOKEN)
    }

    private companion object {
        const val KEY_ACCESS_TOKEN = "auth_access_token"
        const val KEY_REFRESH_TOKEN = "auth_refresh_token"
    }
}
