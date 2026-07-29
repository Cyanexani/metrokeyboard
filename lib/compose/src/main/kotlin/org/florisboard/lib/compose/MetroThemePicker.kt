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

package org.florisboard.lib.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Signature Windows Phone 8.1 Accent Color Grid.
 *
 * 20 Authentic WP 8.1 Metro Accent Colors in square tiles.
 * Tapping a tile updates the app's primary accent color immediately.
 */
val WP81AccentColors = listOf(
    Color(0xFF0050EF) to "Cobalt",
    Color(0xFF1BA1E2) to "Cyan",
    Color(0xFF00ABA9) to "Teal",
    Color(0xFF008A00) to "Green",
    Color(0xFF60A917) to "Lime",
    Color(0xFF00A896) to "Emerald",
    Color(0xFFFA6800) to "Orange",
    Color(0xFFF0A30A) to "Amber",
    Color(0xFFE51400) to "Red",
    Color(0xFFA20025) to "Crimson",
    Color(0xFFD80073) to "Magenta",
    Color(0xFFF06B9C) to "Pink",
    Color(0xFF6A00FF) to "Indigo",
    Color(0xFFAA00FF) to "Violet",
    Color(0xFF76608A) to "Mauve",
    Color(0xFF825A2C) to "Brown",
    Color(0xFF6D8764) to "Olive",
    Color(0xFF647687) to "Steel",
    Color(0xFF87794E) to "Taupe",
    Color(0xFF000000) to "Black",
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MetroAccentColorGrid(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "ACCENT COLOR",
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
            ),
            color = MaterialTheme.colorScheme.primary,
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            for ((color, name) in WP81AccentColors) {
                val isSelected = selectedColor == color
                val interactionSource = remember { MutableInteractionSource() }

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .metroPressTilt(interactionSource)
                        .background(color)
                        .semantics {
                            contentDescription = name
                            role = Role.RadioButton
                            selected = isSelected
                        }
                        .then(
                            if (isSelected) {
                                Modifier.border(width = 3.dp, color = Color.White)
                            } else {
                                Modifier
                            }
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onColorSelected(color) },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color.White)
                        )
                    }
                }
            }
        }
    }
}
