/*
 * Copyright (C) 2024-2025 The MetroboardBoard Contributors
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

package dev.patrickgold.metroboard.app.ext

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.patrickgold.metroboard.R
import dev.patrickgold.metroboard.app.LocalNavController
import dev.patrickgold.metroboard.app.Routes
import dev.patrickgold.metroboard.extensionManager
import dev.patrickgold.metroboard.ime.theme.ThemeExtension
import dev.patrickgold.metroboard.lib.compose.MetroboardScreen
import dev.patrickgold.metroboard.lib.ext.ExtensionManager
import org.metroboard.lib.compose.MetroboardOutlinedBox
import org.metroboard.lib.compose.MetroboardTextButton
import org.metroboard.lib.compose.MetroButtonItem
import org.metroboard.lib.compose.defaultMetroboardOutlinedBox
import org.metroboard.lib.compose.metroboardScrollbar
import org.metroboard.lib.compose.stringRes

enum class ExtensionListScreenType(
    val id: String,
    @StringRes val titleResId: Int,
    val getExtensionIndex: (ExtensionManager) -> ExtensionManager.ExtensionIndex<*>,
    val launchExtensionCreate: ((NavController) -> Unit)?,
) {
    EXT_THEME(
        id = "ext-theme",
        titleResId = R.string.ext__list__ext_theme,
        getExtensionIndex = { it.themes },
        launchExtensionCreate = { it.navigate(Routes.Ext.Edit("null", ThemeExtension.SERIAL_TYPE)) },
    ),
    EXT_KEYBOARD(
        id = "ext-keyboard",
        titleResId = R.string.ext__list__ext_keyboard,
        getExtensionIndex = { it.keyboardExtensions },
        launchExtensionCreate = null,
    ),
    EXT_LANGUAGEPACK(
        id = "ext-languagepack",
        titleResId = R.string.ext__list__ext_languagepack,
        getExtensionIndex = { it.languagePacks },
        launchExtensionCreate = null,
    );
}

@Composable
fun ExtensionListScreen(type: ExtensionListScreenType, showUpdate: Boolean) = MetroboardScreen {
    title = stringRes(type.titleResId)
    previewFieldVisible = false
    scrollable = false

    val context = LocalContext.current
    val navController = LocalNavController.current
    val extensionManager by context.extensionManager()
    val extensionIndex by type.getExtensionIndex(extensionManager).collectAsState()

    var fabHeight by remember {
        mutableStateOf(0)
    }
    val fabHeightDp = with(LocalDensity.current) { fabHeight.toDp()+16.dp }
    val listState = rememberLazyListState()

    content {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .metroboardScrollbar(state = listState, isVertical = true),
            state = listState,
            contentPadding = PaddingValues(bottom = fabHeightDp),
        ) {
            if (showUpdate) {
                item {
                    ImportExtensionBox(navController)
                }
                item {
                    UpdateBox(extensionIndex = extensionIndex)
                }
            }
            items(extensionIndex) { ext ->
                MetroboardOutlinedBox(
                    modifier = Modifier.defaultMetroboardOutlinedBox(),
                    title = ext.meta.title,
                    subtitle = ext.meta.id,
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        text = ext.meta.description ?: "",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp),
                    ) {
                        MetroboardTextButton(
                            onClick = {
                                navController.navigate(Routes.Ext.View(ext.meta.id))
                            },
                            icon = Icons.Outlined.Info,
                            text = stringRes(id = R.string.ext__list__view_details),
                            colors = ButtonDefaults.textButtonColors(),
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        MetroboardTextButton(
                            onClick = {
                                navController.navigate(Routes.Ext.Edit(ext.meta.id))
                            },
                            icon = Icons.Default.Edit,
                            text = stringRes(R.string.action__edit),
                            enabled = extensionManager.canDelete(ext),
                        )
                    }
                }
            }
        }
    }

    if (type.launchExtensionCreate != null) {
        floatingActionButton {
            MetroButtonItem(
                text = "+ " + stringRes(id = R.string.ext__editor__title_create_any),
                onClick = { type.launchExtensionCreate.invoke(navController) },
                modifier = Modifier
                    .padding(16.dp)
                    .onGloballyPositioned {
                        fabHeight = it.size.height
                    },
            )
        }
    }
}
