package com.dimension.maskbook.wallet.ui.scenes.wallets.send.preview

import androidx.compose.runtime.Composable
import com.dimension.maskbook.wallet.repository.GasPriceEditMode
import com.dimension.maskbook.wallet.repository.SearchAddressData
import com.dimension.maskbook.wallet.repository.TokenData
import com.dimension.maskbook.wallet.repository.WalletTokenData
import com.dimension.maskbook.wallet.ui.scenes.wallets.send.*
import java.math.BigDecimal

/*TODO delete this class later*/

private val addressData = SearchAddressData(
    name = "Zhang",
    ens = "Zhang.eth",
    address = "0x12345",
    isContact = false
)

private val tokenData = TokenData(
    address = "0x12345",
    chainId = "xxx",
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

//@Composable
//fun SendTokenScenePreview() {
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
//}

@Composable
fun SearchTokenScenePreview() {
    SearchTokenScene(onBack = { /*TODO*/ }, query = "", onQueryChanged = {})
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
        onCancel = { /*TODO*/ }) {

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
        onCancel = { /*TODO*/ }) {

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
        unlockError = "Incorrect Password.",
        onCancel = { /*TODO*/ }
    ) {

    }
}

@Composable
fun EditGasPriceSheetPreview() {
    EditGasPriceSheet(
        price = "1.44",
        costFee = "0.0034",
        costFeeUnit = "Eth",
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