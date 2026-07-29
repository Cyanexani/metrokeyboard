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

package dev.patrickgold.florisboard.app.devtools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.app.LocalNavController
import dev.patrickgold.florisboard.app.Routes
import dev.patrickgold.florisboard.extensionManager
import dev.patrickgold.florisboard.ime.dictionary.DictionaryManager
import dev.patrickgold.florisboard.ime.dictionary.FlorisUserDictionaryDatabase
import dev.patrickgold.florisboard.ime.smartbar.quickaction.QuickActionArrangement
import dev.patrickgold.florisboard.lib.compose.FlorisConfirmDeleteDialog
import dev.patrickgold.florisboard.lib.compose.FlorisScreen
import dev.patrickgold.jetpref.datastore.model.collectAsState
import dev.patrickgold.jetpref.datastore.ui.Preference
import kotlinx.coroutines.launch
import org.florisboard.lib.android.AndroidSettings
import org.florisboard.lib.android.AndroidVersion
import org.florisboard.lib.android.showLongToast
import org.florisboard.lib.compose.MetroCheckboxPreferenceItem
import org.florisboard.lib.compose.MetroHeader
import org.florisboard.lib.compose.MetroSwitchPreferenceItem
import org.florisboard.lib.compose.stringRes

class DebugOnPurposeCrashException : Exception(
    "Success! The app crashed purposely to display this beautiful screen we all love :)"
)

