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

package org.metroboard.lib.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MetroAppBarAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit,
)

/**
 * Windows Phone 8.1 Bottom Command Bar with 4 Circle Action Buttons and "..." expansion.
 */
@Composable
fun MetroBottomAppBar(
    actions: List<MetroAppBarAction>,
    modifier: Modifier = Modifier,
    menuItems: List<Pair<String, () -> Unit>> = emptyList(),
) {
    var expanded by remember { mutableStateOf(false) }
    val contentColor = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Expanded secondary menu items
        AnimatedVisibility(
            visible = expanded && menuItems.isNotEmpty(),
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                menuItems.forEach { (text, onClick) ->
                    val interactionSource = remember { MutableInteractionSource() }
                    Text(
                        text = text.lowercase(),
                        fontSize = 16.sp,
                        color = contentColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .metroPressTilt(interactionSource)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = {
                                    expanded = false
                                    onClick()
                                }
                            )
                            .sizeIn(minHeight = 44.dp)
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }
            }
        }

        // Primary action bar row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                actions.take(4).forEach { action ->
                    val interactionSource = remember { MutableInteractionSource() }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .metroPressTilt(interactionSource)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = action.onClick,
                            )
                            .sizeIn(minWidth = 44.dp, minHeight = 44.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .border(width = 2.dp, color = contentColor, shape = CircleShape)
                                .background(Color.Transparent, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = action.icon,
                                contentDescription = action.label,
                                tint = contentColor,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                        if (expanded) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = action.label.lowercase(),
                                fontSize = 11.sp,
                                color = contentColor,
                            )
                        }
                    }
                }
            }

            // "..." expansion ellipsis button
            val interactionSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .metroPressTilt(interactionSource)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { expanded = !expanded },
                    )
                    .size(44.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "more",
                    tint = contentColor,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}
