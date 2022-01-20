package com.dimension.maskbook.wallet.viewmodel.wallets.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.TokenData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class SendTokenDataViewModel(
    initialTokenData: TokenData,
    private val walletRepository: IWalletRepository,
): ViewModel() {
    private val _tokenData = MutableStateFlow(initialTokenData)
    val tokenData = _tokenData.asStateIn(viewModelScope, initialTokenData)
    fun setTokenData(value: TokenData) {
        _tokenData.value = value
    }

    val walletTokens by lazy {
        walletRepository.currentWallet.mapNotNull { it }.map { it.tokens }
    }

    val walletTokenData by lazy {
        combine(walletRepository.currentWallet.mapNotNull { it }, tokenData) { wallet, token ->
            wallet.tokens.firstOrNull { it.tokenAddress == token.address }
        }.asStateIn(viewModelScope, null)
            .mapNotNull { it }
    }
}