package com.dimension.maskbook.wallet.viewmodel.wallets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.WalletData
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.BalancesSceneType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull

class WalletBalancesViewModel(
    private val repository: IWalletRepository,
): ViewModel() {
    val wallets by lazy {
        repository.wallets.asStateIn(viewModelScope, emptyList())
    }
    val currentWallet by lazy {
        repository.currentWallet.asStateIn(viewModelScope, null)
    }
    val dWebData by lazy {
        repository.dWebData.asStateIn(viewModelScope, null).mapNotNull { it }
    }
    private val _sceneType = MutableStateFlow(BalancesSceneType.Token)
    val sceneType = _sceneType.asStateIn(viewModelScope, BalancesSceneType.Token)
    fun setSceneType(value: BalancesSceneType) {
        _sceneType.value = value
    }
    fun setCurrentWallet(walletData: WalletData) {
        repository.setCurrentWallet(walletData = walletData)
    }

}