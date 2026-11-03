package ua.chnu.recipes.platform

import androidx.compose.runtime.Composable
import org.koin.core.module.Module

/**
 * Koin module with the dependencies that cannot be created in common code
 * (e.g. KSafe needs an Android Context on Android).
 */
@Composable
expect fun rememberPlatformModule(): Module
