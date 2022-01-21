package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R

@Composable
fun PhoneInputModal(
    regionCode: String,
    onRegionCodeChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    phoneValid: Boolean,
    onConfirm: () -> Unit,
    buttonEnabled: Boolean,
    title: String,
) {
    MaskModal(
        title = {
            Text(text = title)
        }
    ) {
        Column(Modifier.padding(ScaffoldPadding)) {
            Text(text = stringResource(R.string.scene_setting_profile_phone_number))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                MaskInputField(
                    modifier = Modifier.weight(1f),
                    value = regionCode,
                    onValueChange = { onRegionCodeChange(it) },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                )
                Spacer(modifier = Modifier.width(8.dp))
                MaskInputField(
                    modifier = Modifier.weight(4f),
                    value = phone,
                    onValueChange = {
                        onPhoneChange(it)
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                )
            }
            if (!phoneValid) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.scene_restore_tip_invalid_mobile_number), color = Color.Red)
            }
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onConfirm()
                },
                enabled = phoneValid && !buttonEnabled && phone.isNotEmpty(),
            ) {
                Text(text = stringResource(R.string.common_controls_confirm))
            }
        }
    }
}

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
    subTitle: @Composable ((ColumnScope) -> Unit)? = null,
    footer: @Composable (ColumnScope) -> Unit = {},
) {
    MaskModal {
        Column(
            modifier = Modifier.padding(ScaffoldPadding)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h6,
            )
            subTitle?.let {
                Spacer(modifier = Modifier.height(13.dp))
                it.invoke(this)
            }
            Spacer(modifier = Modifier.height(21.dp))
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
                    append(stringResource(com.dimension.maskbook.wallet.R.string.scene_backup_tips_phone))
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