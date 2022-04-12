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
package com.dimension.maskbook.labs.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ui.widget.button.MaskButton

@Composable
fun RedPacketShareButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit,
) {
    MaskButton(
        onClick = onClick,
        modifier = modifier,
        contentPadding = contentPadding,
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
        ),
    ) {
        Box {
            Spacer(
                modifier = Modifier
                    .background(color = Color(0xFFF9B95F), shape = CircleShape)
                    .padding(top = 5.dp)
                    .height(43.dp)
                    .fillMaxWidth(),
            )
            Box(contentAlignment = Alignment.Center) {
                Spacer(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    Color(0xFFFCCD65),
                                    Color(0xFFFFCA7B),
                                )
                            ),
                            shape = CircleShape
                        )
                        .height(43.dp)
                        .fillMaxWidth(),
                )
                content()
            }
        }
    }
}
