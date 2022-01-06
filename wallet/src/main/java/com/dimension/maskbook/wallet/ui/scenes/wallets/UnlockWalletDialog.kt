package com.dimension.maskbook.wallet.ui.scenes.wallets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.widget.*

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
        onDismissRequest = onBack,
        title = {
            Text(text = "Unlock Wallet")
        },
        text = {
            AnimatedVisibility(visible = !biometricEnabled) {
                Column {
                    Text(text = "Payment password")
                    Spacer(modifier = Modifier.height(8.dp))
                    MaskPasswordInputField(value = password, onValueChange = onPasswordChanged)
                    if (!passwordValid) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Incorrect Password.", color = Color.Red)
                    }
                }
            }
        },
        buttons = {
            Row {
                SecondaryButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Cancel")
                }
                PrimaryButton(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Unlock")
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
