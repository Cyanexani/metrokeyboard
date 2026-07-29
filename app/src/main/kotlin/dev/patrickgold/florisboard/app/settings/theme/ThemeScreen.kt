/*
 * Copyright (C) 2021-2025 The FlorisBoard Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.patrickgold.florisboard.app.settings.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState as collectFlowAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.app.LocalNavController
import dev.patrickgold.florisboard.app.Routes
import dev.patrickgold.florisboard.app.enumDisplayEntriesOf
import dev.patrickgold.florisboard.app.ext.AddonManagementReferenceBox
import dev.patrickgold.florisboard.app.ext.ExtensionListScreenType
import dev.patrickgold.florisboard.ime.theme.ThemeMode
import dev.patrickgold.florisboard.lib.compose.FlorisScreen
import dev.patrickgold.florisboard.themeManager
import dev.patrickgold.jetpref.datastore.model.collectAsState
import dev.patrickgold.jetpref.datastore.model.LocalTime
import dev.patrickgold.jetpref.datastore.ui.ExperimentalJetPrefDatastoreUi
import dev.patrickgold.florisboard.lib.compose.MetroListPreference
import dev.patrickgold.florisboard.lib.compose.MetroNavigationPreference
import kotlinx.coroutines.launch
import org.florisboard.lib.compose.MetroAccentColorGrid
import org.florisboard.lib.compose.MetroSliderPreferenceItem
import org.florisboard.lib.compose.stringRes

@OptIn(ExperimentalJetPrefDatastoreUi::class)
@Composable
fun ThemeScreen() = FlorisScreen {
    title = stringRes(R.string.settings__theme__title)
    categoryTitle = "PERSONALIZATION"
    previewFieldVisible = true

    val context = LocalContext.current
    val navController = LocalNavController.current
    val themeManager by context.themeManager()
    val scope = rememberCoroutineScope()

    content {
        val dayThemeId by prefs.theme.dayThemeId.collectAsState()
        val nightThemeId by prefs.theme.nightThemeId.collectAsState()
        val appAccentColor by prefs.other.accentColor.collectAsState()
        val themeMode by prefs.theme.mode.collectAsState()
        val indexedThemeState = themeManager.indexedThemeConfigs.collectFlowAsState()
        val (themeMap, _) = indexedThemeState.value

        val dayThemeLabel = themeMap[dayThemeId]?.label ?: dayThemeId.toString()
        val nightThemeLabel = themeMap[nightThemeId]?.label ?: nightThemeId.toString()

        MetroListPreference(
            prefs.theme.mode,
            icon = Icons.Default.BrightnessAuto,
            title = stringRes(R.string.pref__theme__mode__label),
            entries = enumDisplayEntriesOf(ThemeMode::class),
        )
        MetroNavigationPreference(
            title = stringRes(R.string.pref__theme__day),
            summary = dayThemeLabel,
            enabledIf = { prefs.theme.mode isNotEqualTo ThemeMode.ALWAYS_NIGHT },
            onClick = {
                navController.navigate(Routes.Settings.ThemeManager(ThemeManagerScreenAction.SELECT_DAY))
            },
        )
        MetroNavigationPreference(
            title = stringRes(R.string.pref__theme__night),
            summary = nightThemeLabel,
            enabledIf = { prefs.theme.mode isNotEqualTo ThemeMode.ALWAYS_DAY },
            onClick = {
                navController.navigate(Routes.Settings.ThemeManager(ThemeManagerScreenAction.SELECT_NIGHT))
            },
        )
        val sunriseTime by prefs.theme.sunriseTime.collectAsState()
        MetroSliderPreferenceItem(
            title = stringRes(R.string.pref__theme__sunrise_time__label),
            value = (sunriseTime.hour * 60 + sunriseTime.minute).toFloat(),
            onValueChange = { rawMinutes ->
                val minutes = rawMinutes.toInt()
                scope.launch { prefs.theme.sunriseTime.set(LocalTime(minutes / 60, minutes % 60)) }
            },
            valueRange = 0f..1435f,
            steps = 286,
            valueLabel = { rawMinutes ->
                val minutes = rawMinutes.toInt()
                "%02d:%02d".format(minutes / 60, minutes % 60)
            },
            enabled = themeMode == ThemeMode.FOLLOW_TIME,
        )
        val sunsetTime by prefs.theme.sunsetTime.collectAsState()
        MetroSliderPreferenceItem(
            title = stringRes(R.string.pref__theme__sunset_time__label),
            value = (sunsetTime.hour * 60 + sunsetTime.minute).toFloat(),
            onValueChange = { rawMinutes ->
                val minutes = rawMinutes.toInt()
                scope.launch { prefs.theme.sunsetTime.set(LocalTime(minutes / 60, minutes % 60)) }
            },
            valueRange = 0f..1435f,
            steps = 286,
            valueLabel = { rawMinutes ->
                val minutes = rawMinutes.toInt()
                "%02d:%02d".format(minutes / 60, minutes % 60)
            },
            enabled = themeMode == ThemeMode.FOLLOW_TIME,
        )

        // Signature Windows Phone 8.1 Accent Color Grid — updates overall app theme live
        MetroAccentColorGrid(
            selectedColor = appAccentColor,
            onColorSelected = { newColor ->
                scope.launch {
                    prefs.other.accentColor.set(newColor)
                    prefs.theme.accentColor.set(newColor)
                }
            },
        )

        AddonManagementReferenceBox(type = ExtensionListScreenType.EXT_THEME)
    }
}
