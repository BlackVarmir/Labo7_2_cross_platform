package ua.chnu.recipes.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Palette from the Claude Design mockup: basil green + tangerine accent.
val Basil = Color(0xFF2E6B4F)
val BasilDark = Color(0xFF1F4D38)
val BasilTint = Color(0xFFDCEBE1)
val Tangerine = Color(0xFFF28C28)
val Ink = Color(0xFF1B2620)
val InkMuted = Color(0xFF566860)
val Paper = Color(0xFFF6F8F4)
val Hairline = Color(0xFFDCE4DD)
val Tomato = Color(0xFFE8553D)

val LightColors: ColorScheme = lightColorScheme(
    primary = Basil,
    onPrimary = Color.White,
    primaryContainer = BasilTint,
    onPrimaryContainer = BasilDark,
    secondary = Color(0xFF9A5A16),
    onSecondary = Color.White,
    tertiary = Tangerine,
    onTertiary = Ink,
    background = Paper,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = BasilTint,
    onSurfaceVariant = InkMuted,
    outline = Hairline,
    error = Tomato,
    onError = Color.White,
)

val DarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFF8FD0AB),
    onPrimary = Color(0xFF0F2A1D),
    primaryContainer = BasilDark,
    onPrimaryContainer = BasilTint,
    secondary = Color(0xFFF2B26B),
    onSecondary = Color(0xFF3A2008),
    tertiary = Color(0xFFF2A65A),
    onTertiary = Color(0xFF2A1604),
    background = Color(0xFF101713),
    onBackground = Color(0xFFE5EDE7),
    surface = Color(0xFF18221C),
    onSurface = Color(0xFFE5EDE7),
    surfaceVariant = Color(0xFF24332A),
    onSurfaceVariant = Color(0xFFA9B8AE),
    outline = Color(0xFF2E3B33),
    error = Color(0xFFFF8A75),
    onError = Color(0xFF3A0B03),
)
