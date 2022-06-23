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
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@Composable
fun NameImage(
    name: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    style: TextStyle? = null,
) {
    BoxWithConstraints(
        modifier = modifier
            .background(color, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        val combinedTextStyle = style ?: LocalTextStyle.current.copy(
            fontSize = (maxHeight / 2).value.sp
        )
        Text(
            text = name.firstOrNull()?.toString() ?: "N",
            style = combinedTextStyle,
            color = Color.White,
        )
    }
}

private val colors = listOf(
    0xFF1C68F3,
    0xFFF3C41C,
    0xFF6AB0E3,
    0xFFE3716A,
    0xFF6ADCE3,
    0xFF6A76E3,
)
val String.walletColor: Color
    get() {
        // generate color from colors list
        return Color(colors[this.hashCode() % colors.size])
    }
