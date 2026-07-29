/*
 * Copyright (C) 2021-2025 The MetroboardBoard Contributors
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

package dev.patrickgold.metroboard.app.settings.advanced

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import dev.patrickgold.metroboard.R
import dev.patrickgold.metroboard.app.AppTheme
import dev.patrickgold.metroboard.app.LocalNavController
import dev.patrickgold.metroboard.app.Routes
import dev.patrickgold.metroboard.app.enumDisplayEntriesOf
import dev.patrickgold.metroboard.ime.core.DisplayLanguageNamesIn
import dev.patrickgold.metroboard.lib.MetroboardLocale
import dev.patrickgold.metroboard.lib.compose.MetroboardScreen
import dev.patrickgold.jetpref.datastore.model.collectAsState
import dev.patrickgold.metroboard.lib.compose.MetroListPreference
import dev.patrickgold.jetpref.datastore.ui.listPrefEntries
import kotlinx.coroutines.launch
import org.metroboard.lib.android.AndroidVersion
import org.metroboard.lib.compose.MetroAccentColorGrid
import org.metroboard.lib.compose.MetroCheckboxPreferenceItem
import org.metroboard.lib.compose.MetroHeader
import org.metroboard.lib.compose.MetroNavigationPreferenceItem
import org.metroboard.lib.compose.stringRes

@Composable
fun OtherScreen() = MetroboardScreen {
    title = stringRes(R.string.settings__other__title)
    previewFieldVisible = false

    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()

    content {
        MetroListPreference(
            prefs.other.settingsTheme,
            icon = Icons.Default.Palette,
            title = stringRes(R.string.pref__other__settings_theme__label),
            entries = enumDisplayEntriesOf(AppTheme::class),
        )
        val appAccentColor by prefs.other.accentColor.collectAsState()
        MetroAccentColorGrid(
            selectedColor = appAccentColor,
            onColorSelected = { newColor ->
                scope.launch {
                    prefs.other.accentColor.set(newColor)
                    prefs.theme.accentColor.set(newColor)
                }
            },
        )
        MetroListPreference(
            prefs.other.settingsLanguage,
            icon = Icons.Default.Language,
            title = stringRes(R.string.pref__other__settings_language__label),
            entries = listPrefEntries {
                listOf(
                    "auto",
                    "ar",
                    "bg",
                    "bs",
                    "ca",
                    "ckb",
                    "cs",
                    "da",
                    "de",
                    "el",
                    "en",
                    "eo",
                    "es",
                    "fa",
                    "fi",
                    "fr",
                    "hr",
                    "hu",
                    "in",
                    "it",
                    "iw",
                    "ja",
                    "ko-KR",
                    "ku",
                    "lv-LV",
                    "mk",
                    "nds-DE",
                    "nl",
                    "no",
                    "pl",
                    "pt",
                    "pt-BR",
                    "ru",
                    "sk",
                    "sl",
                    "sr",
                    "sv",
                    "tr",
                    "uk",
                    "zgh",
                    "zh-CN",
                ).map { languageTag ->
                    if (languageTag == "auto") {
                        entry(
                            key = "auto",
                            label = stringRes(R.string.settings__system_default),
                        )
                    } else {
                        val displayLanguageNamesIn by prefs.localization.displayLanguageNamesIn.collectAsState()
                        val locale = MetroboardLocale.fromTag(languageTag)
                        entry(locale.languageTag(), when (displayLanguageNamesIn) {
                            DisplayLanguageNamesIn.SYSTEM_LOCALE -> locale.displayName()
                            DisplayLanguageNamesIn.NATIVE_LOCALE -> locale.displayName(locale)
                        })
                    }
                }
            }
        )

        val showAppIcon by prefs.other.showAppIcon.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__other__show_app_icon__label),
            checked = showAppIcon,
            onCheckedChange = { newValue ->
                scope.launch { prefs.other.showAppIcon.set(newValue) }
            },
            summary = when {
                AndroidVersion.ATLEAST_API29_Q -> stringRes(R.string.pref__other__show_app_icon__summary_atleast_q)
                else -> null
            },
            enabled = AndroidVersion.ATMOST_API28_P,
        )

        MetroNavigationPreferenceItem(
            title = stringRes(R.string.physical_keyboard__title),
            onClick = { navController.navigate(Routes.Settings.PhysicalKeyboard) },
        )
        MetroNavigationPreferenceItem(
            title = stringRes(R.string.devtools__title),
            onClick = { navController.navigate(Routes.Devtools.Home) },
        )

        MetroHeader(title = stringRes(R.string.backup_and_restore__title))
        MetroNavigationPreferenceItem(
            onClick = { navController.navigate(Routes.Settings.Backup) },
            title = stringRes(R.string.backup_and_restore__back_up__title),
            summary = stringRes(R.string.backup_and_restore__back_up__summary),
        )
        MetroNavigationPreferenceItem(
            onClick = { navController.navigate(Routes.Settings.Restore) },
            title = stringRes(R.string.backup_and_restore__restore__title),
            summary = stringRes(R.string.backup_and_restore__restore__summary),
        )
    }
}
