package com.dimension.maskbook.wallet.ui.scenes.wallets.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.repository.WalletCreateOrImportResult
import com.dimension.maskbook.wallet.ui.widget.MaskDialog
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton

@Composable
fun WalletCreateOrImportResult.Dialog(onDismissRequest: () -> Unit) {
    MaskDialog(
        onDismissRequest = {
            onDismissRequest.invoke()
        },
        title = { Title() },
        icon = { Icon() },
        buttons = {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onDismissRequest.invoke()
                }
            ) {
                when (type) {
                    WalletCreateOrImportResult.Type.SUCCESS -> Text(text = "Done")
                    WalletCreateOrImportResult.Type.ERROR -> Text(text = "Ok")
                    WalletCreateOrImportResult.Type.WARNING -> Text(text = "Understood")
                }
            }
        },
        text = {
            if (!message.isNullOrEmpty()) Text(text = message)
        }
    )
}

@Composable
private fun WalletCreateOrImportResult.Title() = when (type) {
    WalletCreateOrImportResult.Type.SUCCESS -> Text(text = title ?: "Success")
    WalletCreateOrImportResult.Type.ERROR -> Text(text = title ?: "Error!")
    WalletCreateOrImportResult.Type.WARNING -> Text(text = title ?: "Warning")
}

@Composable
private fun WalletCreateOrImportResult.Icon() = when (type) {
    WalletCreateOrImportResult.Type.SUCCESS -> Image(
        painter = painterResource(id = R.drawable.ic_property_1_snccess),
        contentDescription = null
    )
    WalletCreateOrImportResult.Type.ERROR -> Image(
        painter = painterResource(id = R.drawable.ic_property_1_failed),
        contentDescription = null
    )
    WalletCreateOrImportResult.Type.WARNING -> Image(
        painter = painterResource(id = R.drawable.ic_property_1_note),
        contentDescription = null
    )
}