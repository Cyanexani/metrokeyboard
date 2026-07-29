/*
 * Copyright (C) 2025-2026 The MetroboardBoard Contributors
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

package dev.patrickgold.metroboard.ime.window

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.unit.LayoutDirection
import dev.patrickgold.metroboard.MetroboardImeService
import dev.patrickgold.metroboard.R
import dev.patrickgold.metroboard.ime.input.LocalInputFeedbackController
import dev.patrickgold.metroboard.ime.theme.MetroboardImeTheme
import org.metroboard.lib.compose.ProvideLocalizedResources

/**
 * Provides the [ImeWindowController] instance this composition tree is associated with.
 */
val LocalWindowController = staticCompositionLocalOf<ImeWindowController> {
    error("This composition local provider is only available within an IME view")
}

/**
 * The main entry point and bridge between the IME dialog view and the composables. It will fill the maximum area
 * available within the accompanying dialog view, and also draw under system bars.
 *
 * The layout direction will be forced to [LayoutDirection.Ltr], to ensure the window positioning logic's left/right
 * corresponds to the physical left/right. For UI components that need to conform to the actual system layout
 * direction, the UI components should be wrapped with [org.metroboard.lib.compose.ProvideActualLayoutDirection].
 *
 * @see ImeRootWindow
 */
@SuppressLint("ViewConstructor")
class ImeRootView(val ims: MetroboardImeService) : AbstractComposeView(ims) {
    init {
        isHapticFeedbackEnabled = true
        layoutParams = LayoutParams(
            /* width = */ LayoutParams.MATCH_PARENT,
            /* height = */ LayoutParams.MATCH_PARENT,
        )
    }

    @Composable
    override fun Content() {
        CompositionLocalProvider(
            LocalInputFeedbackController provides ims.inputFeedbackController,
            LocalWindowController provides ims.windowController,
        ) {
            ProvideLocalizedResources(
                resourcesContext = ims.resourcesContext,
                appName = R.string.app_name,
                forceLayoutDirection = LayoutDirection.Ltr,
            ) {
                MetroboardImeTheme {
                    ImeRootWindow()
                }
            }
        }
    }

    override fun getAccessibilityClassName(): CharSequence? {
        return this::class.simpleName
    }
}
