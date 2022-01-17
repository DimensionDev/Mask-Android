package com.dimension.maskbook.wallet.ui.scenes.wallets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.widget.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UnlockWalletDialog(
    onBack: () -> Unit,
    biometricEnabled: Boolean,
    password: String,
    onPasswordChanged: (String) -> Unit,
    passwordValid: Boolean,
    onConfirm: () -> Unit,
) {
    MaskDialog(
        // workaround for https://issuetracker.google.com/issues/194911971
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(horizontal = 23.dp),
        onDismissRequest = onBack,
        title = {
            Text(text = stringResource(R.string.scene_wallet_unlock_title))
        },
        text = {
            AnimatedVisibility(visible = !biometricEnabled) {
                Column {
                    Text(text = stringResource(R.string.scene_setting_general_setup_payment_password))
                    Spacer(modifier = Modifier.height(8.dp))
                    MaskPasswordInputField(value = password, onValueChange = onPasswordChanged)
                    if (!passwordValid) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = stringResource(R.string.scene_change_password_incorrect_password), color = Color.Red)
                    }
                }
            }
        },
        buttons = {
            //TODO Biometrics replace UI
            Row {
                SecondaryButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.common_controls_cancel))
                }
                Spacer(modifier = Modifier.width(8.dp))
                PrimaryButton(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.scene_wallet_unlock_button))
                }
            }
        }
    )
}

@Composable
fun UnlockWalletWithTouchModal() {
    MaskModal {
        Column(
            modifier = Modifier
                .padding(ScaffoldPadding)
        ) {
            Image(
                painterResource(id = R.drawable.touch_id),
                contentDescription = null,
                modifier = Modifier.size(72.dp),
            )
            Spacer(modifier = Modifier.height(21.dp))
            Text(
                text = "Touch ID is required to continue",
                style = MaterialTheme.typography.subtitle1.copy(fontSize = 18.sp)
            )
            Spacer(modifier = Modifier.height(17.dp))
            Text(
                text = "Place your finger on the fingerprint sensor.",
                style = MaterialTheme.typography.caption.copy(fontSize = 16.sp),
            )
        }
    }
}
