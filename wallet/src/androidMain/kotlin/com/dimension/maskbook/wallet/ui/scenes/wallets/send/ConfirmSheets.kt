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
package com.dimension.maskbook.wallet.ui.scenes.wallets.send

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.common.ui.widget.button.SecondaryButton
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.export.model.TokenData
import com.dimension.maskbook.wallet.export.model.TradableData
import com.dimension.maskbook.wallet.export.model.WalletCollectibleData
import com.dimension.maskbook.wallet.repository.SearchAddressData

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
        tradableData = tokenData,
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
    tokenData: TradableData,
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
        tradableData = tokenData,
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
    tradableData: TokenData,
    sendPrice: String,
    message: String,
    onSign: () -> Unit,
    onCancel: () -> Unit,
) {
    MaskModal(
        title = {
            Text(
                text = "Signature request",
            )
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            AddressAndTokenContent(
                addressData = addressData,
                tradableData = tradableData,
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
    tradableData: TradableData,
    sendPrice: String,
    gasFee: String,
    total: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onEditGasFee: () -> Unit,
) {
    MaskModal(
        title = {
            Text(
                text = title,
            )
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            AddressAndTokenContent(
                addressData = addressData,
                tradableData = tradableData,
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
    tradableData: TradableData,
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
    when (tradableData) {
        is TokenData -> TokenContent(tokenData = tradableData, sendPrice = sendPrice)
        is WalletCollectibleData -> CollectibleContent(collectibleData = tradableData)
    }
}

@Composable
private fun TokenContent(tokenData: TokenData, sendPrice: String) {
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
private fun CollectibleContent(
    collectibleData: WalletCollectibleData
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberImagePainter(collectibleData.icon),
            contentDescription = null,
            modifier = Modifier.size(38.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = collectibleData.name)
            Text(text = collectibleData.collection.name)
        }
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
