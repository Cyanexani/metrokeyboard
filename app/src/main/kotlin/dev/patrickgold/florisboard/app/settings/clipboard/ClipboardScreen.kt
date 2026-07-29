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

package dev.patrickgold.florisboard.app.settings.clipboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.app.enumDisplayEntriesOf
import dev.patrickgold.florisboard.ime.clipboard.CLIPBOARD_HISTORY_NUM_GRID_COLUMNS_AUTO
import dev.patrickgold.florisboard.ime.clipboard.ClipboardSyncBehavior
import dev.patrickgold.florisboard.lib.compose.FlorisScreen
import dev.patrickgold.jetpref.datastore.model.collectAsState
import dev.patrickgold.jetpref.datastore.ui.ExperimentalJetPrefDatastoreUi
import dev.patrickgold.florisboard.lib.compose.MetroListPreference
import kotlinx.coroutines.launch
import org.florisboard.lib.android.AndroidVersion
import org.florisboard.lib.compose.MetroCheckboxPreferenceItem
import org.florisboard.lib.compose.MetroHeader
import org.florisboard.lib.compose.MetroSliderPreferenceItem
import org.florisboard.lib.compose.MetroSwitchPreferenceItem
import org.florisboard.lib.compose.pluralsRes
import org.florisboard.lib.compose.stringRes

