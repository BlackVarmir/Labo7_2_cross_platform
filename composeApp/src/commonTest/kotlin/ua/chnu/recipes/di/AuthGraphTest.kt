package ua.chnu.recipes.di

import org.koin.dsl.koinApplication
import org.koin.dsl.module
import ua.chnu.recipes.data.RecipesApi
import ua.chnu.recipes.data.storage.TokenStorage
import ua.chnu.recipes.domain.model.AuthTokens
import ua.chnu.recipes.domain.repository.AuthRepository
import ua.chnu.recipes.domain.usecase.IsUserAuthorizedUseCase
import ua.chnu.recipes.domain.usecase.LoginUseCase
import ua.chnu.recipes.domain.usecase.LogoutUseCase
import ua.chnu.recipes.domain.usecase.ObserveAuthStateUseCase
import ua.chnu.recipes.domain.usecase.RefreshTokensUseCase
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

private class InMemoryTokenStorage : TokenStorage {
    private var tokens: AuthTokens? = null
    override fun getTokens() = tokens
    override fun saveTokens(tokens: AuthTokens) { this.tokens = tokens }
    override fun clear() { tokens = null }
}

/** Checks that Koin can build the whole authorization graph (KSafe is replaced by an in-memory storage). */
class AuthGraphTest {
    @Test
    fun authorizationGraphIsResolvable() {
        val koin = koinApplication {
            modules(appModule, module { single<TokenStorage> { InMemoryTokenStorage() } })
        }.koin

        assertNotNull(koin.get<AuthRepository>())
        assertNotNull(koin.get<RecipesApi>())
        assertNotNull(koin.get<LoginUseCase>())
        assertNotNull(koin.get<LogoutUseCase>())
        assertNotNull(koin.get<RefreshTokensUseCase>())
        assertNotNull(koin.get<ObserveAuthStateUseCase>())
        assertFalse(koin.get<IsUserAuthorizedUseCase>()())
    }
}
