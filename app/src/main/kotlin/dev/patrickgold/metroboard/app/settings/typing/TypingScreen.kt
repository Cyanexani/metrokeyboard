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

package dev.patrickgold.metroboard.app.settings.typing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.patrickgold.metroboard.R
import dev.patrickgold.metroboard.app.LocalNavController
import dev.patrickgold.metroboard.app.Routes
import dev.patrickgold.metroboard.app.enumDisplayEntriesOf
import dev.patrickgold.metroboard.ime.keyboard.IncognitoMode
import dev.patrickgold.metroboard.ime.nlp.SpellingLanguageMode
import dev.patrickgold.metroboard.lib.compose.MetroboardHyperlinkText
import dev.patrickgold.metroboard.lib.compose.MetroboardScreen
import dev.patrickgold.jetpref.datastore.model.collectAsState
import dev.patrickgold.jetpref.datastore.ui.ExperimentalJetPrefDatastoreUi
import dev.patrickgold.metroboard.lib.compose.MetroListPreference
import kotlinx.coroutines.launch
import org.metroboard.lib.android.AndroidVersion
import org.metroboard.lib.compose.MetroboardErrorCard
import org.metroboard.lib.compose.MetroCheckboxPreferenceItem
import org.metroboard.lib.compose.MetroHeader
import org.metroboard.lib.compose.MetroNavigationPreferenceItem
import org.metroboard.lib.compose.MetroSection
import org.metroboard.lib.compose.MetroSwitchPreferenceItem
import org.metroboard.lib.compose.stringRes

@OptIn(ExperimentalJetPrefDatastoreUi::class)
@Composable
fun TypingScreen() = MetroboardScreen {
    title = stringRes(R.string.settings__typing__title)
    previewFieldVisible = true

    val navController = LocalNavController.current

    content {
        val scope = rememberCoroutineScope()

        MetroboardErrorCard(
            modifier = Modifier.padding(8.dp),
            text = """
                Suggestions (except system autofill) and spell checking are not available in this release. All
                preferences in the "Corrections" group are properly implemented though.
            """.trimIndent().replace('\n', ' '),
        )

        MetroHeader(title = stringRes(R.string.pref__suggestion__title))

        val suggestionEnabled by prefs.suggestion.enabled.collectAsState()
        MetroSwitchPreferenceItem(
            title = stringRes(R.string.pref__suggestion__enabled__label),
            summary = stringRes(R.string.pref__suggestion__enabled__summary),
            checked = suggestionEnabled,
            onCheckedChange = { scope.launch { prefs.suggestion.enabled.set(it) } },
        )

        val blockOffensive by prefs.suggestion.blockPossiblyOffensive.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__suggestion__block_possibly_offensive__label),
            summary = stringRes(R.string.pref__suggestion__block_possibly_offensive__summary),
            checked = blockOffensive,
            onCheckedChange = { scope.launch { prefs.suggestion.blockPossiblyOffensive.set(it) } },
            enabled = suggestionEnabled,
        )

        if (AndroidVersion.ATLEAST_API30_R) {
            val inlineSuggestions by prefs.suggestion.api30InlineSuggestionsEnabled.collectAsState()
            MetroCheckboxPreferenceItem(
                title = stringRes(R.string.pref__suggestion__api30_inline_suggestions_enabled__label),
                summary = stringRes(R.string.pref__suggestion__api30_inline_suggestions_enabled__summary),
                checked = inlineSuggestions,
                onCheckedChange = { scope.launch { prefs.suggestion.api30InlineSuggestionsEnabled.set(it) } },
            )
        }

        MetroListPreference(
            prefs.suggestion.incognitoMode,
            title = stringRes(R.string.pref__suggestion__incognito_mode__label),
            entries = enumDisplayEntriesOf(IncognitoMode::class),
        )

        MetroHeader(title = stringRes(R.string.pref__correction__title))

        val autoCaps by prefs.correction.autoCapitalization.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__correction__auto_capitalization__label),
            summary = stringRes(R.string.pref__correction__auto_capitalization__summary),
            checked = autoCaps,
            onCheckedChange = { scope.launch { prefs.correction.autoCapitalization.set(it) } },
        )

        val autoSpacePunctuation by prefs.correction.autoSpacePunctuation.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__correction__auto_space_punctuation__label),
            summary = stringRes(R.string.pref__correction__auto_space_punctuation__summary),
            checked = autoSpacePunctuation,
            onCheckedChange = { scope.launch { prefs.correction.autoSpacePunctuation.set(it) } },
        )

        if (autoSpacePunctuation) {
            MetroSection(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = """
                            Auto-space after punctuation is an experimental feature which may break or behave
                            unexpectedly. If you want, please give feedback about it in below linked feedback
                            thread. This helps a lot in improving this feature. Thanks!
                        """.trimIndent().replace('\n', ' '),
                    )
                    MetroboardHyperlinkText(
                        text = "Feedback thread (GitHub)",
                        url = "https://github.com/Cyanexani/metrokeyboard/discussions/1935",
                    )
                }
            }
        }

        val rememberCapsLock by prefs.correction.rememberCapsLockState.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__correction__remember_caps_lock_state__label),
            summary = stringRes(R.string.pref__correction__remember_caps_lock_state__summary),
            checked = rememberCapsLock,
            onCheckedChange = { scope.launch { prefs.correction.rememberCapsLockState.set(it) } },
        )

        val doubleSpacePeriod by prefs.correction.doubleSpacePeriod.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__correction__double_space_period__label),
            summary = stringRes(R.string.pref__correction__double_space_period__summary),
            checked = doubleSpacePeriod,
            onCheckedChange = { scope.launch { prefs.correction.doubleSpacePeriod.set(it) } },
        )

        MetroHeader(title = stringRes(R.string.pref__spelling__title))

        val metroboardSpellCheckerEnabled = remember { mutableStateOf(false) }
        SpellCheckerServiceSelector(metroboardSpellCheckerEnabled)

        MetroListPreference(
            prefs.spelling.languageMode,
            title = stringRes(R.string.pref__spelling__language_mode__label),
            entries = enumDisplayEntriesOf(SpellingLanguageMode::class),
            enabledIf = { metroboardSpellCheckerEnabled.value },
        )

        MetroHeader(title = stringRes(R.string.settings__dictionary__title))

        MetroNavigationPreferenceItem(
            title = stringRes(R.string.settings__dictionary__title),
            onClick = { navController.navigate(Routes.Settings.Dictionary) },
        )
    }
}
