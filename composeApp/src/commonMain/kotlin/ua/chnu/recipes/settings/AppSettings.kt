package ua.chnu.recipes.settings

import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import ua.chnu.recipes.platform.systemLanguage

enum class ThemeMode { System, Light, Dark }

/** Languages the app is localized into. */
object Languages {
    const val UK = "uk"
    const val EN = "en"
}

/**
 * User preferences persisted with multiplatform-settings
 * (SharedPreferences on Android, NSUserDefaults on iOS, java.util.prefs on desktop).
 */
class AppSettings(private val settings: Settings = Settings()) {
    private val log = Logger.withTag("AppSettings")

    var onboardingCompleted: Boolean
        get() = settings.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        set(value) {
            log.i { "onboardingCompleted = $value" }
            settings.putBoolean(KEY_ONBOARDING_COMPLETED, value)
        }

    /** "uk" or "en". Defaults to the device language (Ukrainian if the device uses it, otherwise English). */
    var language: String
        get() = settings.getStringOrNull(KEY_LANGUAGE)
            ?: if (systemLanguage().startsWith(Languages.UK)) Languages.UK else Languages.EN
        set(value) {
            log.i { "language = $value" }
            settings.putString(KEY_LANGUAGE, value)
        }

    var themeMode: ThemeMode
        get() = settings.getStringOrNull(KEY_THEME)
            ?.let { name -> ThemeMode.entries.firstOrNull { it.name == name } }
            ?: ThemeMode.System
        set(value) {
            log.i { "themeMode = $value" }
            settings.putString(KEY_THEME, value.name)
        }

    private companion object {
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        const val KEY_LANGUAGE = "language"
        const val KEY_THEME = "theme_mode"
    }
}
