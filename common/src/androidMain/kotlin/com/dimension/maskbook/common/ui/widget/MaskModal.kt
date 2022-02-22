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

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ui.notification.InAppNotification
import com.dimension.maskbook.common.ui.theme.isDarkTheme
import com.google.accompanist.insets.navigationBarsWithImePadding

@Composable
fun MaskModal(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    subTitle: @Composable (() -> Unit)? = null,
    contentPadding: PaddingValues = ModalPadding,
    content: @Composable () -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val inAppNotification = remember { InAppNotification() }
    CompositionLocalProvider(LocalInAppNotification provides inAppNotification) {
        MaskInAppNotification(snackBarHostState)
        Box(modifier = Modifier.navigationBarsWithImePadding()) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(20.dp))
                Spacer(
                    modifier = Modifier
                        .size(40.dp, 4.dp)
                        .background(
                            if (isDarkTheme()) {
                                MaskModalDefaults.lineDarkColor
                            } else {
                                MaskModalDefaults.lineLightColor
                            },
                            shape = CircleShape
                        )
                )
                Spacer(Modifier.height(24.dp))
                if (title != null) {
                    ProvideTextStyle(MaterialTheme.typography.h4, title)
                }
                if (subTitle != null) {
                    Spacer(Modifier.height(13.dp))
                    ProvideTextStyle(MaterialTheme.typography.subtitle2, subTitle)
                }
                if (title != null || subTitle != null) {
                    Spacer(Modifier.height(20.dp))
                }
                Box(
                    modifier = Modifier.padding(contentPadding)
                ) {
                    content.invoke()
                }
                Spacer(Modifier.height(24.dp))
            }
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.align(Alignment.BottomCenter),
                snackbar = { MaskSnackbar(it) }
            )
        }
    }
}

private object MaskModalDefaults {
    val lineLightColor = Color(0xFFCBD1D9)
    val lineDarkColor = Color(0xFF2C354C)
}

val ModalPadding = PaddingValues(
    top = 0.dp,
    start = 24.dp,
    end = 24.dp,
    bottom = 0.dp,
)
