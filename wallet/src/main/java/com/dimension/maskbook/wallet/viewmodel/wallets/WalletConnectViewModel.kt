package com.dimension.maskbook.wallet.viewmodel.wallets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.walletconnect.WalletConnectManager

class WalletConnectViewModel(
    private val manager: WalletConnectManager
): ViewModel() {
    val qrCode = manager.wcUrl.asStateIn(
        viewModelScope, ""
    )

    // TODO get supported wallet
}