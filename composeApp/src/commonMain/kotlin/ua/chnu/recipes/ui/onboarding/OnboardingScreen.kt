package ua.chnu.recipes.ui.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import ua.chnu.recipes.resources.Res
import ua.chnu.recipes.resources.onboarding_next
import ua.chnu.recipes.resources.onboarding_skip
import ua.chnu.recipes.resources.onboarding_start
import ua.chnu.recipes.resources.onboarding_text_1
import ua.chnu.recipes.resources.onboarding_text_2
import ua.chnu.recipes.resources.onboarding_text_3
import ua.chnu.recipes.resources.onboarding_title_1
import ua.chnu.recipes.resources.onboarding_title_2
import ua.chnu.recipes.resources.onboarding_title_3
import ua.chnu.recipes.ui.onboarding.OnboardingContract.Effect
import ua.chnu.recipes.ui.onboarding.OnboardingContract.Intent
import ua.chnu.recipes.ui.theme.Basil
import ua.chnu.recipes.ui.theme.BasilDark
import ua.chnu.recipes.ui.theme.Ink
import ua.chnu.recipes.ui.theme.Tangerine
import ua.chnu.recipes.ui.theme.Tomato

private val titles: List<StringResource> =
    listOf(Res.string.onboarding_title_1, Res.string.onboarding_title_2, Res.string.onboarding_title_3)
private val texts: List<StringResource> =
    listOf(Res.string.onboarding_text_1, Res.string.onboarding_text_2, Res.string.onboarding_text_3)

/**
 * First-launch introduction: a 3-slide HorizontalPager with dots, "Skip" and "Next / Get started".
 * Follows MVI: the screen renders [OnboardingContract.State], sends [OnboardingContract.Intent]s
 * and reacts to one-shot [OnboardingContract.Effect]s.
 */
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onNavigateToAuth: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val pagerState = rememberPagerState(pageCount = { state.pageCount })
    val currentOnNavigateToAuth by rememberUpdatedState(onNavigateToAuth)

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { viewModel.onIntent(Intent.PageChanged(it)) }
    }
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is Effect.ScrollToPage -> pagerState.animateScrollToPage(effect.page)
                Effect.NavigateToAuth -> currentOnNavigateToAuth()
            }
        }
    }
    val isLast = state.isLast

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(24.dp)
    ) {
        Row(Modifier.fillMaxWidth().height(44.dp), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { viewModel.onIntent(Intent.SkipClicked) }) {
                Text(
                    stringResource(Res.string.onboarding_skip),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            OnboardingPage(page)
        }

        Row(
            Modifier.fillMaxWidth().padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(titles.size) { index ->
                val active = index == pagerState.currentPage
                Box(
                    Modifier
                        .padding(horizontal = 4.dp)
                        .height(8.dp)
                        .width(if (active) 28.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                )
            }
        }

        Button(
            onClick = { viewModel.onIntent(Intent.NextClicked) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                stringResource(if (isLast) Res.string.onboarding_start else Res.string.onboarding_next),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun OnboardingPage(page: Int) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                when (page) {
                    0 -> BowlIllustration()
                    1 -> SearchIllustration()
                    else -> TimerIllustration()
                }
            }
        }
        Text(
            stringResource(titles[page]),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            stringResource(texts[page]),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ---- Illustrations (drawn with Canvas, same shapes as in the Claude Design mockup) ----

@Composable
private fun BowlIllustration() {
    Canvas(Modifier.size(220.dp)) {
        val s = size.width / 240f
        val bowl = Path().apply {
            moveTo(48f * s, 112f * s)
            lineTo(192f * s, 112f * s)
            arcTo(Rect(Offset(48f * s, 40f * s), Size(144f * s, 144f * s)), 0f, 180f, false)
            close()
        }
        drawPath(bowl, Basil)
        drawRoundRect(
            BasilDark, topLeft = Offset(40f * s, 104f * s), size = Size(160f * s, 12f * s),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f * s),
        )
        drawCircle(Tangerine, 14f * s, Offset(92f * s, 96f * s))
        drawCircle(Tomato, 16f * s, Offset(124f * s, 88f * s))
        drawCircle(Color(0xFF8DBF6A), 12f * s, Offset(154f * s, 98f * s))
        listOf(96f, 124f, 152f).forEachIndexed { i, x ->
            val y = if (i == 1) 56f else 60f
            val steam = Path().apply {
                moveTo(x * s, y * s)
                relativeCubicTo(-8f * s, -10f * s, 8f * s, -16f * s, 0f, -28f * s)
            }
            drawPath(steam, Tangerine, style = Stroke(width = 5f * s, cap = StrokeCap.Round))
        }
    }
}

@Composable
private fun SearchIllustration() {
    Column(Modifier.width(220.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                "Margherita",
                modifier = Modifier.padding(start = 20.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(true, false, false).forEach { selected ->
                Box(
                    Modifier.height(30.dp).width(62.dp).clip(RoundedCornerShape(15.dp))
                        .background(if (selected) Basil else MaterialTheme.colorScheme.surface)
                )
            }
        }
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(Tangerine))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(Modifier.height(10.dp).width(100.dp).clip(CircleShape).background(Ink))
                Box(Modifier.height(8.dp).width(64.dp).clip(CircleShape).background(MaterialTheme.colorScheme.outline))
            }
        }
    }
}

@Composable
private fun TimerIllustration() {
    Canvas(Modifier.size(220.dp)) {
        val s = size.width / 240f
        drawRoundRect(
            Basil, topLeft = Offset(98f * s, 26f * s), size = Size(44f * s, 16f * s),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f * s),
        )
        val center = Offset(120f * s, 128f * s)
        drawCircle(Color.White, 84f * s, center)
        drawCircle(Basil, 84f * s, center, style = Stroke(width = 12f * s))
        val stroke = 9f * s
        drawLine(Ink, center, Offset(120f * s, 80f * s), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(Ink, center, Offset(156f * s, 150f * s), strokeWidth = stroke, cap = StrokeCap.Round)
        drawCircle(Tangerine, 9f * s, center)
    }
}
