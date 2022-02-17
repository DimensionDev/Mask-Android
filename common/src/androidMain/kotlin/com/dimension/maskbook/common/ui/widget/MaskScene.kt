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

import android.os.Build
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.dimension.maskbook.common.ui.theme.MaskTheme
import com.dimension.maskbook.common.ui.theme.isDarkTheme

@Composable
fun MaskScene(
    requireDarkTheme: Boolean = false,
    extendViewIntoStatusBar: Boolean = false,
    extendViewIntoNavigationBar: Boolean = false,
    statusBarColorProvider: @Composable () -> Color = {
        MaterialTheme.colors.background
    },
    navigationBarColorProvider: @Composable () -> Color = {
        MaterialTheme.colors.background
    },
    content: @Composable () -> Unit,
) {
    val darkTheme = if (requireDarkTheme) true else isDarkTheme()
    MaskTheme(isDarkTheme = darkTheme) {
        val statusBarColor = statusBarColorProvider.invoke()
        val navigationBarColor = navigationBarColorProvider.invoke().let {
            val surface = MaterialTheme.colors.surface
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O && !darkTheme && it == surface) {
                MaterialTheme.colors.onSurface
            } else {
                it
            }
        }
        PlatformInsets(
            control = NativeInsetsControl(
                extendToTop = extendViewIntoStatusBar,
                extendToBottom = extendViewIntoNavigationBar,
                extendToStart = extendViewIntoNavigationBar,
                extendToEnd = extendViewIntoNavigationBar,
            ),
            color = NativeInsetsColor(
                top = statusBarColor,
                start = navigationBarColor,
                end = navigationBarColor,
                bottom = navigationBarColor,
            ),
            content = content,
        )
    }
}
