package ua.chnu.recipes.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import ua.chnu.recipes.resources.Res
import ua.chnu.recipes.resources.language_en
import ua.chnu.recipes.resources.language_uk
import ua.chnu.recipes.resources.settings_about
import ua.chnu.recipes.resources.settings_language
import ua.chnu.recipes.resources.settings_show_onboarding
import ua.chnu.recipes.resources.settings_theme
import ua.chnu.recipes.resources.settings_title
import ua.chnu.recipes.resources.settings_version
import ua.chnu.recipes.resources.theme_dark
import ua.chnu.recipes.resources.theme_light
import ua.chnu.recipes.resources.theme_system
import ua.chnu.recipes.settings.Languages
import ua.chnu.recipes.settings.ThemeMode

private const val APP_VERSION = "1.0.0"

@Composable
fun SettingsScreen(
    language: String,
    onLanguageChange: (String) -> Unit,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    onShowOnboarding: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Spacer(Modifier.height(0.dp))
        Text(
            stringResource(Res.string.settings_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )

        SettingsGroup(stringResource(Res.string.settings_language)) {
            Column(Modifier.selectableGroup()) {
                RadioRow(stringResource(Res.string.language_uk), language == Languages.UK) { onLanguageChange(Languages.UK) }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                RadioRow(stringResource(Res.string.language_en), language == Languages.EN) { onLanguageChange(Languages.EN) }
            }
        }

        SettingsGroup(stringResource(Res.string.settings_theme)) {
            Column(Modifier.selectableGroup()) {
                val options: List<Pair<ThemeMode, StringResource>> = listOf(
                    ThemeMode.System to Res.string.theme_system,
                    ThemeMode.Light to Res.string.theme_light,
                    ThemeMode.Dark to Res.string.theme_dark,
                )
                options.forEachIndexed { index, (mode, label) ->
                    if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    RadioRow(stringResource(label), themeMode == mode) { onThemeModeChange(mode) }
                }
            }
        }

        SettingsGroup(stringResource(Res.string.settings_about)) {
            Column {
                Row(
                    Modifier.fillMaxWidth().heightIn(min = 56.dp).clickable(onClick = onShowOnboarding).padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(stringResource(Res.string.settings_show_onboarding), style = MaterialTheme.typography.titleMedium)
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Row(
                    Modifier.fillMaxWidth().heightIn(min = 56.dp).padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(stringResource(Res.string.settings_version), style = MaterialTheme.typography.titleMedium)
                    Text(APP_VERSION, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun SettingsGroup(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 10.dp),
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) { content() }
    }
}

@Composable
private fun RadioRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        RadioButton(selected = selected, onClick = null)
    }
}
