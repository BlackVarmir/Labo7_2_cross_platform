package ua.chnu.recipes

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import ua.chnu.recipes.di.appModule
import ua.chnu.recipes.navigation.AuthRoute
import ua.chnu.recipes.navigation.OnboardingRoute
import ua.chnu.recipes.navigation.RecipeDetailRoute
import ua.chnu.recipes.navigation.RecipeListRoute
import ua.chnu.recipes.navigation.Route
import ua.chnu.recipes.navigation.SettingsRoute
import ua.chnu.recipes.ui.auth.AuthScreen
import ua.chnu.recipes.ui.main.MainViewModel
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import org.jetbrains.compose.resources.stringResource
import ua.chnu.recipes.platform.LocalAppLocale
import ua.chnu.recipes.resources.Res
import ua.chnu.recipes.resources.nav_recipes
import ua.chnu.recipes.resources.nav_settings
import ua.chnu.recipes.settings.AppSettings
import ua.chnu.recipes.ui.detail.RecipeDetailScreen
import ua.chnu.recipes.ui.list.RecipeListScreen
import ua.chnu.recipes.ui.onboarding.OnboardingScreen
import ua.chnu.recipes.ui.settings.SettingsScreen
import ua.chnu.recipes.ui.theme.RecipesTheme

private val log = Logger.withTag("App")

/** Navigation 3 needs to know all [NavKey] subtypes on non-Android targets to (de)serialize the back stack. */
private val navConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(OnboardingRoute::class, OnboardingRoute.serializer())
            subclass(AuthRoute::class, AuthRoute.serializer())
            subclass(RecipeListRoute::class, RecipeListRoute.serializer())
            subclass(SettingsRoute::class, SettingsRoute.serializer())
            subclass(RecipeDetailRoute::class, RecipeDetailRoute.serializer())
        }
    }
}

@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .build()
    }

    KoinApplication(application = { modules(appModule) }) {
        AppContent()
    }
}

@Composable
private fun AppContent() {
    val settings = koinInject<AppSettings>()
    val mainViewModel = koinViewModel<MainViewModel>()

    // Back stack lives outside of key(language) so that changing the language keeps the current screen.
    val backStack = rememberNavBackStack(navConfig, mainViewModel.startRoute)

    var language by remember { mutableStateOf(settings.language) }
    var themeMode by remember { mutableStateOf(settings.themeMode) }

    // Overriding the locale + key(language) re-creates the UI tree so that
    // string resources are re-read in the newly selected language.
    CompositionLocalProvider(LocalAppLocale provides language) {
        key(language) {
            RecipesTheme(themeMode) {
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator(),
                    ),
                    entryProvider = entryProvider {
                        entry<OnboardingRoute> {
                            OnboardingScreen(
                                viewModel = koinViewModel(),
                                onNavigateToAuth = { backStack.replaceAllWith(AuthRoute) },
                            )
                        }
                        entry<AuthRoute> {
                            AuthScreen(
                                viewModel = koinViewModel(),
                                onSignedIn = { backStack.replaceAllWith(RecipeListRoute) },
                            )
                        }
                        entry<RecipeListRoute> {
                            MainScaffold(
                                showSettings = false,
                                onRecipes = {},
                                onSettings = { backStack.add(SettingsRoute) },
                            ) { padding ->
                                RecipeListScreen(
                                    viewModel = koinViewModel(),
                                    onRecipeClick = { backStack.add(RecipeDetailRoute(it)) },
                                    modifier = Modifier.padding(padding),
                                )
                            }
                        }
                        entry<SettingsRoute> {
                            MainScaffold(
                                showSettings = true,
                                onRecipes = { backStack.removeLastOrNull() },
                                onSettings = {},
                            ) { padding ->
                                SettingsScreen(
                                    language = language,
                                    onLanguageChange = {
                                        log.i { "Language changed to $it" }
                                        settings.language = it
                                        language = it
                                    },
                                    themeMode = themeMode,
                                    onThemeModeChange = {
                                        settings.themeMode = it
                                        themeMode = it
                                    },
                                    onShowOnboarding = { backStack.replaceAllWith(OnboardingRoute) },
                                    modifier = Modifier.padding(padding),
                                )
                            }
                        }
                        entry<RecipeDetailRoute> { route ->
                            RecipeDetailScreen(
                                recipeId = route.recipe.id,
                                initial = route.recipe,
                                api = koinInject(),
                                onBack = { backStack.removeLastOrNull() },
                            )
                        }
                    },
                )
            }
        }
    }
}

private fun NavBackStack<NavKey>.replaceAllWith(route: Route) {
    clear()
    add(route)
}

@Composable
private fun MainScaffold(
    showSettings: Boolean,
    onRecipes: () -> Unit,
    onSettings: () -> Unit,
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                val colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                NavigationBarItem(
                    selected = !showSettings,
                    onClick = onRecipes,
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                    label = { Text(stringResource(Res.string.nav_recipes), style = MaterialTheme.typography.labelSmall) },
                    colors = colors,
                )
                NavigationBarItem(
                    selected = showSettings,
                    onClick = onSettings,
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text(stringResource(Res.string.nav_settings), style = MaterialTheme.typography.labelSmall) },
                    colors = colors,
                )
            }
        },
        content = content,
    )
}
