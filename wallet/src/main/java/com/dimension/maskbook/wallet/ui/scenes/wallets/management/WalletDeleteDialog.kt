package com.dimension.maskbook.wallet.ui.scenes.wallets.management

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.dimension.maskbook.wallet.repository.WalletData
import com.dimension.maskbook.wallet.ui.widget.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WalletDeleteDialog(
    walletData: WalletData,
    biometricEnabled:Boolean,
    password: String,
    onPasswordChanged: (String) -> Unit,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    passwordValid: Boolean,
) {
    MaskDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onBack,
        icon = {
            WalletAvatar(
                modifier = Modifier.size(36.dp),
                walletData = walletData
            )
        },
        title = {
            Text(text = "Delete Wallet")
        },
        text = {
            Column {
                Text(text = walletData.address)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Are you sure you want to delete this wallet? Your wallet cannot be recoverd without seed phrase.")
            }
        },
        buttons = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnimatedVisibility(visible = !biometricEnabled) {
                    Text(text = "Payment password")
                    MaskPasswordInputField(value = password, onValueChange = onPasswordChanged)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SecondaryButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(text = "Cancel")
                    }
                    PrimaryButton(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        enabled = passwordValid || biometricEnabled
                    ) {
                        Text(text = "Delete")
                    }
                }
            }
        }
    )
}