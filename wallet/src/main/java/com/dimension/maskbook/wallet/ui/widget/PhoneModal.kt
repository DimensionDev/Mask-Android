package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

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
            Spacer(modifier = Modifier.height(21.dp))
            Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_setting_profile_phone_number))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = regionCode,
                    onValueChange = { onRegionCodeChange(it) },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
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
                Text(text = "Incorrect Phone Number", color = Color.Red)
            }
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onConfirm()
                },
                enabled = phoneValid && !buttonEnabled && phone.isNotEmpty(),
            ) {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_confirm))
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
            Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_backup_validation_code))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
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
                        Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_resend))
                    } else {
                        Text(text = countDown.toString() + "s")
                    }
                }
            }
            if (!codeValid) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Incorrect Code", color = Color.Red)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Verification code has been sent to by SMS to $phone"
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onVerify()
                },
                enabled = code.isNotEmpty() && !buttonEnabled,
            ) {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_confirm))
            }
            footer.invoke(this)
        }
    }
}