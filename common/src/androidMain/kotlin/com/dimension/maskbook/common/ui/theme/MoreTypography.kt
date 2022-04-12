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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
class MoreTypography(
    val h7: TextStyle,
    val h10: TextStyle,
)

internal val LocalMoreTypography = staticCompositionLocalOf { provideMoreTypography(false) }

val MaterialTheme.moreTypography: MoreTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalMoreTypography.current

fun provideMoreTypography(isDarkTheme: Boolean): MoreTypography {
    return MoreTypography(
        h7 = TextStyle(
            fontSize = 12.sp,
            lineHeight = 18.sp,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.W700,
            color = if (isDarkTheme) Color.White.copy(0.8f) else Color(0xFF1D2238),
        ),
        h10 = TextStyle(
            fontSize = 10.sp,
            lineHeight = 15.sp,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.W400,
        ),
    )
}
