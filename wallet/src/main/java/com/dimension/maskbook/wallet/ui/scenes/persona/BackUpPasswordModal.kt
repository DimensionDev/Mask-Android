package com.dimension.maskbook.wallet.ui.scenes.persona

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.widget.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BackUpPasswordModal(
    biometricEnabled: Boolean,
    password: String,
    onPasswordChanged: (String) -> Unit,
    passwordValid: Boolean,
    onConfirm: () -> Unit,
) {
    MaskModal {
        Column(
            modifier = Modifier
                .padding(ScaffoldPadding)
                .animateContentSize(),
        ) {
            //TODO Biometrics replace UI
            Text(
                text = if (biometricEnabled) "Unlock with biometrics" else androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_set_backup_password_backup_password),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
            AnimatedVisibility(visible = !biometricEnabled) {
                Column {
                    MaskPasswordInputField(value = password, onValueChange = onPasswordChanged)
                    if (!passwordValid) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_change_password_incorrect_password), color = Color.Red)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                PrimaryButton(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = if (biometricEnabled) androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_wallet_unlock_button) else androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_confirm))
                }
            }
        }

    }
}
