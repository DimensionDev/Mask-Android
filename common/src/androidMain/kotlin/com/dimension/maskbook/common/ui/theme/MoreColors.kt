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
package com.dimension.maskbook.common.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.graphics.Color

@Stable
class MoreColors constructor(
    caption: Color,
    onCaption: Color,
) {
    var caption by mutableStateOf(caption, structuralEqualityPolicy())
        internal set
    var onCaption by mutableStateOf(onCaption, structuralEqualityPolicy())
        internal set
}

internal val LocalMoreColors = staticCompositionLocalOf { provideMoreColors(false) }

val MaterialTheme.moreColor: MoreColors
    @Composable
    @ReadOnlyComposable
    get() = LocalMoreColors.current

fun moreColors(
    caption: Color,
    onCaption: Color,
) = MoreColors(
    caption = caption,
    onCaption = onCaption,
)

fun provideMoreColors(isDarkTheme: Boolean): MoreColors {
    return if (isDarkTheme) {
        moreColors(
            caption = Color(0xCC171C31),
            onCaption = Color(0x66FFFFFF),
        )
    } else {
        moreColors(
            caption = Color(0xFFF0F3F8),
            onCaption = Color(0xFF6B738D),
        )
    }
}
