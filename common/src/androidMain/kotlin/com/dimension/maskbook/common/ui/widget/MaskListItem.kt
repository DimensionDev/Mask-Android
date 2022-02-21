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
import androidx.compose.foundation.layout.PaddingValues
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
import com.dimension.maskbook.common.ext.applyTextStyle

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MaskListItem(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    icon: @Composable (() -> Unit)? = null,
    secondaryText: @Composable (() -> Unit)? = null,
    overlineText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    text: @Composable () -> Unit
) {
    val typography = MaterialTheme.typography
    val styledText = applyTextStyle(typography.h5, text) ?: text
    val styledSecondaryText = applyTextStyle(typography.body2, secondaryText)
    val styledOverlineText = applyTextStyle(typography.overline, overlineText)
    val styledTrailing = applyTextStyle(typography.body2, trailing)

    Row(
        modifier = modifier.padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            icon()
            Spacer(Modifier.width(8.dp))
        }
        when {
            styledSecondaryText != null -> {
                Column(Modifier.weight(1f)) {
                    styledText.invoke()
                    styledSecondaryText()
                }
            }
            styledOverlineText != null -> {
                Column(Modifier.weight(1f)) {
                    styledOverlineText()
                    styledText.invoke()
                }
            }
            else -> {
                Box(Modifier.weight(1f)) {
                    styledText.invoke()
                }
            }
        }
        if (styledTrailing != null) {
            Spacer(Modifier.width(8.dp))
            styledTrailing()
        }
    }
}
