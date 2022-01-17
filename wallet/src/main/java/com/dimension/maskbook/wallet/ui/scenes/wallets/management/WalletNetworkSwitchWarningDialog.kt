package com.dimension.maskbook.wallet.ui.scenes.wallets.management

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.widget.MaskDialog
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.SecondaryButton

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WalletNetworkSwitchWarningDialog(
    currentNetwork: String,
    connectingNetwork: String,
    onCancel: () -> Unit,
    onSwitch: () -> Unit,
) {
    MaskDialog(
        onDismissRequest = onCancel,
        icon = {
            Image(
                painter = painterResource(id = R.drawable.ic_warn),
                contentDescription = "warning",
                Modifier.size(60.dp)
            )
        },
        text = {
            Column {
                Text(
                    text = buildAnnotatedString {
                        append("The current network ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("($currentNetwork)")
                        }
                        append(" is different from the connecting network ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("($connectingNetwork)")
                        }
                        append(". Do you want to switch your current network to ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(connectingNetwork)
                        }
                        append("?.")
                    },
                    style = MaterialTheme.typography.body1
                )
            }
        },
        buttons = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SecondaryButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(text = "Cancel")
                    }
                    PrimaryButton(
                        onClick = onSwitch,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(text = "Ok")
                    }
                }
            }
        }
    )
}