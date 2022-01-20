package com.dimension.maskbook.wallet.viewmodel.wallets.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ChainType
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.WalletData
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class WalletSwitchViewModel(
    private val walletRepository: IWalletRepository
): ViewModel() {
    val network by lazy {
        walletRepository.dWebData.map { it.chainType }.asStateIn(viewModelScope, ChainType.eth)
    }

    fun setChainType(chainType: ChainType) {
        walletRepository.setChainType(networkType = chainType)
    }

    val currentWallet by lazy {
        walletRepository.currentWallet.asStateIn(viewModelScope, null).mapNotNull { it }
    }

    private val _wallets by lazy {
        walletRepository.wallets.asStateIn(viewModelScope, emptyList())
    }

    val wallets = combine(network, _wallets) { n, w ->
        w.filter { !it.fromWalletConnect || it.walletConnectChainType == n }
    }

    fun setCurrentWallet(data: WalletData) {
        walletRepository.setCurrentWallet(data)
    }

}