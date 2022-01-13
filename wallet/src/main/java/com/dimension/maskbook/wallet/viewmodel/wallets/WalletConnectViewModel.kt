package com.dimension.maskbook.wallet.viewmodel.wallets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ChainType
import com.dimension.maskbook.wallet.repository.IWalletConnectRepository
import com.dimension.maskbook.wallet.repository.WCWallet
import com.dimension.maskbook.wallet.walletconnect.WalletConnectClientManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class WalletConnectViewModel(
    private val manager: WalletConnectClientManager,
    private val repository: IWalletConnectRepository,
    private val onResult: (success: Boolean) -> Unit,
) : ViewModel() {

    init {
        connect()
    }

    fun connect() {
        manager.connect(onResult = { success, wcUrl, accounts ->
            if (success) {
                // TODO saveWallets use wcUrl/accounts
            }
            onResult.invoke(success)
        })
    }

    val qrCode = manager.wcUrl.asStateIn(
        viewModelScope, ""
    )
    val wcUrl = manager.wcUrl.asStateIn(
        viewModelScope, ""
    )

    private val _selectedWallet = MutableStateFlow<WCWallet?>(null)

    private val _chainType = MutableStateFlow(ChainType.eth)

    fun selectChain(chainType: ChainType) {
        _chainType.value = chainType
    }

    fun retry() {
        // reset session
        connect()
    }

    private val _supportedWallets by lazy {
        repository.supportedWallets.asStateIn(
            viewModelScope, emptyList()
        )
    }

    val currentSupportedWallets = combine(_chainType, _supportedWallets) { type, wallets ->
        wallets.filter {
            it.isSupported(type)
        }
    }
}