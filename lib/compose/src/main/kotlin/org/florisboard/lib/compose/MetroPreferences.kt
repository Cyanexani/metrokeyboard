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

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
private fun MetroCheckmark(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(size.width * 0.10f, size.height * 0.52f)
            lineTo(size.width * 0.40f, size.height * 0.80f)
            lineTo(size.width * 0.90f, size.height * 0.16f)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Square,
                join = StrokeJoin.Miter,
            ),
        )
    }
}

/**
 * Windows Phone 8.1 Metro Checkbox.
 */
@Composable
fun MetroCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val foregroundColor = MaterialTheme.colorScheme.onBackground
    val markScale by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing),
        label = "metro_checkbox_mark_scale",
    )

    val hitTargetSize = if (onCheckedChange != null) 44.dp else 24.dp

    Box(
        modifier = modifier
            .size(hitTargetSize)
            .alpha(if (enabled) 1f else 0.4f)
            .then(
                if (onCheckedChange != null) {
                    Modifier.toggleable(
                        value = checked,
                        enabled = enabled,
                        role = Role.Checkbox,
                        onValueChange = onCheckedChange,
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .border(width = 2.dp, color = foregroundColor)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                MetroCheckmark(
                    color = foregroundColor,
                    modifier = Modifier
                        .size(18.dp)
                        .graphicsLayer {
                            scaleX = markScale
                            scaleY = markScale
                        },
                )
            }
        }
    }
}

/**
 * Windows Phone 8.1 Metro tri-state Checkbox, for "select all / some / none" list headers.
 */
@Composable
fun MetroTriStateCheckbox(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val foregroundColor = MaterialTheme.colorScheme.onBackground
    val isFilled = state != ToggleableState.Off
    val markScale by animateFloatAsState(
        targetValue = if (isFilled) 1f else 0f,
        animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing),
        label = "metro_tri_state_mark_scale",
    )

    Box(
        modifier = modifier
            .size(44.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .then(
                if (onClick != null) Modifier.clickable(enabled = enabled, onClick = onClick) else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .border(width = 2.dp, color = foregroundColor)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                ToggleableState.On -> MetroCheckmark(
                    color = foregroundColor,
                    modifier = Modifier
                        .size(18.dp)
                        .graphicsLayer {
                            scaleX = markScale
                            scaleY = markScale
                        },
                )
                ToggleableState.Indeterminate -> Box(
                    modifier = Modifier
                        .size(width = 12.dp, height = 2.dp)
                        .background(foregroundColor)
                        .graphicsLayer {
                            scaleX = markScale
                            scaleY = markScale
                        },
                )
                ToggleableState.Off -> Unit
            }
        }
    }
}

/**
 * Preference Item with WP 8.1 Checkmark Tick (Left-aligned checkmark tick box).
 */
@Composable
fun MetroCheckboxPreferenceItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .metroPressTilt(interactionSource)
            .toggleable(
                value = checked,
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Checkbox,
                onValueChange = onCheckedChange,
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MetroCheckbox(
            checked = checked,
            onCheckedChange = null,
            enabled = enabled,
            modifier = Modifier.padding(top = 2.dp),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Normal,
                ),
                color = if (enabled) MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.outline,
            )
            if (summary != null) {
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/**
 * Windows Phone 8.1 tri-state preference row for a select-all/some/none group.
 */
@Composable
fun MetroTriStateCheckboxPreferenceItem(
    title: String,
    state: ToggleableState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .metroPressTilt(interactionSource)
            .triStateToggleable(
                state = state,
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Checkbox,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MetroTriStateCheckbox(
            state = state,
            onClick = null,
            enabled = enabled,
            modifier = Modifier.padding(top = 2.dp),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Normal,
                ),
                color = if (enabled) MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.outline,
            )
            if (summary != null) {
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/**
 * Signature Windows Phone 8.1 Inline List Picker / Dropdown.
 */
@Composable
fun <T> MetroInlineListPicker(
    title: String,
    selectedValue: T,
    entries: List<Pair<T, String>>,
    onValueSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }
    val accentColor = MaterialTheme.colorScheme.primary
    val currentLabel = entries.firstOrNull { it.first == selectedValue }?.second ?: selectedValue.toString()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (title.isNotBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Normal,
                ),
                color = if (enabled) MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.outline,
            )
        }

        if (!expanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 2.dp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                    .clickable(enabled = enabled) { expanded = true }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
            ) {
                Text(
                    text = currentLabel,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 2.5.dp, color = accentColor)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(vertical = 4.dp),
            ) {
                Column {
                    entries.forEach { (value, label) ->
                        val isSelected = value == selectedValue
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = enabled) {
                                    onValueSelected(value)
                                    expanded = false
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (isSelected) {
                                MetroCheckmark(
                                    color = accentColor,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 17.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                ),
                                color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }

        if (summary != null) {
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Windows Phone 8.1 Adaptive Option Picker:
 * - If entries.size <= 3: Displays clean horizontal Metro segmented buttons.
 * - If entries.size > 3: Displays a signature WP 8.1 Metro dropdown picker.
 */
@Composable
fun <T> MetroOptionPicker(
    title: String,
    selectedValue: T,
    entries: List<Pair<T, String>>,
    onValueSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
) {
    if (entries.size <= 3) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (title.isNotBlank()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                    color = if (enabled) MaterialTheme.colorScheme.onBackground
                            else MaterialTheme.colorScheme.outline,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                entries.forEach { (value, label) ->
                    val isSelected = value == selectedValue
                    val interactionSource = remember { MutableInteractionSource() }
                    val accentColor = MaterialTheme.colorScheme.primary
                    val borderColor = if (isSelected) accentColor
                                      else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    val bgColor = if (isSelected) accentColor.copy(alpha = 0.2f) else Color.Transparent

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .border(width = 2.dp, color = borderColor)
                            .background(bgColor)
                            .metroPressTilt(interactionSource)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                enabled = enabled,
                                onClick = { onValueSelected(value) },
                            )
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (isSelected) {
                            MetroCheckmark(
                                color = accentColor,
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .size(15.dp),
                            )
                        }
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            ),
                            color = if (isSelected) accentColor else MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }

            if (summary != null) {
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    } else {
        MetroInlineListPicker(
            title = title,
            selectedValue = selectedValue,
            entries = entries,
            onValueSelected = onValueSelected,
            modifier = modifier,
            summary = summary,
            enabled = enabled,
        )
    }
}

/**
 * Windows Phone 8.1 Metro List Item with Square Accent Tile Icon.
 */
@Composable
fun MetroTilePreferenceItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val accentColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .metroPressTilt(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(if (enabled) accentColor else Color.DarkGray),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title.lowercase(),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                ),
                color = if (enabled) MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            )
            if (summary != null) {
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

/**
 * Icon-free Windows Phone settings navigation row.
 * The original settings hub is driven by typography, not colored app tiles.
 */
@Composable
fun MetroNavigationPreferenceItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
    onLongClick: (() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .metroPressTilt(interactionSource)
            .then(
                if (onLongClick != null) {
                    Modifier.combinedClickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = enabled,
                        onClick = onClick,
                        onLongClick = onLongClick,
                    )
                } else {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = enabled,
                        onClick = onClick,
                    )
                }
            )
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title.lowercase(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Normal,
            ),
            color = if (enabled) MaterialTheme.colorScheme.onBackground
                    else MaterialTheme.colorScheme.outline,
        )
        if (summary != null) {
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Windows Phone 8.1 Metro-style List Picker / Dropdown item.
 */
@Composable
fun MetroListPreferenceItem(
    title: String,
    valueText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val borderColor = if (enabled) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                      else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
            ),
            color = if (enabled) MaterialTheme.colorScheme.onBackground
                    else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 2.dp, color = borderColor)
                .padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            Text(
                text = valueText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = if (enabled) MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            )
        }

        if (summary != null) {
            Text(
                text = summary,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
            )
        }
    }
}

