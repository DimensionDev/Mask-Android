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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.R
import com.dimension.maskbook.common.ui.widget.button.MaskButton

@Composable
fun MaskSearchInput(
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSearch: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.small,
    placeholder: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
) {
    MaskButton(
        onClick = {},
        shape = shape,
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        MaskInputField(
            value = value,
            onValueChange = onValueChanged,
            modifier = modifier.weight(1f),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = placeholder,
            readOnly = readOnly,
            singleLine = true,
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch()
                }
            ),
        )
        trailing?.invoke()
    }
}
