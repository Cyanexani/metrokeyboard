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

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.ui.semantics.Role
import kotlin.math.roundToInt

/**
 * Bare WP 8.1 Metro radio indicator with no label or row wrapper, for embedding inside
 * existing list items (e.g. as a JetPrefListItem leading icon) that render their own text.
 */
@Composable
fun MetroRadioIndicator(
    selected: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val foregroundColor = MaterialTheme.colorScheme.onBackground
    val dotScale by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing),
        label = "metro_radio_dot_scale",
    )

    Box(
        modifier = modifier
            .size(32.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .clip(CircleShape)
            .background(Color.Transparent)
            .border(width = 3.dp, color = foregroundColor, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .graphicsLayer {
                    scaleX = dotScale
                    scaleY = dotScale
                }
                .clip(CircleShape)
                .background(foregroundColor),
        )
    }
}

@Composable
fun MetroRadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    subLabel: String? = null,
    enabled: Boolean = true,
    trailing: (@Composable () -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .metroPressTilt(interactionSource)
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        MetroRadioIndicator(selected = selected, enabled = enabled)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                ),
                color = if (enabled) MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.outline,
            )
            if (!subLabel.isNullOrBlank()) {
                Text(
                    text = subLabel,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        trailing?.invoke()
    }
}

/**
 * Windows Phone 8.1 Metro Progress / Slider.
 *
 *   - Track: flat 4dp line. Accent-filled up to the thumb.
 *   - Thumb: 24dp accent circle, growing to 28dp while pressed.
 *   - Tap anywhere on the track to jump to that position, or drag the thumb.
 */
@Composable
fun MetroSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
) {
    val accentColor = MaterialTheme.colorScheme.primary
    val trackH = 4.dp
    val thumbRestingSize = 24.dp
    val thumbPressedSize = 28.dp
    val density = LocalDensity.current
    var isPressed by remember { mutableStateOf(false) }
    val thumbSize by animateDpAsState(
        targetValue = if (isPressed) thumbPressedSize else thumbRestingSize,
        animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing),
        label = "metro_slider_thumb_size",
    )

    fun valueFromFraction(fraction: Float): Float {
        val span = valueRange.endInclusive - valueRange.start
        return (valueRange.start + fraction.coerceIn(0f, 1f) * span)
            .coerceIn(valueRange.start, valueRange.endInclusive)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp)
            .alpha(if (enabled) 1f else 0.4f),
        contentAlignment = Alignment.CenterStart,
    ) {
        val maxWidthPx = with(density) { maxWidth.toPx() }
        val thumbTouchPx = with(density) { thumbPressedSize.toPx() }
        val usableWidthPx = (maxWidthPx - thumbTouchPx).coerceAtLeast(1f)

        val span = valueRange.endInclusive - valueRange.start
        val fraction = if (span == 0f) 0f else ((value - valueRange.start) / span).coerceIn(0f, 1f)
        val thumbCenterPx = thumbTouchPx / 2f + fraction * usableWidthPx

        fun updateFromX(x: Float) {
            if (!enabled) return
            val f = ((x - thumbTouchPx / 2f) / usableWidthPx).coerceIn(0f, 1f)
            onValueChange(valueFromFraction(f))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackH)
                .align(Alignment.CenterStart)
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)),
        )
        Box(
            modifier = Modifier
                .width(with(density) { thumbCenterPx.toDp() })
                .height(trackH)
                .align(Alignment.CenterStart)
                .background(accentColor),
        )
        Box(
            modifier = Modifier
                .size(thumbPressedSize)
                .offset {
                    IntOffset(
                        x = (thumbCenterPx - thumbTouchPx / 2f).roundToInt(),
                        y = 0,
                    )
                }
                .zIndex(1f)
                .clip(CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(thumbSize)
                    .clip(CircleShape)
                    .background(accentColor),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(enabled, maxWidthPx, valueRange, steps) {
                    detectTapGestures(
                        onPress = { offset ->
                            isPressed = true
                            updateFromX(offset.x)
                            tryAwaitRelease()
                            isPressed = false
                        },
                    )
                }
                .pointerInput(enabled, maxWidthPx, valueRange, steps) {
                    detectHorizontalDragGestures(
                        onDragStart = { offset ->
                            isPressed = true
                            updateFromX(offset.x)
                        },
                        onDragEnd = { isPressed = false },
                        onDragCancel = { isPressed = false },
                        onHorizontalDrag = { change, _ -> updateFromX(change.position.x) },
                    )
                },
        )
    }
}

