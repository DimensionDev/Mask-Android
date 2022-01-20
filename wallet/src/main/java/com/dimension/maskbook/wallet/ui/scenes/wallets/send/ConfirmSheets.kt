package com.dimension.maskbook.wallet.ui.scenes.wallets.send

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.repository.SearchAddressData
import com.dimension.maskbook.wallet.repository.TokenData
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.ui.widget.SecondaryButton

@Composable
fun ApproveConfirmSheet(
    addressData: SearchAddressData,
    tokenData: TokenData,
    sendPrice: String,
    gasFee: String,
    total: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onEditGasFee: () -> Unit,
) {
    ConfirmSheet(
        title = stringResource(R.string.scene_wallet_balance_transaction_approve),
        addressData = addressData,
        tokenData = tokenData,
        sendPrice = sendPrice,
        gasFee = gasFee,
        total = total,
        onConfirm = onConfirm,
        onCancel = onCancel,
        onEditGasFee = onEditGasFee
    )
}

@Composable
fun SendConfirmSheet(
    addressData: SearchAddressData,
    tokenData: TokenData,
    sendPrice: String,
    gasFee: String,
    total: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onEditGasFee: () -> Unit,
) {
    ConfirmSheet(
        title = stringResource(R.string.scene_wallet_balance_btn_Send),
        addressData = addressData,
        tokenData = tokenData,
        sendPrice = sendPrice,
        gasFee = gasFee,
        total = total,
        onConfirm = onConfirm,
        onCancel = onCancel,
        onEditGasFee = onEditGasFee
    )
}

@Composable
fun SignatureRequestSignSheet(
    addressData: SearchAddressData,
    tokenData: TokenData,
    sendPrice: String,
    message: String,
    onSign: () -> Unit,
    onCancel: () -> Unit,
) {
    MaskModal {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(ScaffoldPadding),
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Signature request",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(20.dp))
            AddressAndTokenContent(
                addressData = addressData,
                tokenData = tokenData,
                sendPrice = sendPrice
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Message:")
                Text(text = message, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(20.dp))
            ButtonContent(onCancel = onCancel, onConfirm = onSign, confirmText = "Sign")
        }
    }
}



@Composable
private fun ConfirmSheet(
    title: String,
    addressData: SearchAddressData,
    tokenData: TokenData,
    sendPrice: String,
    gasFee: String,
    total: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onEditGasFee: () -> Unit,
) {
    MaskModal {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(ScaffoldPadding),
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(20.dp))
            AddressAndTokenContent(
                addressData = addressData,
                tokenData = tokenData,
                sendPrice = sendPrice
            )
            Spacer(modifier = Modifier.height(20.dp))
            GasFeeAndTotalContent(
                gasFee = gasFee,
                onEditGasFee = onEditGasFee,
                total = total
            )
            Spacer(modifier = Modifier.height(20.dp))
            ButtonContent(onCancel = onCancel, onConfirm = onConfirm)
        }
    }
}

@Composable
private fun ColumnScope.AddressAndTokenContent(
    addressData: SearchAddressData,
    tokenData: TokenData,
    sendPrice: String
) {
    Text(
        text = addressData.name ?: addressData.ens ?: "",
        style = MaterialTheme.typography.subtitle1,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = addressData.address,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )
    Spacer(modifier = Modifier.height(20.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberImagePainter(tokenData.logoURI),
            contentDescription = null,
            modifier = Modifier.size(38.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = tokenData.symbol, modifier = Modifier.weight(1f))
        Text(text = sendPrice)
    }
}

@Composable
private fun ColumnScope.GasFeeAndTotalContent(
    gasFee: String,
    onEditGasFee: () -> Unit,
    total: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(R.string.scene_sendTransaction_sendConfirmPop_gasFee), modifier = Modifier.weight(1f))
        Text(text = gasFee)
        TextButton(onClick = onEditGasFee) {
            Text(text = stringResource(R.string.scene_sendTransaction_sendConfirmPop_edit), color = MaterialTheme.colors.primary)
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(R.string.scene_sendTransaction_sendConfirmPop_total), modifier = Modifier.weight(1f))
        Text(text = total)
    }
}

@Composable
private fun ColumnScope.ButtonContent(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    confirmText: String = stringResource(R.string.common_controls_confirm)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SecondaryButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f),
        ) {
            Text(text = stringResource(R.string.common_controls_cancel))
        }
        Spacer(modifier = Modifier.width(20.dp))
        PrimaryButton(
            modifier = Modifier.weight(1f),
            onClick = onConfirm,
        ) {
            Text(text = confirmText)
        }
    }
}
