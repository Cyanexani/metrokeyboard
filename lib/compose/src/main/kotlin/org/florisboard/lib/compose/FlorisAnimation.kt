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

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer

fun EnterTransition.Companion.verticalTween(
    duration: Int,
    expandFrom: Alignment.Vertical = Alignment.Bottom,
): EnterTransition {
    return fadeIn(tween(duration)) + expandVertically(tween(duration), expandFrom)
}

fun ExitTransition.Companion.verticalTween(
    duration: Int,
    shrinkTowards: Alignment.Vertical = Alignment.Bottom,
): ExitTransition {
    return fadeOut(tween(duration)) + shrinkVertically(tween(duration), shrinkTowards)
}

fun EnterTransition.Companion.horizontalTween(
    duration: Int,
    expandFrom: Alignment.Horizontal = Alignment.End,
): EnterTransition {
    return fadeIn(tween(duration)) + expandHorizontally(tween(duration), expandFrom)
}

fun ExitTransition.Companion.horizontalTween(
    duration: Int,
    shrinkTowards: Alignment.Horizontal = Alignment.End,
): ExitTransition {
    return fadeOut(tween(duration)) + shrinkHorizontally(tween(duration), shrinkTowards)
}

/**
 * Windows Phone 8.1 3D Turnstile Y-axis transform.
 */
fun Modifier.metroTurnstileSwing(
    rotationY: Float,
    alpha: Float,
    pivotFractionX: Float,
): Modifier = composed {
    this.graphicsLayer {
        transformOrigin = TransformOrigin(pivotFractionX, 0.5f)
        this.rotationY = rotationY
        cameraDistance = 12f * density
        this.alpha = alpha
    }
}

/** Windows Phone 8.1 dialog popup: 0.8 -> 1.0 scale with a short fade. */
@Composable
fun Modifier.metroDialogEnterAnimation(): Modifier {
    var entered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (entered) 1f else 0.8f,
        animationSpec = tween(durationMillis = 250, easing = LinearOutSlowInEasing),
        label = "metro_dialog_scale",
    )
    val dialogAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(durationMillis = 200, easing = LinearEasing),
        label = "metro_dialog_alpha",
    )

    LaunchedEffect(Unit) {
        entered = true
    }

    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
        alpha = dialogAlpha
        transformOrigin = TransformOrigin.Center
    }
}

/**
 * True perspective Turnstile container for navigation destinations.
 *
 * The animation is registered on [AnimatedContentScope.transition], so the
 * outgoing destination remains composed until its 3D exit has completed.
 */
@Composable
fun AnimatedContentScope.MetroTurnstileContainer(
    reverse: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val rotationY by transition.animateFloat(
        transitionSpec = {
            when {
                EnterExitState.PreEnter isTransitioningTo EnterExitState.Visible -> tween(
                    durationMillis = 350,
                    easing = LinearOutSlowInEasing,
                )
                EnterExitState.Visible isTransitioningTo EnterExitState.PostExit -> tween(
                    durationMillis = 250,
                    easing = FastOutLinearInEasing,
                )
                else -> snap()
            }
        },
        label = "metro_turnstile_rotation_y",
    ) { state ->
        when (state) {
            EnterExitState.PreEnter -> if (reverse) -75f else 75f
            EnterExitState.Visible -> 0f
            EnterExitState.PostExit -> if (reverse) 75f else -75f
        }
    }
    val contentAlpha by transition.animateFloat(
        transitionSpec = {
            when {
                (EnterExitState.PreEnter isTransitioningTo EnterExitState.Visible) ||
                (EnterExitState.Visible isTransitioningTo EnterExitState.PostExit) -> tween(
                    durationMillis = 200,
                    easing = LinearEasing,
                )
                else -> snap()
            }
        },
        label = "metro_turnstile_alpha",
    ) { state ->
        if (state == EnterExitState.Visible) 1f else 0f
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .metroTurnstileSwing(
                rotationY = rotationY,
                alpha = contentAlpha,
                pivotFractionX = if (reverse) 1f else 0f,
            ),
        propagateMinConstraints = true,
    ) {
        content()
    }
}
