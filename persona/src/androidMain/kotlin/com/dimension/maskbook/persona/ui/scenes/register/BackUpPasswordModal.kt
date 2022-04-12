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
package com.dimension.maskbook.persona.ui.scenes.register

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.MaskPasswordInputField
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.persona.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BackUpPasswordModal(
    password: String,
    onPasswordChanged: (String) -> Unit,
    passwordValid: Boolean,
    onConfirm: () -> Unit,
) {
    MaskModal {
        Column(
            modifier = Modifier
                .animateContentSize(),
        ) {
            Text(
                text = stringResource(R.string.scene_set_backup_password_backup_password),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                MaskPasswordInputField(
                    value = password,
                    onValueChange = onPasswordChanged,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (password.isNotEmpty() && !passwordValid) {
                        stringResource(R.string.scene_change_password_incorrect_password)
                    } else "",
                    color = Color.Red
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                PrimaryButton(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    enabled = passwordValid,
                ) {
                    Text(
                        text = stringResource(R.string.common_controls_next)
                    )
                }
            }
        }
    }
}
