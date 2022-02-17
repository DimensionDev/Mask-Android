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
package com.dimension.maskbook.setting.ui.scenes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ui.widget.MaskInputField
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.localization.R

@Composable
fun PhoneCodeInputModal(
    phone: String,
    code: String,
    onCodeChange: (String) -> Unit,
    canSend: Boolean,
    codeValid: Boolean,
    countDown: Int,
    buttonEnabled: Boolean,
    onSendCode: () -> Unit,
    onVerify: () -> Unit,
    title: String,
    subTitle: @Composable (() -> Unit)? = null,
    footer: @Composable (ColumnScope) -> Unit = {},
) {
    MaskModal(
        title = {
            Text(
                text = title,
            )
        },
        subTitle = subTitle
    ) {
        Column {
            Text(text = stringResource(R.string.scene_backup_validation_code))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MaskInputField(
                    modifier = Modifier.weight(1f),
                    value = code,
                    onValueChange = {
                        onCodeChange(it)
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Spacer(modifier = Modifier.width(8.dp))
                PrimaryButton(
                    onClick = {
                        onSendCode()
                    },
                    enabled = canSend && !buttonEnabled,
                ) {
                    if (canSend) {
                        Text(text = stringResource(R.string.common_controls_resend))
                    } else {
                        Text(text = countDown.toString() + "s")
                    }
                }
            }
            if (!codeValid) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.scene_restore_tip_invalid_validationcode), color = Color.Red)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.scene_backup_tips_phone))
                    append('\n')
                    withStyle(SpanStyle(color = MaterialTheme.colors.primary, fontWeight = FontWeight.Bold)) {
                        append(phone)
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onVerify()
                },
                enabled = code.isNotEmpty() && !buttonEnabled,
            ) {
                Text(text = stringResource(R.string.common_controls_confirm))
            }
            footer.invoke(this)
        }
    }
}
