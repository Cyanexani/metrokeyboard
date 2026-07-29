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
import androidx.compose.ui.platform.LocalContext
import dev.patrickgold.metroboard.R
import dev.patrickgold.metroboard.app.enumDisplayEntriesOf
import dev.patrickgold.metroboard.ime.input.HapticVibrationMode
import dev.patrickgold.metroboard.ime.input.InputFeedbackActivationMode
import dev.patrickgold.metroboard.lib.compose.MetroboardScreen
import dev.patrickgold.jetpref.datastore.model.collectAsState
import dev.patrickgold.jetpref.datastore.ui.ExperimentalJetPrefDatastoreUi
import dev.patrickgold.metroboard.lib.compose.MetroListPreference
import kotlinx.coroutines.launch
import org.metroboard.lib.android.systemVibratorOrNull
import org.metroboard.lib.android.vibrate
import org.metroboard.lib.compose.MetroCheckboxPreferenceItem
import org.metroboard.lib.compose.MetroHeader
import org.metroboard.lib.compose.MetroSliderPreferenceItem
import org.metroboard.lib.compose.stringRes

@OptIn(ExperimentalJetPrefDatastoreUi::class)
@Composable
fun InputFeedbackScreen() = MetroboardScreen {
    title = stringRes(R.string.settings__input_feedback__title)
    previewFieldVisible = true
    iconSpaceReserved = false

    val context = LocalContext.current
    val vibrator = context.systemVibratorOrNull()

    content {
        val scope = rememberCoroutineScope()

        MetroHeader(title = stringRes(R.string.pref__input_feedback__group_audio__label))
        MetroListPreference(
            listPref = prefs.inputFeedback.audioActivationMode,
            switchPref = prefs.inputFeedback.audioEnabled,
            title = stringRes(R.string.pref__input_feedback__audio_enabled__label),
            summarySwitchDisabled = stringRes(R.string.pref__input_feedback__audio_enabled__summary_disabled),
            entries = enumDisplayEntriesOf(InputFeedbackActivationMode::class, "audio"),
        )
        val audioVolume by prefs.inputFeedback.audioVolume.collectAsState()
        val audioEnabled by prefs.inputFeedback.audioEnabled.collectAsState()
        MetroSliderPreferenceItem(
            title = stringRes(R.string.pref__input_feedback__audio_volume__label),
            value = audioVolume.toFloat(),
            onValueChange = { scope.launch { prefs.inputFeedback.audioVolume.set(it.toInt()) } },
            valueRange = 1f..100f,
            steps = 99,
            valueLabel = { stringRes(R.string.unit__percent__symbol, "v" to it.toInt()) },
            enabled = audioEnabled,
        )

        val audioKeyPress by prefs.inputFeedback.audioFeatKeyPress.collectAsState()

        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__input_feedback__audio_feat_key_press__label),
            summary = stringRes(R.string.pref__input_feedback__any_feat_key_press__summary),
            checked = audioKeyPress,
            onCheckedChange = { scope.launch { prefs.inputFeedback.audioFeatKeyPress.set(it) } },
            enabled = audioEnabled,
        )

        val audioKeyLongPress by prefs.inputFeedback.audioFeatKeyLongPress.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__input_feedback__audio_feat_key_long_press__label),
            summary = stringRes(R.string.pref__input_feedback__any_feat_key_long_press__summary),
            checked = audioKeyLongPress,
            onCheckedChange = { scope.launch { prefs.inputFeedback.audioFeatKeyLongPress.set(it) } },
            enabled = audioEnabled,
        )

        val audioKeyRepeatedAction by prefs.inputFeedback.audioFeatKeyRepeatedAction.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__input_feedback__audio_feat_key_repeated_action__label),
            summary = stringRes(R.string.pref__input_feedback__any_feat_key_repeated_action__summary),
            checked = audioKeyRepeatedAction,
            onCheckedChange = { scope.launch { prefs.inputFeedback.audioFeatKeyRepeatedAction.set(it) } },
            enabled = audioEnabled,
        )

        val audioGestureSwipe by prefs.inputFeedback.audioFeatGestureSwipe.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__input_feedback__audio_feat_gesture_swipe__label),
            summary = stringRes(R.string.pref__input_feedback__any_feat_gesture_swipe__summary),
            checked = audioGestureSwipe,
            onCheckedChange = { scope.launch { prefs.inputFeedback.audioFeatGestureSwipe.set(it) } },
            enabled = audioEnabled,
        )

        val audioGestureMovingSwipe by prefs.inputFeedback.audioFeatGestureMovingSwipe.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__input_feedback__audio_feat_gesture_moving_swipe__label),
            summary = stringRes(R.string.pref__input_feedback__audio_feat_gesture_moving_swipe__label),
            checked = audioGestureMovingSwipe,
            onCheckedChange = { scope.launch { prefs.inputFeedback.audioFeatGestureMovingSwipe.set(it) } },
            enabled = audioEnabled,
        )

        MetroHeader(title = stringRes(R.string.pref__input_feedback__group_haptic__label))
        MetroListPreference(
            listPref = prefs.inputFeedback.hapticActivationMode,
            switchPref = prefs.inputFeedback.hapticEnabled,
            title = stringRes(R.string.pref__input_feedback__haptic_enabled__label),
            summarySwitchDisabled = stringRes(R.string.pref__input_feedback__haptic_enabled__summary_disabled),
            entries = enumDisplayEntriesOf(InputFeedbackActivationMode::class, "haptic"),
        )
        MetroListPreference(
            prefs.inputFeedback.hapticVibrationMode,
            title = stringRes(R.string.pref__input_feedback__haptic_vibration_mode__label),
            entries = enumDisplayEntriesOf(HapticVibrationMode::class),
            enabledIf = { prefs.inputFeedback.hapticEnabled isEqualTo true },
        )
        val hapticVibrationDuration by prefs.inputFeedback.hapticVibrationDuration.collectAsState()
        val hapticEnabled by prefs.inputFeedback.hapticEnabled.collectAsState()
        val hapticVibrationMode by prefs.inputFeedback.hapticVibrationMode.collectAsState()
        MetroSliderPreferenceItem(
            title = stringRes(R.string.pref__input_feedback__haptic_vibration_duration__label),
            value = hapticVibrationDuration.toFloat(),
            onValueChange = { duration ->
                scope.launch { prefs.inputFeedback.hapticVibrationDuration.set(duration.toInt()) }
                val strength = prefs.inputFeedback.hapticVibrationStrength.get()
                vibrator?.vibrate(duration.toInt(), strength)
            },
            valueRange = 1f..100f,
            steps = 99,
            valueLabel = { stringRes(R.string.unit__milliseconds__symbol, "v" to it.toInt()) },
            enabled = hapticEnabled && hapticVibrationMode == HapticVibrationMode.USE_VIBRATOR_DIRECTLY && vibrator != null && vibrator.hasVibrator(),
        )
        val hapticVibrationStrength by prefs.inputFeedback.hapticVibrationStrength.collectAsState()
        MetroSliderPreferenceItem(
            title = stringRes(R.string.pref__input_feedback__haptic_vibration_strength__label),
            value = hapticVibrationStrength.toFloat(),
            onValueChange = { strength ->
                scope.launch { prefs.inputFeedback.hapticVibrationStrength.set(strength.toInt()) }
                val duration = prefs.inputFeedback.hapticVibrationDuration.get()
                vibrator?.vibrate(duration, strength.toInt())
            },
            valueRange = 1f..100f,
            steps = 99,
            valueLabel = { stringRes(R.string.unit__percent__symbol, "v" to it.toInt()) },
            enabled = hapticEnabled && hapticVibrationMode == HapticVibrationMode.USE_VIBRATOR_DIRECTLY && vibrator != null && vibrator.hasVibrator() && vibrator.hasAmplitudeControl(),
        )

        val hapticKeyPress by prefs.inputFeedback.hapticFeatKeyPress.collectAsState()

        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__input_feedback__haptic_feat_key_press__label),
            summary = stringRes(R.string.pref__input_feedback__any_feat_key_press__summary),
            checked = hapticKeyPress,
            onCheckedChange = { scope.launch { prefs.inputFeedback.hapticFeatKeyPress.set(it) } },
            enabled = hapticEnabled,
        )

        val hapticKeyLongPress by prefs.inputFeedback.hapticFeatKeyLongPress.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__input_feedback__haptic_feat_key_long_press__label),
            summary = stringRes(R.string.pref__input_feedback__any_feat_key_long_press__summary),
            checked = hapticKeyLongPress,
            onCheckedChange = { scope.launch { prefs.inputFeedback.hapticFeatKeyLongPress.set(it) } },
            enabled = hapticEnabled,
        )

        val hapticKeyRepeatedAction by prefs.inputFeedback.hapticFeatKeyRepeatedAction.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__input_feedback__haptic_feat_key_repeated_action__label),
            summary = stringRes(R.string.pref__input_feedback__any_feat_key_repeated_action__summary),
            checked = hapticKeyRepeatedAction,
            onCheckedChange = { scope.launch { prefs.inputFeedback.hapticFeatKeyRepeatedAction.set(it) } },
            enabled = hapticEnabled,
        )

        val hapticGestureSwipe by prefs.inputFeedback.hapticFeatGestureSwipe.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__input_feedback__haptic_feat_gesture_swipe__label),
            summary = stringRes(R.string.pref__input_feedback__any_feat_gesture_swipe__summary),
            checked = hapticGestureSwipe,
            onCheckedChange = { scope.launch { prefs.inputFeedback.hapticFeatGestureSwipe.set(it) } },
            enabled = hapticEnabled,
        )

        val hapticGestureMovingSwipe by prefs.inputFeedback.hapticFeatGestureMovingSwipe.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.pref__input_feedback__haptic_feat_gesture_moving_swipe__label),
            summary = stringRes(R.string.pref__input_feedback__haptic_feat_gesture_moving_swipe__label),
            checked = hapticGestureMovingSwipe,
            onCheckedChange = { scope.launch { prefs.inputFeedback.hapticFeatGestureMovingSwipe.set(it) } },
            enabled = hapticEnabled,
        )
    }
}
