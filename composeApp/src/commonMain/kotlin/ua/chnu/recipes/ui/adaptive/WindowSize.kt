package ua.chnu.recipes.ui.adaptive

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass

/** Width buckets of the Material window size classes (compact < 600dp <= medium < 840dp <= expanded). */
enum class WindowWidth { Compact, Medium, Expanded }

/** Height buckets (compact < 480dp <= medium < 900dp <= expanded). */
enum class WindowHeight { Compact, Medium, Expanded }

/** Current window size class, recalculated when the window is resized or the device is rotated. */
data class WindowSize(val width: WindowWidth, val height: WindowHeight) {
    /** Phone in landscape: wide, but too short to stack content vertically. */
    val isLandscapeCompactHeight: Boolean get() = height == WindowHeight.Compact && width != WindowWidth.Compact

    /** True when content should be laid out side by side instead of in one column. */
    val useTwoPanes: Boolean get() = width != WindowWidth.Compact
}

@Composable
fun rememberWindowSize(): WindowSize {
    val sizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val width = when {
        sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> WindowWidth.Expanded
        sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> WindowWidth.Medium
        else -> WindowWidth.Compact
    }
    val height = when {
        sizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND) -> WindowHeight.Expanded
        sizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND) -> WindowHeight.Medium
        else -> WindowHeight.Compact
    }
    return WindowSize(width, height)
}