@Composable
fun DevtoolsScreen() = FlorisScreen {
    title = stringRes(R.string.devtools__title)
    previewFieldVisible = true

    val context = LocalContext.current
    val navController = LocalNavController.current
    val extensionManager by context.extensionManager()
    val scope = rememberCoroutineScope()

    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }

    content {
        val devtoolsEnabled by prefs.devtools.enabled.collectAsState()
        MetroSwitchPreferenceItem(
            title = stringRes(R.string.devtools__enabled__label),
            summary = stringRes(R.string.devtools__enabled__summary),
            checked = devtoolsEnabled,
            onCheckedChange = { scope.launch { prefs.devtools.enabled.set(it) } },
        )

        MetroHeader(title = stringRes(R.string.devtools__title))
        val showPrimaryClip by prefs.devtools.showPrimaryClip.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.devtools__show_primary_clip__label),
            summary = stringRes(R.string.devtools__show_primary_clip__summary),
            checked = showPrimaryClip,
            onCheckedChange = { scope.launch { prefs.devtools.showPrimaryClip.set(it) } },
            enabled = devtoolsEnabled,
        )
        val showInputStateOverlay by prefs.devtools.showInputStateOverlay.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.devtools__show_input_state_overlay__label),
            summary = stringRes(R.string.devtools__show_input_state_overlay__summary),
            checked = showInputStateOverlay,
            onCheckedChange = { scope.launch { prefs.devtools.showInputStateOverlay.set(it) } },
            enabled = devtoolsEnabled,
        )
        val showSpellingOverlay by prefs.devtools.showSpellingOverlay.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.devtools__show_spelling_overlay__label),
            summary = stringRes(R.string.devtools__show_spelling_overlay__summary),
            checked = showSpellingOverlay,
            onCheckedChange = { scope.launch { prefs.devtools.showSpellingOverlay.set(it) } },
            enabled = devtoolsEnabled,
        )
        if (AndroidVersion.ATLEAST_API30_R) {
            val showInlineAutofillOverlay by prefs.devtools.showInlineAutofillOverlay.collectAsState()
            MetroCheckboxPreferenceItem(
                title = stringRes(R.string.devtools__show_inline_autofill_overlay__label),
                summary = stringRes(R.string.devtools__show_inline_autofill_overlay__summary),
                checked = showInlineAutofillOverlay,
                onCheckedChange = { scope.launch { prefs.devtools.showInlineAutofillOverlay.set(it) } },
                enabled = devtoolsEnabled,
            )
        }
        val showKeyTouchBoundaries by prefs.devtools.showKeyTouchBoundaries.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.devtools__show_key_touch_boundaries__label),
            summary = stringRes(R.string.devtools__show_key_touch_boundaries__summary),
            checked = showKeyTouchBoundaries,
            onCheckedChange = { scope.launch { prefs.devtools.showKeyTouchBoundaries.set(it) } },
            enabled = devtoolsEnabled,
        )
        val showDragAndDropHelpers by prefs.devtools.showDragAndDropHelpers.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.devtools__show_drag_and_drop_helpers__label),
            summary = stringRes(R.string.devtools__show_drag_and_drop_helpers__summary),
            checked = showDragAndDropHelpers,
            onCheckedChange = { scope.launch { prefs.devtools.showDragAndDropHelpers.set(it) } },
            enabled = devtoolsEnabled,
        )
        Preference(
            title = stringRes(R.string.devtools__clear_udm_internal_database__label),
            summary = stringRes(R.string.devtools__clear_udm_internal_database__summary),
            onClick = { setShowDialog(true) },
            enabledIf = { prefs.devtools.enabled isEqualTo true },
        )
        Preference(
            title = stringRes(R.string.devtools__reset_quick_actions_to_default__label),
            summary = stringRes(R.string.devtools__reset_quick_actions_to_default__summary),
            onClick = {
                scope.launch {
                    prefs.smartbar.actionArrangement.set(QuickActionArrangement.Default)
                    context.showLongToast(R.string.devtools__reset_quick_actions_to_default__toast_success)
                }
            },
            enabledIf = { prefs.devtools.enabled isEqualTo true },
        )
        Preference(
            title = stringRes(R.string.devtools__reset_flag__label, "flag_name" to "isImeSetUp"),
            summary = stringRes(R.string.devtools__reset_flag_is_ime_set_up__summary),
            onClick = { scope.launch { prefs.internal.isImeSetUp.set(false) } },
            enabledIf = { prefs.devtools.enabled isEqualTo true },
        )
        Preference(
            title = stringRes(R.string.devtools__test_crash_report__label),
            summary = stringRes(R.string.devtools__test_crash_report__summary),
            onClick = { throw DebugOnPurposeCrashException() },
            enabledIf = { prefs.devtools.enabled isEqualTo true },
        )
        Preference(
            title = "Debug log",
            summary = "View and export the debug log",
            onClick = { navController.navigate(Routes.Devtools.ExportDebugLog) },
            enabledIf = { prefs.devtools.enabled isEqualTo true },
        )
        val glideEnabled by prefs.glide.enabled.collectAsState()
        MetroSwitchPreferenceItem(
            title = "prefs.glide.enabled (debug)",
            summary = if (glideEnabled) "This impacts your performance and may trigger the all keys invisible bug!" else "Recommended to keep this off!",
            checked = glideEnabled,
            onCheckedChange = { scope.launch { prefs.glide.enabled.set(it) } },
            enabled = devtoolsEnabled,
        )

        MetroHeader(title = stringRes(R.string.devtools__group_ime_window_tools__title))
        val showWindowResizeHandleBoundaries by prefs.devtools.showWindowResizeHandleBoundaries.collectAsState()
        MetroCheckboxPreferenceItem(
            title = stringRes(R.string.devtools__show_window_resize_handle_boundaries__label),
            summary = stringRes(R.string.devtools__show_window_resize_handle_boundaries__summary),
            checked = showWindowResizeHandleBoundaries,
            onCheckedChange = { scope.launch { prefs.devtools.showWindowResizeHandleBoundaries.set(it) } },
            enabled = devtoolsEnabled,
        )
        Preference(
            title = stringRes(R.string.devtools__reset_window_config__label),
            summary = stringRes(R.string.devtools__reset_window_config__summary),
            onClick = {
                scope.launch {
                    prefs.keyboard.windowConfig.reset().fold(
                        onSuccess = {
                            context.showLongToast(R.string.devtools__reset_window_config__toast_success)
                        },
                        onFailure = { error ->
                            context.showLongToast(
                                R.string.devtools__reset_window_config__toast_failure,
                                "message" to "${error.localizedMessage}",
                            )
                        },
                    )
                }
            },
            enabledIf = { prefs.devtools.enabled isEqualTo true },
        )

        MetroHeader(title = stringRes(R.string.devtools__group_android__title))
        Preference(
            title = stringRes(R.string.devtools__android_settings_global__title),
            onClick = {
                navController.navigate(
                    Routes.Devtools.AndroidSettings(AndroidSettings.Global.groupId)
                )
            },
            enabledIf = { prefs.devtools.enabled isEqualTo true },
        )
        Preference(
            title = stringRes(R.string.devtools__android_settings_secure__title),
            onClick = {
                navController.navigate(
                    Routes.Devtools.AndroidSettings(AndroidSettings.Secure.groupId)
                )
            },
            enabledIf = { prefs.devtools.enabled isEqualTo true },
        )
        Preference(
            title = stringRes(R.string.devtools__android_settings_system__title),
            onClick = {
                navController.navigate(
                    Routes.Devtools.AndroidSettings(AndroidSettings.System.groupId)
                )
            },
            enabledIf = { prefs.devtools.enabled isEqualTo true },
        )
        Preference(
            title = stringRes(R.string.devtools__android_locales__title),
            onClick = { navController.navigate(Routes.Devtools.AndroidLocales) },
            enabledIf = { prefs.devtools.enabled isEqualTo true },
        )

        MetroHeader(title = "prefs.internal.version*")
        val versionOnInstall by prefs.internal.versionOnInstall.collectAsState()
        Preference(
            title = "prefs.internal.versionOnInstall",
            summary = versionOnInstall,
        )
        val versionLastUse by prefs.internal.versionLastUse.collectAsState()
        Preference(
            title = "prefs.internal.versionLastUse",
            summary = versionLastUse,
        )
        val versionLastChangelog by prefs.internal.versionLastChangelog.collectAsState()
        Preference(
            title = "prefs.internal.versionLastChangelog",
            summary = versionLastChangelog,
        )

        MetroHeader(title = "ExtensionManager index paths")
        Preference(
            title = "keyboardExtensions",
            summary = extensionManager.keyboardExtensions.internalModuleDir.absolutePath,
            onClick = {
                scope.launch {
                    context.showLongToast(extensionManager.keyboardExtensions.internalModuleDir.absolutePath)
                }
            },
        )
        Preference(
            title = "themes",
            summary = extensionManager.themes.internalModuleDir.absolutePath,
            onClick = {
                scope.launch {
                    context.showLongToast(extensionManager.themes.internalModuleDir.absolutePath)
                }
            },
        )

        if (showDialog) {
            FlorisConfirmDeleteDialog(
                onConfirm = {
                    DictionaryManager.default().let {
                        it.loadUserDictionariesIfNecessary()
                        it.florisUserDictionaryDao()?.deleteAll()
                    }
                    setShowDialog(false)
                },
                onDismiss = { setShowDialog(false) },
                what = FlorisUserDictionaryDatabase.DB_FILE_NAME,
            )
        }
    }
}
