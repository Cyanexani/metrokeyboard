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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

/**
 * Windows Phone 8.1 Metro toggle from the supplied UI guide.
 */
@Composable
fun MetroSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    // The guide's 89x34 values are source-screen pixels. At the reference
    // video's density they render at roughly 56x22 Android dp.
    // The supplied phone footage is authoritative for the thumb shape: it is
    // a sharp rectangular block, despite the PDF describing it as circular.
    val trackW = 56.dp
    val trackH = 22.dp
    val trackPadding = 3.dp
    val thumbSize = 16.dp
    val thumbOnX = trackW - trackPadding - thumbSize
    val thumbOffX = trackPadding

    val accentColor = MaterialTheme.colorScheme.primary
    val offBorderColor = MaterialTheme.colorScheme.onBackground

    val trackColor by animateColorAsState(
        targetValue = if (checked) accentColor else Color.Transparent,
        animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing),
        label = "metro_track",
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) accentColor else offBorderColor,
        animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing),
        label = "metro_border",
    )
    val thumbColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.onPrimary
                      else MaterialTheme.colorScheme.onBackground,
        animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing),
        label = "metro_thumb_color",
    )
    val thumbX by animateFloatAsState(
        targetValue = if (checked) thumbOnX.value else thumbOffX.value,
        animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing),
        label = "metro_thumb_x",
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .width(trackW)
            .height(48.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .then(
                if (onCheckedChange != null) {
                    Modifier.toggleable(
                        value = checked,
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = enabled,
                        role = Role.Switch,
                        onValueChange = onCheckedChange,
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .width(trackW)
                .height(trackH)
                .align(Alignment.CenterStart)
                .background(trackColor)
                .border(width = 2.dp, color = borderColor),
        )
        Box(
            modifier = Modifier
                .size(thumbSize)
                .offset(x = thumbX.dp)
                .zIndex(1f)
                .background(thumbColor),
        )
    }
}

/**
 * Full Windows Phone 8.1 settings toggle row.
 *
 * Layout from video:
 *
 *   Block calls+SMS                    [■■■■ □]   ← label + toggle same row
 *   On                                             ← large bold state text below
 *   Block incoming calls from...                   ← optional dim summary
 *
 * The entire row is tappable.
 */
@Composable
fun MetroSwitchPreferenceItem(
    title: String,
    summary: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .metroPressTilt(interactionSource)
            .toggleable(
                value = checked,
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        // Label + toggle on same row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Normal,
                ),
                color = if (enabled) MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
            )
            MetroSwitch(
                checked = checked,
                onCheckedChange = null,
                enabled = enabled,
            )
        }

        // Large "On" / "Off" status — WP 8.1 signature style
        Text(
            text = stringResource(
                if (checked) R.string.metro_switch_state_on else R.string.metro_switch_state_off,
            ),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 20.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Light,
            ),
            color = if (enabled) MaterialTheme.colorScheme.onBackground
                    else MaterialTheme.colorScheme.outline,
        )

        // Optional description / summary
        if (summary != null) {
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                ),
                color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.outline,
            )
        }
    }
}
