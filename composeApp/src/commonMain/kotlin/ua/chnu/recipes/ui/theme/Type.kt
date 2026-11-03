package ua.chnu.recipes.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import ua.chnu.recipes.resources.Res
import ua.chnu.recipes.resources.manrope_bold
import ua.chnu.recipes.resources.manrope_extrabold
import ua.chnu.recipes.resources.manrope_medium
import ua.chnu.recipes.resources.manrope_regular
import ua.chnu.recipes.resources.manrope_semibold

/** Manrope (SIL OFL) — a custom font that includes Latin and Cyrillic glyphs. */
@Composable
fun manropeFamily(): FontFamily = FontFamily(
    Font(Res.font.manrope_regular, FontWeight.Normal),
    Font(Res.font.manrope_medium, FontWeight.Medium),
    Font(Res.font.manrope_semibold, FontWeight.SemiBold),
    Font(Res.font.manrope_bold, FontWeight.Bold),
    Font(Res.font.manrope_extrabold, FontWeight.ExtraBold),
)

@Composable
fun appTypography(): Typography {
    val family = manropeFamily()
    return Typography(
        headlineLarge = TextStyle(fontFamily = family, fontWeight = FontWeight.ExtraBold, fontSize = 30.sp, lineHeight = 35.sp),
        headlineMedium = TextStyle(fontFamily = family, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, lineHeight = 34.sp),
        titleLarge = TextStyle(fontFamily = family, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, lineHeight = 26.sp),
        titleMedium = TextStyle(fontFamily = family, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 20.sp),
        bodyLarge = TextStyle(fontFamily = family, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
        bodyMedium = TextStyle(fontFamily = family, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp),
        labelLarge = TextStyle(fontFamily = family, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 18.sp),
        labelMedium = TextStyle(fontFamily = family, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, lineHeight = 17.sp),
        labelSmall = TextStyle(fontFamily = family, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp),
    )
}
