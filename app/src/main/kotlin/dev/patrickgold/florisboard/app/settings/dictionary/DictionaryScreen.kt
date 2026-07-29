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

package dev.patrickgold.florisboard.app.settings.dictionary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.app.LocalNavController
import dev.patrickgold.florisboard.app.Routes
import dev.patrickgold.florisboard.lib.compose.FlorisScreen
import dev.patrickgold.jetpref.datastore.model.collectAsState
import dev.patrickgold.florisboard.lib.compose.MetroNavigationPreference
import kotlinx.coroutines.launch
import org.florisboard.lib.compose.MetroSwitchPreferenceItem
import org.florisboard.lib.compose.stringRes

@Composable
fun DictionaryScreen() = FlorisScreen {
    title = stringRes(R.string.settings__dictionary__title)
    previewFieldVisible = true

    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()

    content {
        val enableSystemUserDictionary by prefs.dictionary.enableSystemUserDictionary.collectAsState()
        MetroSwitchPreferenceItem(
            title = stringRes(R.string.pref__dictionary__enable_system_user_dictionary__label),
            summary = stringRes(R.string.pref__dictionary__enable_system_user_dictionary__summary),
            checked = enableSystemUserDictionary,
            onCheckedChange = { scope.launch { prefs.dictionary.enableSystemUserDictionary.set(it) } },
        )
        MetroNavigationPreference(
            title = stringRes(R.string.pref__dictionary__manage_system_user_dictionary__label),
            summary = stringRes(R.string.pref__dictionary__manage_system_user_dictionary__summary),
            onClick = { navController.navigate(Routes.Settings.UserDictionary(UserDictionaryType.SYSTEM)) },
            enabledIf = { prefs.dictionary.enableSystemUserDictionary isEqualTo true },
        )
        val enableFlorisUserDictionary by prefs.dictionary.enableFlorisUserDictionary.collectAsState()
        MetroSwitchPreferenceItem(
            title = stringRes(R.string.pref__dictionary__enable_internal_user_dictionary__label),
            summary = stringRes(R.string.pref__dictionary__enable_internal_user_dictionary__summary),
            checked = enableFlorisUserDictionary,
            onCheckedChange = { scope.launch { prefs.dictionary.enableFlorisUserDictionary.set(it) } },
        )
        MetroNavigationPreference(
            title = stringRes(R.string.pref__dictionary__manage_floris_user_dictionary__label),
            summary = stringRes(R.string.pref__dictionary__manage_floris_user_dictionary__summary),
            onClick = { navController.navigate(Routes.Settings.UserDictionary(UserDictionaryType.FLORIS)) },
            enabledIf = { prefs.dictionary.enableFlorisUserDictionary isEqualTo true },
        )
    }
}