/**
 * Windows Phone 8.1 Metro Vertical Slider (Audio Equalizer Style).
 *
 *   - Track: flat 4dp line. Accent-filled from bottom up to the thumb.
 *   - Thumb: 24dp accent circle, growing to 28dp while pressed.
 */
@Composable
fun MetroVerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
) {
    val accentColor = MaterialTheme.colorScheme.primary
    val trackW = 4.dp
    val thumbRestingSize = 24.dp
    val thumbPressedSize = 28.dp
    val density = LocalDensity.current
    var isPressed by remember { mutableStateOf(false) }
    val thumbSize by animateDpAsState(
        targetValue = if (isPressed) thumbPressedSize else thumbRestingSize,
        animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing),
        label = "metro_vertical_slider_thumb_size",
    )

    fun valueFromFraction(fraction: Float): Float {
        val span = valueRange.endInclusive - valueRange.start
        return (valueRange.start + fraction.coerceIn(0f, 1f) * span)
            .coerceIn(valueRange.start, valueRange.endInclusive)
    }

    BoxWithConstraints(
        modifier = modifier
            .width(76.dp)
            .fillMaxHeight()
            .alpha(if (enabled) 1f else 0.4f),
        contentAlignment = Alignment.BottomCenter,
    ) {
        val maxHeightPx = with(density) { maxHeight.toPx() }
        val thumbTouchPx = with(density) { thumbPressedSize.toPx() }
        val usableHeightPx = (maxHeightPx - thumbTouchPx).coerceAtLeast(1f)

        val span = valueRange.endInclusive - valueRange.start
        val fraction = if (span == 0f) 0f else ((value - valueRange.start) / span).coerceIn(0f, 1f)
        val thumbCenterFromBottomPx = thumbTouchPx / 2f + fraction * usableHeightPx

        fun updateFromY(y: Float) {
            if (!enabled) return
            val f = ((maxHeightPx - y - thumbTouchPx / 2f) / usableHeightPx)
                .coerceIn(0f, 1f)
            onValueChange(valueFromFraction(f))
        }

        Box(
            modifier = Modifier
                .width(trackW)
                .fillMaxHeight()
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)),
        )
        Box(
            modifier = Modifier
                .width(trackW)
                .height(with(density) { thumbCenterFromBottomPx.toDp() })
                .align(Alignment.BottomCenter)
                .background(accentColor),
        )
        Box(
            modifier = Modifier
                .size(thumbPressedSize)
                .align(Alignment.BottomCenter)
                .offset {
                    IntOffset(
                        x = 0,
                        y = -(thumbCenterFromBottomPx - thumbTouchPx / 2f).roundToInt(),
                    )
                }
                .zIndex(1f)
                .clip(CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(thumbSize)
                    .clip(CircleShape)
                    .background(accentColor),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(enabled, maxHeightPx, valueRange, steps) {
                    detectTapGestures(
                        onPress = { offset ->
                            isPressed = true
                            updateFromY(offset.y)
                            tryAwaitRelease()
                            isPressed = false
                        },
                    )
                }
                .pointerInput(enabled, maxHeightPx, valueRange, steps) {
                    detectVerticalDragGestures(
                        onDragStart = { offset ->
                            isPressed = true
                            updateFromY(offset.y)
                        },
                        onDragEnd = { isPressed = false },
                        onDragCancel = { isPressed = false },
                        onVerticalDrag = { change, _ -> updateFromY(change.position.y) },
                    )
                },
        )
    }
}

/**
 * Windows Phone 8.1 Metro Slider Preference Item.
 *
 * Displays title on top left, value label on top right,
 * and WP 8.1 flat rectangular slider directly inline below!
 */
@Composable
fun MetroSliderPreferenceItem(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    valueLabel: @Composable (Float) -> String = { it.toInt().toString() },
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
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
            Text(
                text = valueLabel(value),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.primary,
            )
        }

        MetroSlider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            enabled = enabled,
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
