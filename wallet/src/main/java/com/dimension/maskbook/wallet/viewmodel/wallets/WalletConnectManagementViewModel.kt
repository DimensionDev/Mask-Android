package com.dimension.maskbook.wallet.viewmodel.wallets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.repository.*
import com.dimension.maskbook.wallet.walletconnect.WalletConnectClientManager
import kotlinx.coroutines.launch

class WalletConnectManagementViewModel(
    private val manager: WalletConnectClientManager,
    private val walletRepository: IWalletRepository
) : ViewModel() {
    fun disconnect(walletData: WalletData) {
        viewModelScope.launch {
            if (!walletData.fromWalletConnect) return@launch
            // if session is no longer exists, we have to delete this wallet manually
            if (!manager.disConnect(address = walletData.address)) {
                walletRepository.deleteWallet(walletData.id)
            }
        }
    }
}