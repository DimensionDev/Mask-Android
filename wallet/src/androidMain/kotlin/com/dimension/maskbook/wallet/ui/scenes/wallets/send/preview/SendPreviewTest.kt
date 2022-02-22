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
package com.dimension.maskbook.wallet.ui.scenes.wallets.send.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dimension.maskbook.common.bigDecimal.BigDecimal
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.export.model.TokenData
import com.dimension.maskbook.wallet.export.model.WalletTokenData
import com.dimension.maskbook.wallet.repository.GasPriceEditMode
import com.dimension.maskbook.wallet.repository.SearchAddressData
import com.dimension.maskbook.wallet.ui.scenes.wallets.send.AddContactSheet
import com.dimension.maskbook.wallet.ui.scenes.wallets.send.ApproveConfirmSheet
import com.dimension.maskbook.wallet.ui.scenes.wallets.send.EditGasPriceSheet
import com.dimension.maskbook.wallet.ui.scenes.wallets.send.SendConfirmSheet
import com.dimension.maskbook.wallet.ui.scenes.wallets.send.SignatureRequestSignSheet
import com.dimension.maskbook.wallet.ui.scenes.wallets.send.UnlockWalletDialog

/*TODO delete this class later*/

private val addressData = SearchAddressData(
    name = "Zhang",
    ens = "Zhang.eth",
    address = "0x12345",
    isContact = false
)

private val tokenData = TokenData(
    address = "0x12345",
    chainType = ChainType.eth,
    name = "Eth",
    symbol = "ETH",
    decimals = 10,
    logoURI = "https://downloads.coindesk.com/arc-hosted-images/eth.png",
    price = BigDecimal.valueOf(2000)
)

private val walletData = WalletTokenData(
    count = BigDecimal(10),
    tokenAddress = "0x12345",
    tokenData = tokenData,
)

// @Composable
// fun SendTokenScenePreview() {
//    SendTokenScene(
//        onBack = { /*TODO*/ },
//        addressData = addressData,
//        onAddContact = { /*TODO*/ },
//        tokenData = tokenData,
//        walletTokenData = walletData,
//        onSelectToken = { /*TODO*/ },
//        amount = 10f,
//        maxAmount = 10f,
//        onAmountChanged = {},
//        unlockWays = UnlockWays.PASSWORD,
//        gasFee = "5.5",
//        arrivesIn = "30 sec",
//        onEditGasFee = { /*TODO*/ },
//        onSend = {},
//        sendError = "Password error"
//    )
// }

@Composable
fun SearchTokenScenePreview() {
//    SearchTokenScene(onBack = { /*TODO*/ }, query = "", onQueryChanged = {})
}

@Composable
fun SendConfirmSheetPreview() {
    SendConfirmSheet(
        addressData = addressData,
        tokenData = tokenData,
        sendPrice = "280",
        gasFee = "0.0003",
        total = "300",
        onConfirm = { /*TODO*/ },
        onCancel = { /*TODO*/ }
    ) {
    }
}

@Composable
fun ApproveConfirmSheetPreview() {
    ApproveConfirmSheet(
        addressData = addressData,
        tokenData = tokenData,
        sendPrice = "280",
        gasFee = "0.0003",
        total = "300",
        onConfirm = { /*TODO*/ },
        onCancel = { /*TODO*/ }
    ) {
    }
}

@Composable
fun SignatureRequestSignSheetPreview() {
    SignatureRequestSignSheet(
        addressData = addressData,
        tokenData = tokenData,
        sendPrice = "280",
        onSign = { /*TODO*/ },
        onCancel = { /*TODO*/ },
        message = "I’m signing my one-time nonce: 20334\n" +
            "I’m signing my one-time nonce: 20334\n" +
            "I’m signing my one-time nonce: 20334"
    )
}

@Composable
fun UnlockWalletPreview() {
    UnlockWalletDialog(
        unlockError = stringResource(R.string.scene_change_password_incorrect_password),
        onCancel = { /*TODO*/ }
    ) {
    }
}

@Composable
fun EditGasPriceSheetPreview() {
    EditGasPriceSheet(
        price = "1.44",
        costFee = "0.0034",
        costFeeUnit = stringResource(R.string.chain_short_name_eth),
        arrivesIn = "30 Sec",
        mode = GasPriceEditMode.MEDIUM,
        gasLimit = "45000",
        onGasLimitChanged = {},
        maxPriorityFee = "4",
        onMaxPriorityFeeChanged = {},
        maxFee = "70",
        onMaxFeeChanged = {},
        onSelectMode = {},
        canConfirm = false,
        maxFeePrice = "1.5",
        maxPriorityFeePrice = "2.0",
        gasLimitError = "gas error",
        maxPriorityFeeError = "max priority error",
        maxFeeError = "max fee error"
    ) {
    }
}

@Composable
fun AddContactSheetPreview() {
    AddContactSheet(
        avatarLabel = "Z",
        address = "0x1234452354234234234",
        canConfirm = false,
        nameInput = "",
        onNameChanged = {}
    ) {
    }
}
