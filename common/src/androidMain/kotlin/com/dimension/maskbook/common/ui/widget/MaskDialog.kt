/*
 *  Mask-Android
 *
 *  Copyright (C) 2022  DimensionDev and Contributors
 *
 *  This file is part of Mask-Android.
 *
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Mask-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.common.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dimension.maskbook.common.ui.theme.MaskTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MaskDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    buttons: @Composable () -> Unit = {},
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    properties: DialogProperties = DialogProperties(
        usePlatformDefaultWidth = false,
    )
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        MaskTheme {
            Box(
                modifier = Modifier.padding(ModalPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MaskDialogContent(
                    modifier,
                    MaterialTheme.shapes.medium,
                    MaterialTheme.colors.background,
                    contentColorFor(MaterialTheme.colors.surface),
                    icon,
                    title,
                    text,
                    buttons
                )
            }
        }
    }
}

@Composable
private fun MaskDialogContent(
    modifier: Modifier,
    shape: Shape,
    backgroundColor: Color,
    contentColor: Color,
    icon: @Composable (() -> Unit)?,
    title: @Composable (() -> Unit)?,
    text: @Composable (() -> Unit)?,
    buttons: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 24.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            icon?.invoke()
            Spacer(modifier = Modifier.height(24.dp))
            if (title != null) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.h4,
                    content = title,
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            if (text != null) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.body2,
                    content = text,
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            buttons()
        }
    }
}
