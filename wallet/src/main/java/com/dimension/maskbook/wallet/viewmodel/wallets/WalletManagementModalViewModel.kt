package com.dimension.maskbook.wallet.viewmodel.wallets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IWalletRepository

class WalletManagementModalViewModel(
    private val repository: IWalletRepository,
): ViewModel() {
    val currentWallet by lazy {
        repository.currentWallet.asStateIn(viewModelScope, null)
    }
}