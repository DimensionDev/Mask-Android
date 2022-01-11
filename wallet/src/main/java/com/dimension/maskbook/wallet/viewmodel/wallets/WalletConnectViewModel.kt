package com.dimension.maskbook.wallet.viewmodel.wallets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.WalletConnectRepository
import com.dimension.maskbook.wallet.walletconnect.WalletConnectManager

class WalletConnectViewModel(
    private val manager: WalletConnectManager,
    private val repository: WalletConnectRepository,
): ViewModel() {
    val qrCode = manager.wcUrl.asStateIn(
        viewModelScope, ""
    )

    private val _supportedWallets = repository.supportedWallets.asStateIn(
        viewModelScope, ""
    )

    // TODO filter with ChainType

}