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

package dev.patrickgold.metroboard.app.settings.keyboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import dev.patrickgold.metroboard.R
import dev.patrickgold.metroboard.app.LocalNavController
import dev.patrickgold.metroboard.app.Routes
import dev.patrickgold.metroboard.app.enumDisplayEntriesOf
import dev.patrickgold.metroboard.ime.input.CapitalizationBehavior
import dev.patrickgold.metroboard.ime.keyboard.SpaceBarMode
import dev.patrickgold.metroboard.ime.landscapeinput.LandscapeInputUiMode
import dev.patrickgold.metroboard.ime.smartbar.IncognitoDisplayMode
import dev.patrickgold.metroboard.ime.text.key.KeyHintMode
import dev.patrickgold.metroboard.ime.text.key.UtilityKeyAction
import dev.patrickgold.metroboard.lib.compose.MetroboardScreen
import dev.patrickgold.jetpref.datastore.model.collectAsState
import dev.patrickgold.jetpref.datastore.ui.ExperimentalJetPrefDatastoreUi
import dev.patrickgold.metroboard.lib.compose.MetroListPreference
import kotlinx.coroutines.launch
import org.metroboard.lib.compose.MetroCheckboxPreferenceItem
import org.metroboard.lib.compose.MetroHeader
import org.metroboard.lib.compose.MetroSliderPreferenceItem
import org.metroboard.lib.compose.MetroSwitchPreferenceItem
import org.metroboard.lib.compose.stringRes

