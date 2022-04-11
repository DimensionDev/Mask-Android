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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ui.theme.isDarkTheme
import com.dimension.maskbook.common.ui.widget.button.MaskButton

@Composable
fun RedPacketClaimButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val isDarkTheme = MaterialTheme.isDarkTheme
    MaskButton(
        enabled = enabled,
        onClick = onClick,
        colors = if (isDarkTheme) {
            RedPacketClaimButtonDefaults.darkButtonColors()
        } else {
            RedPacketClaimButtonDefaults.lightButtonColors()
        },
        shape = CircleShape,
        contentPadding = PaddingValues(vertical = 12.dp),
        modifier = modifier,
        content = content
    )
}

private object RedPacketClaimButtonDefaults {

    @Composable
    fun lightButtonColors() = ButtonDefaults.buttonColors(
        backgroundColor = Color(0xFF111418),
        disabledBackgroundColor = Color(0x80111418),
        contentColor = Color.White,
        disabledContentColor = Color.White,
    )

    @Composable
    fun darkButtonColors() = ButtonDefaults.buttonColors(
        backgroundColor = Color(0xFFEFF3F4),
        disabledBackgroundColor = Color(0x80EFF3F4),
        contentColor = Color(0xFF0F1419),
        disabledContentColor = Color(0x800F1419),
    )
}
