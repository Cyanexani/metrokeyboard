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

package dev.patrickgold.metroboard.ime.theme

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import dev.patrickgold.metroboard.app.MetroboardPreferenceStore
import dev.patrickgold.metroboard.ime.window.LocalWindowController
import dev.patrickgold.metroboard.keyboardManager
import dev.patrickgold.metroboard.themeManager
import dev.patrickgold.jetpref.datastore.model.collectAsState
import org.metroboard.lib.snygg.ui.ProvideSnyggTheme
import org.metroboard.lib.snygg.ui.rememberSnyggTheme

@Composable
fun MetroboardImeTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val windowController = LocalWindowController.current

    val keyboardManager by context.keyboardManager()
    val themeManager by context.themeManager()

    val prefs by MetroboardPreferenceStore
    val accentColor by prefs.theme.accentColor.collectAsState()

    val activeThemeInfo by themeManager.activeThemeInfo.collectAsState()

    val assetResolver = remember(activeThemeInfo) {
        MetroboardAssetResolver(context, activeThemeInfo)
    }
    val snyggTheme = rememberSnyggTheme(activeThemeInfo.stylesheet, assetResolver)
    val windowSpec by windowController.activeWindowSpec.collectAsState()
    val fontScale by remember { derivedStateOf { windowSpec.fontScale } }

    val state by keyboardManager.activeState.collectAsState()
    val attributes = mapOf(
        MetroboardImeUi.Attr.Mode to state.keyboardMode.toString(),
        MetroboardImeUi.Attr.ShiftState to state.inputShiftState.toString(),
    )

    MaterialTheme {
        CompositionLocalProvider(
            LocalTextStyle provides TextStyle.Default,
        ) {
            ProvideSnyggTheme(
                snyggTheme = snyggTheme,
                dynamicAccentColor = accentColor,
                fontSizeMultiplier = fontScale,
                assetResolver = assetResolver,
                rootAttributes = attributes,
                content = content,
                materialYouFlags = activeThemeInfo.config.materialYouFlags
            )
        }
    }
}
