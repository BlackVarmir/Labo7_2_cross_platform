package ua.chnu.recipes.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.preferredLanguages

actual fun systemLanguage(): String =
    (NSLocale.preferredLanguages.firstOrNull() as? String)?.substringBefore('-') ?: "en"

actual object LocalAppLocale {
    private val default = NSLocale.preferredLanguages.firstOrNull() as? String ?: "en"
    private val LocalLocaleTag = staticCompositionLocalOf { default }

    actual val current: String
        @Composable get() = LocalLocaleTag.current

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val new = value ?: default
        val defaults = NSUserDefaults.standardUserDefaults
        if (value == null) {
            defaults.removeObjectForKey("AppleLanguages")
        } else {
            defaults.setObject(listOf(new), "AppleLanguages")
        }
        return LocalLocaleTag.provides(new)
    }
}

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS uses the swipe-back gesture of the host; the on-screen back button is used inside the app.
}
