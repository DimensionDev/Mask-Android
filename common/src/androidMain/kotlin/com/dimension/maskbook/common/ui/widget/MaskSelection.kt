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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CheckboxColors
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MaskSelection(
    selected: Boolean,
    onClicked: () -> Unit,
    enabled: Boolean = true,
    checkboxColors: CheckboxColors = CircleCheckboxDefaults.colors(),
    content: @Composable RowScope.() -> Unit,
) {
    CompositionLocalProvider(
        LocalElevationOverlay provides null
    ) {
        MaskCard(
            elevation = 0.dp,
            backgroundColor = if (selected) MaterialTheme.colors.surface else Color.Transparent,
            onClick = onClicked,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.h5,
                    content = { content.invoke(this) }
                )
                if (selected) {
                    Spacer(Modifier.weight(1f))
                    CircleCheckbox(
                        checked = true,
                        colors = checkboxColors,
                    )
                }
            }
        }
    }
}
