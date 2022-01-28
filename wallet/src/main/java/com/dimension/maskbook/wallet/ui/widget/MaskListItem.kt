/*
 *  Mask-Android
 *
 *  Copyright (C) DimensionDev and Contributors
 * 
 *  This file is part of Mask-Android.
 * 
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Mask-Android. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.ext.applyTextStyle

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MaskListItem(
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    secondaryText: @Composable (() -> Unit)? = null,
    overlineText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    text: @Composable () -> Unit
) {
    val typography = MaterialTheme.typography
    val styleText = applyTextStyle(typography.h5, text)!!
    val styledSecondaryText = applyTextStyle(typography.body2, secondaryText)
    val styledOverlineText = applyTextStyle(typography.overline, overlineText)
    val styledTrailing = applyTextStyle(typography.body2, trailing)

    Row(
        modifier = modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            icon()
            Spacer(Modifier.width(8.dp))
        }
        if (styledSecondaryText != null) {
            Column(Modifier.weight(1f)) {
                styleText()
                styledSecondaryText()
            }
        } else if (styledOverlineText != null) {
            Column(Modifier.weight(1f)) {
                styledOverlineText()
                styleText()
            }
        } else {
            Box(Modifier.weight(1f)) {
                styleText()
            }
        }
        if (styledTrailing != null) {
            Spacer(Modifier.width(8.dp))
            styledTrailing()
        }
    }
}
