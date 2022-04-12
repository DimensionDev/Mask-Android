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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@Composable
fun NameImage(
    name: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    style: TextStyle? = null,
    alpha: Float = LocalContentAlpha.current,
) {
    BoxWithConstraints(
        modifier = modifier
            .alpha(alpha)
            .background(color, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        val combinedTextStyle = style ?: LocalTextStyle.current.copy(
            fontSize = (maxHeight / 2).value.sp
        )
        Text(
            text = name.firstOrNull()?.toString() ?: "N",
            style = combinedTextStyle,
            color = contentColorFor(color),
        )
    }
}

val String.color: Color
    get() {
        // Generate color from a string
        val hash = this.hashCode()
        val r = (hash shr 16 and 0xFF).toFloat()
        val g = (hash shr 8 and 0xFF).toFloat()
        val b = (hash and 0xFF).toFloat()
        return Color(r / 255f, g / 255f, b / 255f)
    }
