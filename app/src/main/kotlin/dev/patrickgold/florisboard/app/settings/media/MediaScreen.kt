/*
 * Copyright (C) 2024-2025 The FlorisBoard Contributors
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

package dev.patrickgold.florisboard.app.settings.media

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.app.FlorisPreferenceStore
import dev.patrickgold.florisboard.app.enumDisplayEntriesOf
import dev.patrickgold.florisboard.ime.media.emoji.EmojiHistory
import dev.patrickgold.florisboard.ime.media.emoji.EmojiHistoryHelper
import dev.patrickgold.florisboard.ime.media.emoji.EmojiSkinTone
import dev.patrickgold.florisboard.ime.media.emoji.EmojiSuggestionType
import dev.patrickgold.florisboard.lib.compose.FlorisScreen
import dev.patrickgold.jetpref.datastore.model.collectAsState
import dev.patrickgold.jetpref.datastore.ui.ExperimentalJetPrefDatastoreUi
import dev.patrickgold.florisboard.lib.compose.MetroListPreference
import dev.patrickgold.jetpref.material.ui.JetPrefAlertDialog
import kotlinx.coroutines.launch
import org.florisboard.lib.compose.MetroHeader
import org.florisboard.lib.compose.MetroButtonItem
import org.florisboard.lib.compose.MetroCheckboxPreferenceItem
import org.florisboard.lib.compose.MetroSliderPreferenceItem
import org.florisboard.lib.compose.metroDialogEnterAnimation
import org.florisboard.lib.compose.MetroSwitchPreferenceItem
import org.florisboard.lib.compose.pluralsRes
import org.florisboard.lib.compose.stringRes

@OptIn(ExperimentalJetPrefDatastoreUi::class)
@Composable
fun MediaScreen() = FlorisScreen {
    title = stringRes(R.string.settings__media__title)
    previewFieldVisible = true
    iconSpaceReserved = false

    val prefs by FlorisPreferenceStore

    var shouldDelete by remember { mutableStateOf<ShouldDelete?>(null) }
    val scope = rememberCoroutineScope()

    content {
        MetroListPreference(
            prefs.emoji.preferredSkinTone,
            title = stringRes(R.string.prefs__media__emoji_preferred_skin_tone),
            entries = enumDisplayEntriesOf(EmojiSkinTone::class),
        )

        MetroHeader(title = stringRes(R.string.prefs__media__emoji_history__title))
        val emojiHistoryEnabled by prefs.emoji.historyEnabled.collectAsState()
        MetroSwitchPreferenceItem(
            title = stringRes(R.string.prefs__media__emoji_history_enabled),
            summary = stringRes(R.string.prefs__media__emoji_history_enabled__summary),
            checked = emojiHistoryEnabled,
            onCheckedChange = { scope.launch { prefs.emoji.historyEnabled.set(it) } },
        )
        MetroListPreference(
            prefs.emoji.historyPinnedUpdateStrategy,
            title = stringRes(R.string.prefs__media__emoji_history_pinned_update_strategy),
            entries = enumDisplayEntriesOf(EmojiHistory.UpdateStrategy::class),
            enabledIf = { prefs.emoji.historyEnabled.isTrue() },
        )
        MetroListPreference(
            prefs.emoji.historyRecentUpdateStrategy,
            title = stringRes(R.string.prefs__media__emoji_history_recent_update_strategy),
            entries = enumDisplayEntriesOf(EmojiHistory.UpdateStrategy::class),
            enabledIf = { prefs.emoji.historyEnabled.isTrue() },
        )
        val historyRecentMaxSize by prefs.emoji.historyRecentMaxSize.collectAsState()
        MetroSliderPreferenceItem(
            title = stringRes(R.string.prefs__media__emoji_history_max_size),
            value = historyRecentMaxSize.toFloat(),
            onValueChange = { scope.launch { prefs.emoji.historyRecentMaxSize.set(it.toInt()) } },
            valueRange = 0f..120f,
            steps = 120,
            valueLabel = { maxSize ->
                if (maxSize.toInt() == EmojiHistory.MaxSizeUnlimited) {
                    stringRes(R.string.general__unlimited)
                } else {
                    pluralsRes(R.plurals.unit__items__written, maxSize.toInt(), "v" to maxSize.toInt())
                }
            },
            enabled = emojiHistoryEnabled,
        )
        MetroButtonItem(
            text = stringRes(R.string.prefs__media__emoji_history_pinned_reset),
            onClick = {
                shouldDelete = ShouldDelete(true)
            },
            enabled = emojiHistoryEnabled,
        )
        MetroButtonItem(
            text = stringRes(R.string.prefs__media__emoji_history_reset),
            onClick = {
                shouldDelete = ShouldDelete(false)
            },
            enabled = emojiHistoryEnabled,
        )

        MetroHeader(title = stringRes(R.string.prefs__media__emoji_suggestion__title))
        val emojiSuggestionEnabled by prefs.emoji.suggestionEnabled.collectAsState()
        MetroSwitchPreferenceItem(
            title = stringRes(R.string.prefs__media__emoji_suggestion_enabled),
            summary = stringRes(R.string.prefs__media__emoji_suggestion_enabled__summary),
            checked = emojiSuggestionEnabled,
            onCheckedChange = { scope.launch { prefs.emoji.suggestionEnabled.set(it) } },
        )
        MetroListPreference(
            prefs.emoji.suggestionType,
            title = stringRes(R.string.prefs__media__emoji_suggestion_type),
            entries = enumDisplayEntriesOf(EmojiSuggestionType::class),
            enabledIf = { prefs.emoji.suggestionEnabled.isTrue() },
        )
        val emojiSuggestionUpdateHistory by prefs.emoji.suggestionUpdateHistory.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.prefs__media__emoji_suggestion_update_history),
            summary = stringRes(R.string.prefs__media__emoji_suggestion_update_history__summary),
            checked = emojiSuggestionUpdateHistory,
            onCheckedChange = { scope.launch { prefs.emoji.suggestionUpdateHistory.set(it) } },
            enabled = emojiSuggestionEnabled && emojiHistoryEnabled,
        )
        val emojiSuggestionCandidateShowName by prefs.emoji.suggestionCandidateShowName.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.prefs__media__emoji_suggestion_candidate_show_name),
            summary = stringRes(R.string.prefs__media__emoji_suggestion_candidate_show_name__summary),
            checked = emojiSuggestionCandidateShowName,
            onCheckedChange = { scope.launch { prefs.emoji.suggestionCandidateShowName.set(it) } },
            enabled = emojiSuggestionEnabled,
        )
        val suggestionQueryMinLength by prefs.emoji.suggestionQueryMinLength.collectAsState()
        MetroSliderPreferenceItem(
            title = stringRes(R.string.prefs__media__emoji_suggestion_query_min_length),
            value = suggestionQueryMinLength.toFloat(),
            onValueChange = { scope.launch { prefs.emoji.suggestionQueryMinLength.set(it.toInt()) } },
            valueRange = 1f..5f,
            steps = 4,
            valueLabel = { length ->
                pluralsRes(R.plurals.unit__characters__written, length.toInt(), "v" to length.toInt())
            },
            enabled = emojiSuggestionEnabled,
        )
        val suggestionCandidateMaxCount by prefs.emoji.suggestionCandidateMaxCount.collectAsState()
        MetroSliderPreferenceItem(
            title = stringRes(R.string.prefs__media__emoji_suggestion_candidate_max_count),
            value = suggestionCandidateMaxCount.toFloat(),
            onValueChange = { scope.launch { prefs.emoji.suggestionCandidateMaxCount.set(it.toInt()) } },
            valueRange = 1f..10f,
            steps = 9,
            valueLabel = { count ->
                pluralsRes(R.plurals.unit__candidates__written, count.toInt(), "v" to count.toInt())
            },
            enabled = emojiSuggestionEnabled,
        )
    }

    DeleteEmojiHistoryConfirmDialog(
        shouldDelete = shouldDelete,
        onDismiss = {
            shouldDelete = null
        },
        onConfirm = {
            shouldDelete?.let {
                scope.launch {
                    if (it.pinned) {
                        EmojiHistoryHelper.deletePinned(prefs = prefs)
                    } else {
                        EmojiHistoryHelper.deleteHistory(prefs = prefs)
                    }
                }
                shouldDelete = null
            }
        },
    )
}

@Composable
fun DeleteEmojiHistoryConfirmDialog(
    shouldDelete: ShouldDelete?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    shouldDelete?.let {
        JetPrefAlertDialog(
            modifier = Modifier.metroDialogEnterAnimation(),
            title = stringRes(R.string.action__reset_confirm_title),
            confirmLabel = stringRes(R.string.action__yes),
            dismissLabel = stringRes(R.string.action__no),
            onDismiss = onDismiss,
            onConfirm = onConfirm,
        ) {
            if (it.pinned) {
                Text(stringRes(R.string.action__reset_confirm_message, "name" to "pinned emojis"))
            } else {
                Text(stringRes(R.string.action__reset_confirm_message, "name" to "emoji history"))
            }

        }
    }
}

data class ShouldDelete(val pinned: Boolean)
