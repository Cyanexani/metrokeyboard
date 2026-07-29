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

package dev.patrickgold.metroboard.app.settings.gestures

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.patrickgold.metroboard.R
import dev.patrickgold.metroboard.app.enumDisplayEntriesOf
import dev.patrickgold.metroboard.ime.text.gestures.SwipeAction
import dev.patrickgold.metroboard.lib.compose.MetroboardScreen
import dev.patrickgold.jetpref.datastore.model.collectAsState
import dev.patrickgold.jetpref.datastore.ui.ExperimentalJetPrefDatastoreUi
import dev.patrickgold.metroboard.lib.compose.MetroListPreference
import kotlinx.coroutines.launch
import org.metroboard.lib.compose.MetroboardInfoCard
import org.metroboard.lib.compose.MetroHeader
import org.metroboard.lib.compose.MetroSliderPreferenceItem
import org.metroboard.lib.compose.stringRes

@OptIn(ExperimentalJetPrefDatastoreUi::class)
@Composable
fun GesturesScreen() = MetroboardScreen {
    title = stringRes(R.string.settings__gestures__title)
    previewFieldVisible = true

    val scope = rememberCoroutineScope()

    content {
        MetroboardInfoCard(
            modifier = Modifier.padding(8.dp),
            text = """
                Glide typing is currently not available and will be re-implemented from the ground up with word suggestions & the new keyboard layout engine. DO NOT file an issue for this missing functionality.
            """.trimIndent()
        )

        MetroHeader(title = stringRes(R.string.pref__gestures__general_title))
        MetroListPreference(
            prefs.gestures.swipeUp,
            title = stringRes(R.string.pref__gestures__swipe_up__label),
            entries = enumDisplayEntriesOf(SwipeAction::class, "general"),
            enabledIf = { prefs.glide.enabled isEqualTo false },
        )
        MetroListPreference(
            prefs.gestures.swipeDown,
            title = stringRes(R.string.pref__gestures__swipe_down__label),
            entries = enumDisplayEntriesOf(SwipeAction::class, "general"),
            enabledIf = { prefs.glide.enabled isEqualTo false },
        )
        MetroListPreference(
            prefs.gestures.swipeLeft,
            title = stringRes(R.string.pref__gestures__swipe_left__label),
            entries = enumDisplayEntriesOf(SwipeAction::class, "general"),
            enabledIf = { prefs.glide.enabled isEqualTo false },
        )
        MetroListPreference(
            prefs.gestures.swipeRight,
            title = stringRes(R.string.pref__gestures__swipe_right__label),
            entries = enumDisplayEntriesOf(SwipeAction::class, "general"),
            enabledIf = { prefs.glide.enabled isEqualTo false },
        )

        MetroHeader(title = stringRes(R.string.pref__gestures__space_bar_title))
        MetroListPreference(
            prefs.gestures.spaceBarSwipeUp,
            title = stringRes(R.string.pref__gestures__space_bar_swipe_up__label),
            entries = enumDisplayEntriesOf(SwipeAction::class, "general"),
        )
        MetroListPreference(
            prefs.gestures.spaceBarSwipeLeft,
            title = stringRes(R.string.pref__gestures__space_bar_swipe_left__label),
            entries = enumDisplayEntriesOf(SwipeAction::class, "general"),
        )
        MetroListPreference(
            prefs.gestures.spaceBarSwipeRight,
            title = stringRes(R.string.pref__gestures__space_bar_swipe_right__label),
            entries = enumDisplayEntriesOf(SwipeAction::class, "general"),
        )
        MetroListPreference(
            prefs.gestures.spaceBarLongPress,
            title = stringRes(R.string.pref__gestures__space_bar_long_press__label),
            entries = enumDisplayEntriesOf(SwipeAction::class, "general"),
        )

        MetroHeader(title = stringRes(R.string.pref__gestures__other_title))
        MetroListPreference(
            prefs.gestures.deleteKeySwipeLeft,
            title = stringRes(R.string.pref__gestures__delete_key_swipe_left__label),
            entries = enumDisplayEntriesOf(SwipeAction::class, "deleteSwipe"),
        )
        MetroListPreference(
            prefs.gestures.deleteKeyLongPress,
            title = stringRes(R.string.pref__gestures__delete_key_long_press__label),
            entries = enumDisplayEntriesOf(SwipeAction::class, "deleteLongPress"),
        )
        val velocityThreshold by prefs.gestures.swipeVelocityThreshold.collectAsState()
        MetroSliderPreferenceItem(
            title = stringRes(R.string.pref__gestures__swipe_velocity_threshold__label),
            value = velocityThreshold.toFloat(),
            onValueChange = { scope.launch { prefs.gestures.swipeVelocityThreshold.set(it.toInt()) } },
            valueRange = 400f..4000f,
            steps = 36,
            valueLabel = { stringRes(R.string.unit__display_pixel_per_seconds__symbol, "v" to it.toInt()) },
        )
        val distanceThreshold by prefs.gestures.swipeDistanceThreshold.collectAsState()
        MetroSliderPreferenceItem(
            title = stringRes(R.string.pref__gestures__swipe_distance_threshold__label),
            value = distanceThreshold.toFloat(),
            onValueChange = { scope.launch { prefs.gestures.swipeDistanceThreshold.set(it.toInt()) } },
            valueRange = 12f..72f,
            steps = 60,
            valueLabel = { stringRes(R.string.unit__display_pixel__symbol, "v" to it.toInt()) },
        )
    }
}
