/*
 * Copyright (C) 2025 The MetroboardBoard Contributors
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

import android.content.Intent
import android.content.res.Configuration
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import dev.patrickgold.metroboard.R
import dev.patrickgold.metroboard.lib.compose.MetroboardScreen
import dev.patrickgold.jetpref.datastore.model.collectAsState
import kotlinx.coroutines.launch
import org.metroboard.lib.compose.MetroSwitchPreferenceItem
import org.metroboard.lib.compose.MetroNavigationPreferenceItem
import org.metroboard.lib.compose.stringRes

@Composable
fun PhysicalKeyboardScreen() = MetroboardScreen {
    title = stringRes(R.string.physical_keyboard__title)

    val context = LocalContext.current
    val physicalKeyboardAttached by remember {
        mutableStateOf(context.resources.configuration.keyboard != Configuration.KEYBOARD_NOKEYS)
    }

    val activityForResult = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { }

    val scope = rememberCoroutineScope()

    content {
        if (physicalKeyboardAttached) {
            MetroNavigationPreferenceItem(
                title = stringRes(R.string.physical_keyboard__system_settings__title),
                summary = stringRes(R.string.physical_keyboard__system_settings__summary),
                onClick = {
                    activityForResult.launch(Intent(Settings.ACTION_HARD_KEYBOARD_SETTINGS))
                }
            )
        } else {
            MetroNavigationPreferenceItem(
                title = stringRes(R.string.physical_keyboard__system_settings__title),
                summary = stringRes(R.string.physical_keyboard__system_settings__summary_not_attached),
                onClick = { },
                enabled = false,
            )
        }
        val showOnScreenKeyboard by prefs.physicalKeyboard.showOnScreenKeyboard.collectAsState()
        MetroSwitchPreferenceItem(
            title = stringRes(R.string.physical_keyboard__show_on_screen_keyboard__title),
            summary = stringRes(R.string.physical_keyboard__show_on_screen_keyboard__summary),
            checked = showOnScreenKeyboard,
            onCheckedChange = { scope.launch { prefs.physicalKeyboard.showOnScreenKeyboard.set(it) } },
        )
    }
}