@OptIn(ExperimentalJetPrefDatastoreUi::class)
@Composable
fun ClipboardScreen() = FlorisScreen {
    title = stringRes(R.string.settings__clipboard__title)
    previewFieldVisible = true

    content {
        val scope = rememberCoroutineScope()

        val useInternalClipboard by prefs.clipboard.useInternalClipboard.collectAsState()
        MetroSwitchPreferenceItem(
            title = stringRes(R.string.pref__clipboard__use_internal_clipboard__label),
            summary = stringRes(R.string.pref__clipboard__use_internal_clipboard__summary),
            checked = useInternalClipboard,
            onCheckedChange = { scope.launch { prefs.clipboard.useInternalClipboard.set(it) } },
        )
        MetroListPreference(
            prefs.clipboard.syncToFloris,
            title = stringRes(R.string.pref__clipboard__sync_from_system_clipboard__label),
            entries = enumDisplayEntriesOf(ClipboardSyncBehavior::class),
            enabledIf = { prefs.clipboard.useInternalClipboard isEqualTo true },
        )
        MetroListPreference(
            prefs.clipboard.syncToSystem,
            title = stringRes(R.string.pref__clipboard__sync_to_system_clipboard__label),
            entries = enumDisplayEntriesOf(ClipboardSyncBehavior::class),
            enabledIf = { prefs.clipboard.useInternalClipboard isEqualTo true },
        )

        MetroHeader(title = stringRes(R.string.pref__clipboard__group_clipboard_suggestion__label))

        val suggestionEnabled by prefs.clipboard.suggestionEnabled.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__clipboard__suggestion_enabled__label),
            summary = stringRes(R.string.pref__clipboard__suggestion_enabled__summary),
            checked = suggestionEnabled,
            onCheckedChange = { scope.launch { prefs.clipboard.suggestionEnabled.set(it) } },
        )
        val suggestionTimeout by prefs.clipboard.suggestionTimeout.collectAsState()
        MetroSliderPreferenceItem(
            title = stringRes(R.string.pref__clipboard__suggestion_timeout__label),
            value = suggestionTimeout.toFloat(),
            onValueChange = { scope.launch { prefs.clipboard.suggestionTimeout.set(it.toInt()) } },
            valueRange = 30f..300f,
            steps = 54,
            valueLabel = { stringRes(R.string.pref__clipboard__suggestion_timeout__summary, "v" to it.toInt()) },
            enabled = suggestionEnabled,
        )

        MetroHeader(title = stringRes(R.string.pref__clipboard__group_clipboard_history__label))

        val historyEnabled by prefs.clipboard.historyEnabled.collectAsState()
        MetroSwitchPreferenceItem(
            title = stringRes(R.string.pref__clipboard__enable_clipboard_history__label),
            summary = stringRes(R.string.pref__clipboard__enable_clipboard_history__summary),
            checked = historyEnabled,
            onCheckedChange = { scope.launch { prefs.clipboard.historyEnabled.set(it) } },
        )
        val historyNumGridColumnsPortrait by prefs.clipboard.historyNumGridColumnsPortrait.collectAsState()
        MetroSliderPreferenceItem(
            title = stringRes(R.string.pref__clipboard__num_history_grid_columns__label),
            value = historyNumGridColumnsPortrait.toFloat(),
            onValueChange = { scope.launch { prefs.clipboard.historyNumGridColumnsPortrait.set(it.toInt()) } },
            valueRange = 0f..10f,
            steps = 10,
            valueLabel = { numGrid ->
                if (numGrid.toInt() == CLIPBOARD_HISTORY_NUM_GRID_COLUMNS_AUTO) {
                    stringRes(R.string.general__auto)
                } else {
                    numGrid.toInt().toString()
                }
            },
            enabled = historyEnabled,
        )
        val historyAutoCleanOldEnabled by prefs.clipboard.historyAutoCleanOldEnabled.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__clipboard__clean_up_old__label),
            checked = historyAutoCleanOldEnabled,
            onCheckedChange = { scope.launch { prefs.clipboard.historyAutoCleanOldEnabled.set(it) } },
            enabled = historyEnabled,
        )
        val historyAutoCleanOldAfter by prefs.clipboard.historyAutoCleanOldAfter.collectAsState()
        MetroSliderPreferenceItem(
            title = stringRes(R.string.pref__clipboard__clean_up_after__label),
            value = historyAutoCleanOldAfter.toFloat(),
            onValueChange = { scope.launch { prefs.clipboard.historyAutoCleanOldAfter.set(it.toInt()) } },
            valueRange = 0f..120f,
            steps = 24,
            valueLabel = { pluralsRes(R.plurals.unit__minutes__written, it.toInt(), "v" to it.toInt()) },
            enabled = historyEnabled && historyAutoCleanOldEnabled,
        )
        if (AndroidVersion.ATLEAST_API33_T) {
            val historyAutoCleanSensitiveEnabled by prefs.clipboard.historyAutoCleanSensitiveEnabled.collectAsState()
            MetroCheckboxPreferenceItem(
                title = stringRes(R.string.pref__clipboard__auto_clean_sensitive__label),
                checked = historyAutoCleanSensitiveEnabled,
                onCheckedChange = { scope.launch { prefs.clipboard.historyAutoCleanSensitiveEnabled.set(it) } },
                enabled = historyEnabled,
            )
            val historyAutoCleanSensitiveAfter by prefs.clipboard.historyAutoCleanSensitiveAfter.collectAsState()
            MetroSliderPreferenceItem(
                title = stringRes(R.string.pref__clipboard__auto_clean_sensitive_after__label),
                value = historyAutoCleanSensitiveAfter.toFloat(),
                onValueChange = { scope.launch { prefs.clipboard.historyAutoCleanSensitiveAfter.set(it.toInt()) } },
                valueRange = 0f..300f,
                steps = 30,
                valueLabel = { pluralsRes(R.plurals.unit__seconds__written, it.toInt(), "v" to it.toInt()) },
                enabled = historyEnabled && historyAutoCleanSensitiveEnabled,
            )
        }
        val historySizeLimitEnabled by prefs.clipboard.historySizeLimitEnabled.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__clipboard__limit_history_size__label),
            checked = historySizeLimitEnabled,
            onCheckedChange = { scope.launch { prefs.clipboard.historySizeLimitEnabled.set(it) } },
            enabled = historyEnabled,
        )
        val historySizeLimit by prefs.clipboard.historySizeLimit.collectAsState()
        MetroSliderPreferenceItem(
            title = stringRes(R.string.pref__clipboard__max_history_size__label),
            value = historySizeLimit.toFloat(),
            onValueChange = { scope.launch { prefs.clipboard.historySizeLimit.set(it.toInt()) } },
            valueRange = 5f..100f,
            steps = 19,
            valueLabel = { pluralsRes(R.plurals.unit__items__written, it.toInt(), "v" to it.toInt()) },
            enabled = historyEnabled && historySizeLimitEnabled,
        )

        val historyHideOnPaste by prefs.clipboard.historyHideOnPaste.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__clipboard__history_hide_on_paste__label),
            checked = historyHideOnPaste,
            onCheckedChange = { scope.launch { prefs.clipboard.historyHideOnPaste.set(it) } },
            enabled = historyEnabled,
        )
        val historyHideOnNextTextField by prefs.clipboard.historyHideOnNextTextField.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__clipboard__history_hide_on_next_text_field__label),
            checked = historyHideOnNextTextField,
            onCheckedChange = { scope.launch { prefs.clipboard.historyHideOnNextTextField.set(it) } },
            enabled = historyEnabled,
        )

        val clearPrimaryClipAffectsHistoryIfUnpinned by prefs.clipboard.clearPrimaryClipAffectsHistoryIfUnpinned.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__clipboard__clear_primary_clip_affects_history_if_unpinned__label),
            summary = stringRes(R.string.pref__clipboard__clear_primary_clip_affects_history_if_unpinned__summary),
            checked = clearPrimaryClipAffectsHistoryIfUnpinned,
            onCheckedChange = { scope.launch { prefs.clipboard.clearPrimaryClipAffectsHistoryIfUnpinned.set(it) } },
            enabled = historyEnabled,
        )
    }
}
