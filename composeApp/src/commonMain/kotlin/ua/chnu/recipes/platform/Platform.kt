package ua.chnu.recipes.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue

/** Language code of the device ("uk", "en", ...). */
expect fun systemLanguage(): String

/**
 * Lets the app override the UI locale at runtime so that compose-resources
 * (values/ and values-uk/) pick the language chosen in Settings.
 * Pass null to go back to the system language.
 */
expect object LocalAppLocale {
    val current: String @Composable get
    @Composable infix fun provides(value: String?): ProvidedValue<*>
}

/** System back button / gesture handling (no-op where there is no system back). */
@Composable
expect fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit)
