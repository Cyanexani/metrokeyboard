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

package dev.patrickgold.metroboard.app.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.patrickgold.metroboard.R
import dev.patrickgold.metroboard.app.LocalNavController
import dev.patrickgold.metroboard.app.Routes
import dev.patrickgold.metroboard.lib.compose.MetroboardScreen
import dev.patrickgold.metroboard.lib.util.InputMethodUtils
import org.metroboard.lib.compose.MetroboardErrorCard
import org.metroboard.lib.compose.MetroboardWarningCard
import org.metroboard.lib.compose.MetroNavigationPreferenceItem
import org.metroboard.lib.compose.stringRes

@Composable
fun HomeScreen() = MetroboardScreen {
    title = stringRes(R.string.settings__title)
    categoryTitle = stringRes(R.string.app_name)
    navigationIconVisible = false
    previewFieldVisible = true

    val navController = LocalNavController.current
    val context = LocalContext.current

    content {
        val isMetroboardBoardEnabled by InputMethodUtils.observeIsMetroboardEnabled(foregroundOnly = true)
        val isMetroboardBoardSelected by InputMethodUtils.observeIsMetroboardSelected(foregroundOnly = true)
        if (!isMetroboardBoardEnabled) {
            MetroboardErrorCard(
                modifier = Modifier.padding(8.dp),
                showIcon = false,
                text = stringRes(R.string.settings__home__ime_not_enabled),
                onClick = { InputMethodUtils.showImeEnablerActivity(context) },
            )
        } else if (!isMetroboardBoardSelected) {
            MetroboardWarningCard(
                modifier = Modifier.padding(8.dp),
                showIcon = false,
                text = stringRes(R.string.settings__home__ime_not_selected),
                onClick = { InputMethodUtils.showImePicker(context) },
            )
        }

        MetroNavigationPreferenceItem(
            title = stringRes(R.string.settings__localization__title),
            onClick = { navController.navigate(Routes.Settings.Localization) },
        )
        MetroNavigationPreferenceItem(
            title = stringRes(R.string.settings__theme__title),
            onClick = { navController.navigate(Routes.Settings.Theme) },
        )
        MetroNavigationPreferenceItem(
            title = stringRes(R.string.settings__keyboard__title),
            onClick = { navController.navigate(Routes.Settings.Keyboard) },
        )
        MetroNavigationPreferenceItem(
            title = stringRes(R.string.settings__smartbar__title),
            onClick = { navController.navigate(Routes.Settings.Smartbar) },
        )
        MetroNavigationPreferenceItem(
            title = stringRes(R.string.settings__typing__title),
            onClick = { navController.navigate(Routes.Settings.Typing) },
        )
        MetroNavigationPreferenceItem(
            title = stringRes(R.string.settings__gestures__title),
            onClick = { navController.navigate(Routes.Settings.Gestures) },
        )
        MetroNavigationPreferenceItem(
            title = stringRes(R.string.settings__clipboard__title),
            onClick = { navController.navigate(Routes.Settings.Clipboard) },
        )
        MetroNavigationPreferenceItem(
            title = stringRes(R.string.settings__media__title),
            onClick = { navController.navigate(Routes.Settings.Media) },
        )
        MetroNavigationPreferenceItem(
            title = stringRes(R.string.ext__home__title),
            onClick = { navController.navigate(Routes.Ext.Home) },
        )
        MetroNavigationPreferenceItem(
            title = stringRes(R.string.settings__other__title),
            onClick = { navController.navigate(Routes.Settings.Other) },
        )
        MetroNavigationPreferenceItem(
            title = stringRes(R.string.about__title),
            onClick = { navController.navigate(Routes.Settings.About) },
        )
    }
}