@OptIn(ExperimentalJetPrefDatastoreUi::class)
@Composable
fun KeyboardScreen() = MetroboardScreen {
    title = stringRes(R.string.settings__keyboard__title)
    previewFieldVisible = true

    val navController = LocalNavController.current

    content {
        val scope = rememberCoroutineScope()

        val numberRow by prefs.keyboard.numberRow.collectAsState()
        MetroSwitchPreferenceItem(
            title = stringRes(R.string.pref__keyboard__number_row__label),
            summary = stringRes(R.string.pref__keyboard__number_row__summary),
            checked = numberRow,
            onCheckedChange = { scope.launch { prefs.keyboard.numberRow.set(it) } },
        )
        MetroListPreference(
            listPref = prefs.keyboard.hintedNumberRowMode,
            switchPref = prefs.keyboard.hintedNumberRowEnabled,
            title = stringRes(R.string.pref__keyboard__hinted_number_row_mode__label),
            summarySwitchDisabled = stringRes(R.string.state__disabled),
            entries = enumDisplayEntriesOf(KeyHintMode::class),
            isMajorFeatureGate = false,
            enabledIf = { prefs.keyboard.numberRow.isFalse() }
        )
        MetroListPreference(
            listPref = prefs.keyboard.hintedSymbolsMode,
            switchPref = prefs.keyboard.hintedSymbolsEnabled,
            title = stringRes(R.string.pref__keyboard__hinted_symbols_mode__label),
            summarySwitchDisabled = stringRes(R.string.state__disabled),
            entries = enumDisplayEntriesOf(KeyHintMode::class),
            isMajorFeatureGate = false,
        )
        val utilityKeyEnabled by prefs.keyboard.utilityKeyEnabled.collectAsState()
        MetroSwitchPreferenceItem(
            title = stringRes(R.string.pref__keyboard__utility_key_enabled__label),
            summary = stringRes(R.string.pref__keyboard__utility_key_enabled__summary),
            checked = utilityKeyEnabled,
            onCheckedChange = { scope.launch { prefs.keyboard.utilityKeyEnabled.set(it) } },
        )
        MetroListPreference(
            prefs.keyboard.utilityKeyAction,
            title = stringRes(R.string.pref__keyboard__utility_key_action__label),
            entries = enumDisplayEntriesOf(UtilityKeyAction::class),
            visibleIf = { prefs.keyboard.utilityKeyEnabled isEqualTo true },
        )
        MetroListPreference(
            prefs.keyboard.spaceBarMode,
            title = stringRes(R.string.pref__keyboard__space_bar_mode__label),
            entries = enumDisplayEntriesOf(SpaceBarMode::class),
        )
        MetroListPreference(
            prefs.keyboard.capitalizationBehavior,
            title = stringRes(R.string.pref__keyboard__capitalization_behavior__label),
            entries = enumDisplayEntriesOf(CapitalizationBehavior::class),
        )
        MetroHeader(title = stringRes(R.string.pref__keyboard__font_size_multiplier__label))
        val portraitFontSize by prefs.keyboard.fontSizeMultiplierPortrait.collectAsState()
        MetroSliderPreferenceItem(
            title = stringRes(R.string.screen_orientation__portrait),
            value = portraitFontSize.toFloat(),
            onValueChange = { scope.launch { prefs.keyboard.fontSizeMultiplierPortrait.set(it.toInt()) } },
            valueLabel = { stringRes(R.string.unit__percent__symbol, "v" to it.toInt()) },
            valueRange = 50f..150f,
            steps = 19,
        )
        val landscapeFontSize by prefs.keyboard.fontSizeMultiplierLandscape.collectAsState()
        MetroSliderPreferenceItem(
            title = stringRes(R.string.screen_orientation__landscape),
            value = landscapeFontSize.toFloat(),
            onValueChange = { scope.launch { prefs.keyboard.fontSizeMultiplierLandscape.set(it.toInt()) } },
            valueLabel = { stringRes(R.string.unit__percent__symbol, "v" to it.toInt()) },
            valueRange = 50f..150f,
            steps = 19,
        )
        MetroListPreference(
            listPref = prefs.keyboard.incognitoDisplayMode,
            title = stringRes(R.string.pref__keyboard__incognito_indicator__label),
            entries = enumDisplayEntriesOf(IncognitoDisplayMode::class),
        )

        MetroHeader(title = stringRes(R.string.pref__keyboard__group_layout__label))
        MetroListPreference(
            prefs.keyboard.landscapeInputUiMode,
            title = stringRes(R.string.pref__keyboard__landscape_input_ui_mode__label),
            entries = enumDisplayEntriesOf(LandscapeInputUiMode::class),
        )
        MetroHeader(title = stringRes(R.string.pref__keyboard__key_spacing__label))
        val verticalKeySpacing by prefs.keyboard.keySpacingVertical.collectAsState()
        MetroSliderPreferenceItem(
            title = stringRes(R.string.screen_orientation__vertical),
            value = verticalKeySpacing.toFloat(),
            onValueChange = { scope.launch { prefs.keyboard.keySpacingVertical.set(it.toInt()) } },
            valueLabel = { stringRes(R.string.unit__percent__symbol, "v" to it.toInt()) },
            valueRange = 50f..150f,
            steps = 19,
        )
        val horizontalKeySpacing by prefs.keyboard.keySpacingHorizontal.collectAsState()
        MetroSliderPreferenceItem(
            title = stringRes(R.string.screen_orientation__horizontal),
            value = horizontalKeySpacing.toFloat(),
            onValueChange = { scope.launch { prefs.keyboard.keySpacingHorizontal.set(it.toInt()) } },
            valueLabel = { stringRes(R.string.unit__percent__symbol, "v" to it.toInt()) },
            valueRange = 50f..150f,
            steps = 19,
        )

        MetroHeader(title = stringRes(R.string.pref__keyboard__group_keypress__label))
        org.metroboard.lib.compose.MetroNavigationPreferenceItem(
            title = stringRes(R.string.settings__input_feedback__title),
            onClick = { navController.navigate(Routes.Settings.InputFeedback) },
        )
        val popupEnabled by prefs.keyboard.popupEnabled.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__keyboard__popup_enabled__label),
            summary = stringRes(R.string.pref__keyboard__popup_enabled__summary),
            checked = popupEnabled,
            onCheckedChange = { scope.launch { prefs.keyboard.popupEnabled.set(it) } },
        )
        val mergeHintPopups by prefs.keyboard.mergeHintPopupsEnabled.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__keyboard__merge_hint_popups_enabled__label),
            summary = stringRes(R.string.pref__keyboard__merge_hint_popups_enabled__summary),
            checked = mergeHintPopups,
            onCheckedChange = { scope.launch { prefs.keyboard.mergeHintPopupsEnabled.set(it) } },
        )
        val longPressDelay by prefs.keyboard.longPressDelay.collectAsState()
        MetroSliderPreferenceItem(
            title = stringRes(R.string.pref__keyboard__long_press_delay__label),
            value = longPressDelay.toFloat(),
            onValueChange = { scope.launch { prefs.keyboard.longPressDelay.set(it.toInt()) } },
            valueRange = 100f..700f,
            steps = 60,
            valueLabel = { stringRes(R.string.unit__milliseconds__symbol, "v" to it.toInt()) },
        )
        val spaceBarSwitches by prefs.keyboard.spaceBarSwitchesToCharacters.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__keyboard__space_bar_switches_to_characters__label),
            summary = stringRes(R.string.pref__keyboard__space_bar_switches_to_characters__summary),
            checked = spaceBarSwitches,
            onCheckedChange = { scope.launch { prefs.keyboard.spaceBarSwitchesToCharacters.set(it) } },
        )
    }
}
