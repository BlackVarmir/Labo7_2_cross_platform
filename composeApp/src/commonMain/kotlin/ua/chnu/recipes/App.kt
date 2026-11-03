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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import org.jetbrains.compose.resources.stringResource
import ua.chnu.recipes.data.Recipe
import ua.chnu.recipes.data.RecipesApi
import ua.chnu.recipes.platform.LocalAppLocale
import ua.chnu.recipes.platform.PlatformBackHandler
import ua.chnu.recipes.resources.Res
import ua.chnu.recipes.resources.nav_recipes
import ua.chnu.recipes.resources.nav_settings
import ua.chnu.recipes.settings.AppSettings
import ua.chnu.recipes.ui.RecipesViewModel
import ua.chnu.recipes.ui.detail.RecipeDetailScreen
import ua.chnu.recipes.ui.list.RecipeListScreen
import ua.chnu.recipes.ui.onboarding.OnboardingScreen
import ua.chnu.recipes.ui.settings.SettingsScreen
import ua.chnu.recipes.ui.theme.RecipesTheme

private sealed interface Screen {
    data object Onboarding : Screen
    data object RecipeList : Screen
    data class Detail(val recipe: Recipe) : Screen
    data object Settings : Screen
}

private val log = Logger.withTag("App")

@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .build()
    }

    val settings = remember { AppSettings() }
    val api = remember { RecipesApi() }
    val scope = rememberCoroutineScope()
    val recipesViewModel = remember { RecipesViewModel(api, scope) }

    var language by remember { mutableStateOf(settings.language) }
    var themeMode by remember { mutableStateOf(settings.themeMode) }
    var screen by remember {
        mutableStateOf<Screen>(if (settings.onboardingCompleted) Screen.RecipeList else Screen.Onboarding)
    }

    PlatformBackHandler(enabled = screen is Screen.Detail || screen is Screen.Settings) {
        screen = Screen.RecipeList
    }

    // Overriding the locale + key(language) re-creates the UI tree so that
    // string resources are re-read in the newly selected language.
    CompositionLocalProvider(LocalAppLocale provides language) {
        key(language) {
            RecipesTheme(themeMode) {
                when (val current = screen) {
                    Screen.Onboarding -> OnboardingScreen(
                        onFinish = {
                            settings.onboardingCompleted = true
                            screen = Screen.RecipeList
                        }
                    )

                    is Screen.Detail -> RecipeDetailScreen(
                        recipeId = current.recipe.id,
                        initial = current.recipe,
                        api = api,
                        onBack = { screen = Screen.RecipeList },
                    )

                    Screen.RecipeList, Screen.Settings -> MainScaffold(
                        showSettings = current == Screen.Settings,
                        onNavigate = { screen = it },
                    ) { padding ->
                        if (current == Screen.Settings) {
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
                                onShowOnboarding = { screen = Screen.Onboarding },
                                modifier = Modifier.padding(padding),
                            )
                        } else {
                            RecipeListScreen(
                                viewModel = recipesViewModel,
                                onRecipeClick = { screen = Screen.Detail(it) },
                                modifier = Modifier.padding(padding),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MainScaffold(
    showSettings: Boolean,
    onNavigate: (Screen) -> Unit,
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
                    onClick = { onNavigate(Screen.RecipeList) },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                    label = { Text(stringResource(Res.string.nav_recipes), style = MaterialTheme.typography.labelSmall) },
                    colors = colors,
                )
                NavigationBarItem(
                    selected = showSettings,
                    onClick = { onNavigate(Screen.Settings) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text(stringResource(Res.string.nav_settings), style = MaterialTheme.typography.labelSmall) },
                    colors = colors,
                )
            }
        },
        content = content,
    )
}
