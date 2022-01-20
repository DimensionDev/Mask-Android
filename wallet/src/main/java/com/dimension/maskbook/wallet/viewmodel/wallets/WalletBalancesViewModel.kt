package com.dimension.maskbook.wallet.viewmodel.wallets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ICollectibleRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.WalletData
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.BalancesSceneType
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.DisplayAmountType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull

class WalletBalancesViewModel(
    private val repository: IWalletRepository,
    private val collectibleRepository: ICollectibleRepository,
): ViewModel() {
    val wallets by lazy {
        repository.wallets.asStateIn(viewModelScope, emptyList())
    }
    val currentWallet by lazy {
        repository.currentWallet.asStateIn(viewModelScope, null)
    }
    val collectible by lazy {
        currentWallet.mapNotNull { it }.flatMapLatest { collectibleRepository.getCollectiblesByWallet(it) }
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

    private val _displayAmountType = MutableStateFlow(DisplayAmountType.All)
    val displayAmountType = _displayAmountType.asStateIn(viewModelScope, DisplayAmountType.All)
    fun setCurrentDisplayAmountType(displayAmountType: DisplayAmountType) {
        _displayAmountType.value = displayAmountType
    }

}