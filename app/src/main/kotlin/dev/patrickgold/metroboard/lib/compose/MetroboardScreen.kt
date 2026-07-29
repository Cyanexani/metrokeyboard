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

package dev.patrickgold.metroboard.lib.compose

import android.app.Activity
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import dev.patrickgold.metroboard.app.MetroboardPreferenceModel
import dev.patrickgold.metroboard.app.MetroboardPreferenceStore
import dev.patrickgold.metroboard.app.LocalNavController
import dev.patrickgold.jetpref.datastore.ui.PreferenceLayout
import dev.patrickgold.jetpref.datastore.ui.PreferenceUiContent
import org.metroboard.lib.android.AndroidVersion
import org.metroboard.lib.compose.MetroboardAppBar
import org.metroboard.lib.compose.MetroboardIconButton
import org.metroboard.lib.compose.autoMirrorForRtl
import org.metroboard.lib.compose.metroboardVerticalScroll

@Composable
fun MetroboardScreen(builder: @Composable MetroboardScreenScope.() -> Unit) {
    val scope = remember { MetroboardScreenScopeImpl() }
    builder(scope)
    scope.Render()
}

typealias MetroboardScreenActions = @Composable RowScope.() -> Unit
typealias MetroboardScreenBottomBar = @Composable () -> Unit
typealias MetroboardScreenContent = PreferenceUiContent<MetroboardPreferenceModel>
typealias MetroboardScreenFab = @Composable () -> Unit
typealias MetroboardScreenNavigationIcon = @Composable () -> Unit

interface MetroboardScreenScope {
    var title: String

    var categoryTitle: String

    var navigationIconVisible: Boolean

    var previewFieldVisible: Boolean

    var scrollable: Boolean

    var iconSpaceReserved: Boolean

    fun actions(actions: MetroboardScreenActions)

    fun bottomBar(bottomBar: MetroboardScreenBottomBar)

    fun content(content: MetroboardScreenContent)

    fun floatingActionButton(fab: MetroboardScreenFab)

    fun navigationIcon(navigationIcon: MetroboardScreenNavigationIcon)
}

private class MetroboardScreenScopeImpl : MetroboardScreenScope {
    override var title: String by mutableStateOf("")
    override var categoryTitle: String by mutableStateOf("SETTINGS")
    override var navigationIconVisible: Boolean by mutableStateOf(false)
    override var previewFieldVisible: Boolean by mutableStateOf(false)
    override var scrollable: Boolean by mutableStateOf(true)
    override var iconSpaceReserved: Boolean by mutableStateOf(false)

    private var actions: MetroboardScreenActions = @Composable { }
    private var bottomBar: MetroboardScreenBottomBar = @Composable { }
    private var content: MetroboardScreenContent = @Composable { }
    private var fab: MetroboardScreenFab = @Composable { }
    private var navigationIcon: MetroboardScreenNavigationIcon = @Composable {
        val navController = LocalNavController.current
        MetroboardIconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.autoMirrorForRtl(),
            icon = Icons.AutoMirrored.Filled.ArrowBack,
        )
    }

    override fun actions(actions: MetroboardScreenActions) {
        this.actions = actions
    }

    override fun bottomBar(bottomBar: MetroboardScreenBottomBar) {
        this.bottomBar = bottomBar
    }

    override fun content(content: MetroboardScreenContent) {
        this.content = content
    }

    override fun floatingActionButton(fab: MetroboardScreenFab) {
        this.fab = fab
    }

    override fun navigationIcon(navigationIcon: MetroboardScreenNavigationIcon) {
        this.navigationIcon = navigationIcon
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Render() {
        val context = LocalContext.current
        val previewFieldController = LocalPreviewFieldController.current
        val colorScheme = MaterialTheme.colorScheme

        SideEffect {
            val window = (context as Activity).window
            previewFieldController?.isVisible = previewFieldVisible
            window.statusBarColor = Color.Transparent.toArgb()
            if (AndroidVersion.ATLEAST_API29_Q) {
                window.navigationBarColor = Color.Transparent.toArgb()
                window.isNavigationBarContrastEnforced = true
            } else {
                window.navigationBarColor = colorScheme.scrim.toArgb()
            }
        }

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = { MetroboardAppBar(title, categoryTitle, navigationIcon.takeIf { navigationIconVisible }, actions, scrollBehavior) },
            bottomBar = bottomBar,
            floatingActionButton = fab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
        ) { innerPadding ->
            val scrollModifier = if (scrollable) {
                Modifier.metroboardVerticalScroll()
            } else {
                Modifier
            }
            PreferenceLayout(
                MetroboardPreferenceStore,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
                    .then(scrollModifier),
                iconSpaceReserved = iconSpaceReserved,
                content = content,
            )
        }
    }
}
