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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


object MetroboardCardDefaults {
    val IconRequiredSize = 24.dp
    val IconSpacing = 12.dp

    val ContentPadding = PaddingValues(start = 0.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
}

object BoxDefaults {
    val OutlinedBoxShape = RoundedCornerShape(0.dp)

    val ContentPadding = PaddingValues(all = 0.dp)
}

@Composable
fun MetroboardSimpleCard(
    modifier: Modifier = Modifier,
    text: String,
    secondaryText: String? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    contentPadding: PaddingValues = MetroboardCardDefaults.ContentPadding,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    icon: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 44.dp)
            .background(backgroundColor)
            .border(width = 2.dp, color = borderColor)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Row(
                modifier = Modifier.padding(contentPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (icon != null) {
                    icon()
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = if (icon == null) 16.dp else 0.dp),
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Normal,
                        ),
                    )
                    if (secondaryText != null) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = secondaryText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetroboardErrorCard(
    text: String,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true,
    contentPadding: PaddingValues = MetroboardCardDefaults.ContentPadding,
    onClick: (() -> Unit)? = null,
) {
    MetroboardSimpleCard(
        modifier = modifier,
        backgroundColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        borderColor = Color(0xFFD33A2C),
        onClick = onClick,
        icon = if (showIcon) ({ Icon(
            modifier = Modifier
                .padding(all = MetroboardCardDefaults.IconSpacing)
                .requiredSize(MetroboardCardDefaults.IconRequiredSize),
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
        ) }) else null,
        text = text,
        contentPadding = contentPadding,
    )
}

@Composable
fun MetroboardWarningCard(
    text: String,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true,
    contentPadding: PaddingValues = MetroboardCardDefaults.ContentPadding,
    onClick: (() -> Unit)? = null,
) {
    MetroboardSimpleCard(
        modifier = modifier,
        backgroundColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        borderColor = Color(0xFFB8720A),
        onClick = onClick,
        icon = if (showIcon) ({ Icon(
            modifier = Modifier
                .padding(all = MetroboardCardDefaults.IconSpacing)
                .requiredSize(MetroboardCardDefaults.IconRequiredSize),
            imageVector = Icons.Outlined.Warning,
            contentDescription = null,
        ) }) else null,
        text = text,
        contentPadding = contentPadding,
    )
}

@Composable
fun MetroboardInfoCard(
    text: String,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true,
    contentPadding: PaddingValues = MetroboardCardDefaults.ContentPadding,
    onClick: (() -> Unit)? = null,
) {
    MetroboardSimpleCard(
        modifier = modifier,
        backgroundColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        borderColor = MaterialTheme.colorScheme.primary,
        onClick = onClick,
        icon = if (showIcon) ({ Icon(
            modifier = Modifier
                .padding(all = MetroboardCardDefaults.IconSpacing)
                .requiredSize(MetroboardCardDefaults.IconRequiredSize),
            imageVector = Icons.Default.Info,
            contentDescription = null,
        ) }) else null,
        text = text,
        contentPadding = contentPadding,
    )
}

@Composable
fun MetroboardOutlinedBox(
    modifier: Modifier = Modifier,
    title: String,
    onTitleClick: (() -> Unit)? = null,
    subtitle: String? = null,
    onSubtitleClick: (() -> Unit)? = null,
    borderWidth: Dp = 1.dp,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    shape: Shape = BoxDefaults.OutlinedBoxShape,
    contentPadding: PaddingValues = BoxDefaults.ContentPadding,
    content: @Composable ColumnScope.() -> Unit,
) {
    MetroboardOutlinedBox(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        onTitleClick = onTitleClick,
        subtitle = if (subtitle != null) {
            {
                Text(
                    modifier = Modifier
                        .padding(start = 6.dp, end = 6.dp, bottom = 4.dp),
                    text = subtitle,
                    color = LocalContentColor.current.copy(alpha = 0.56f),
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                )
            }
        } else {
            null
        },
        onSubtitleClick = onSubtitleClick,
        borderWidth = borderWidth,
        borderColor = borderColor,
        shape = shape,
        contentPadding = contentPadding,
        content = content,
    )
}

// TODO: Rework internal implementation (with same API and visual appearance) of MetroboardOutlinedBox
//  to avoid too much nesting and improve performance
@Composable
fun MetroboardOutlinedBox(
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)? = null,
    onTitleClick: (() -> Unit)? = null,
    subtitle: (@Composable () -> Unit)? = null,
    onSubtitleClick: (() -> Unit)? = null,
    borderWidth: Dp = 1.dp,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    shape: Shape = BoxDefaults.OutlinedBoxShape,
    contentPadding: PaddingValues = BoxDefaults.ContentPadding,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .padding(top = if (title != null) 11.dp else 0.dp),
    ) {
        Column(
            modifier = Modifier
                .border(borderWidth, borderColor, shape)
                .clip(shape)
                .padding(top = if (title != null) 11.dp else 0.dp),
        ) {
            if (title != null && subtitle != null) {
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp, bottom = 4.dp)
                        .rippleClickable(enabled = onSubtitleClick != null) {
                            onSubtitleClick!!()
                        },
                ) {
                    subtitle()
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding),
                content = content,
            )
        }
        if (title != null) {
            Box(
                modifier = Modifier
                    .height(23.dp)
                    .offset(x = 10.dp, y = (-12).dp)
                    .background(MaterialTheme.colorScheme.background)
                    .rippleClickable(enabled = onTitleClick != null) {
                        onTitleClick!!()
                    }
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                title()
            }
        }
    }
}

fun Modifier.defaultMetroboardOutlinedBox(): Modifier {
    return this
        .fillMaxWidth()
        .padding(vertical = 8.dp, horizontal = 16.dp)
}