/**
 * Windows Phone 8.1 Metro-style Action Button item.
 */
@Composable
fun MetroButtonItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val borderColor = if (enabled) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                      else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(width = 2.dp, color = borderColor)
            .metroPressTilt(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(
            text = text.lowercase(),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = if (enabled) MaterialTheme.colorScheme.onBackground
                    else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
        )
    }
}

/**
 * Windows Phone 8.1 Bottom Action Bar (Frame 82).
 */
@Composable
fun MetroBottomActionBar(
    actions: List<Pair<String, () -> Unit>>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        actions.forEach { (label, onClick) ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(width = 2.dp, color = Color.White)
                    .clickable(onClick = onClick)
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label.lowercase(),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = Color.White,
                )
            }
        }
    }
}

/**
 * Windows Phone 8.1 Metro Text Link.
 */
@Composable
fun MetroTextLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            textDecoration = TextDecoration.Underline,
        ),
        color = if (enabled) MaterialTheme.colorScheme.onBackground
                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
        modifier = modifier
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

/**
 * Signature Windows Phone 8.1 Alphabet Jump Tile Header ([ a ], [ b ]).
 */
@Composable
fun MetroAlphabetHeader(
    letter: Char,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val accentColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            .size(32.dp)
            .border(width = 2.dp, color = accentColor)
            .background(accentColor.copy(alpha = 0.15f))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = letter.lowercaseChar().toString(),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = accentColor,
        )
    }
}

/**
 * Windows Phone 8.1 Metro Section Header.
 */
@Composable
fun MetroHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title.lowercase(),
        style = MaterialTheme.typography.titleLarge.copy(
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.sp,
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
    )
}

/** Flat Windows Phone section grouping; intentionally has no card or outline. */
@Composable
fun MetroSection(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    onTitleClick: (() -> Unit)? = null,
    onSubtitleClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (title != null) {
            MetroHeader(
                title = title,
                modifier = if (onTitleClick != null) Modifier.clickable(onClick = onTitleClick) else Modifier,
            )
        }
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier
                    .then(if (onSubtitleClick != null) Modifier.clickable(onClick = onSubtitleClick) else Modifier)
                    .padding(start = 16.dp, end = 16.dp, bottom = 6.dp),
            )
        }
        content()
    }
}

/**
 * Windows Phone 8.1 Metro Text Input Box (Matching Frame 95 of reference video).
 */
@Composable
fun MetroTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor = if (isFocused) MaterialTheme.colorScheme.primary
                      else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
    val bgColor = if (isFocused) Color.White else Color.Transparent
    val textColor = if (isFocused) Color.Black else MaterialTheme.colorScheme.onBackground

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                ),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = singleLine,
            textStyle = TextStyle(
                color = textColor,
                fontSize = 16.sp,
                fontFamily = FontFamily.SansSerif,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 2.dp, color = borderColor, shape = RectangleShape)
                .background(bgColor)
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .onFocusChanged { isFocused = it.isFocused },
            decorationBox = { innerTextField ->
                if (value.isEmpty() && placeholder != null) {
                    Text(
                        text = placeholder,
                        style = TextStyle(
                            color = textColor.copy(alpha = 0.4f),
                            fontSize = 16.sp,
                            fontFamily = FontFamily.SansSerif,
                        )
                    )
                }
                innerTextField()
            }
        )
    }
}
