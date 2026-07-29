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

package dev.patrickgold.metroboard.app.settings.smartbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import dev.patrickgold.metroboard.R
import dev.patrickgold.metroboard.app.enumDisplayEntriesOf
import dev.patrickgold.metroboard.ime.smartbar.CandidatesDisplayMode
import dev.patrickgold.metroboard.ime.smartbar.ExtendedActionsPlacement
import dev.patrickgold.metroboard.ime.smartbar.SmartbarLayout
import dev.patrickgold.metroboard.lib.compose.MetroboardScreen
import dev.patrickgold.jetpref.datastore.model.collectAsState
import dev.patrickgold.metroboard.lib.compose.MetroListPreference
import kotlinx.coroutines.launch
import org.metroboard.lib.compose.MetroCheckboxPreferenceItem
import org.metroboard.lib.compose.MetroHeader
import org.metroboard.lib.compose.MetroSwitchPreferenceItem
import org.metroboard.lib.compose.stringRes

@Composable
fun SmartbarScreen() = MetroboardScreen {
    title = stringRes(R.string.settings__smartbar__title)
    previewFieldVisible = true

    val scope = rememberCoroutineScope()

    content {
        val smartbarEnabled by prefs.smartbar.enabled.collectAsState()
        MetroSwitchPreferenceItem(
            title = stringRes(R.string.pref__smartbar__enabled__label),
            summary = stringRes(R.string.pref__smartbar__enabled__summary),
            checked = smartbarEnabled,
            onCheckedChange = { scope.launch { prefs.smartbar.enabled.set(it) } },
        )
        MetroListPreference(
            listPref = prefs.smartbar.layout,
            title = stringRes(R.string.pref__smartbar__layout__label),
            entries = enumDisplayEntriesOf(SmartbarLayout::class),
            enabledIf = { prefs.smartbar.enabled isEqualTo true },
        )

        MetroHeader(title = stringRes(R.string.pref__smartbar__group_layout_specific__label))
        MetroListPreference(
            prefs.suggestion.displayMode,
            title = stringRes(R.string.pref__suggestion__display_mode__label),
            entries = enumDisplayEntriesOf(CandidatesDisplayMode::class),
            enabledIf = { prefs.smartbar.enabled isEqualTo true },
            visibleIf = { prefs.smartbar.layout isNotEqualTo SmartbarLayout.ACTIONS_ONLY },
        )
        val flipToggles by prefs.smartbar.flipToggles.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__smartbar__flip_toggles__label),
            summary = stringRes(R.string.pref__smartbar__flip_toggles__summary),
            checked = flipToggles,
            onCheckedChange = { scope.launch { prefs.smartbar.flipToggles.set(it) } },
            enabled = smartbarEnabled,
        )
        SideEffect {
            // prefs.smartbar.sharedActionsAutoExpandCollapse.set(true)
        }
        val sharedActionsAutoExpandCollapse by prefs.smartbar.sharedActionsAutoExpandCollapse.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__smartbar__shared_actions_auto_expand_collapse__label),
            summary = "[Since v0.4.1] Always enabled due to UX issues",
            checked = sharedActionsAutoExpandCollapse,
            onCheckedChange = { },
            enabled = false,
        )
        MetroListPreference(
            listPref = prefs.smartbar.extendedActionsPlacement,
            title = stringRes(R.string.pref__smartbar__extended_actions_placement__label),
            entries = enumDisplayEntriesOf(ExtendedActionsPlacement::class),
            enabledIf = { prefs.smartbar.enabled isEqualTo true },
            visibleIf = { prefs.smartbar.layout isEqualTo SmartbarLayout.SUGGESTIONS_ACTIONS_EXTENDED },
        )
    }
}
