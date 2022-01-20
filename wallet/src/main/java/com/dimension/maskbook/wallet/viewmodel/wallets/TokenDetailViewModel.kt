package com.dimension.maskbook.wallet.viewmodel.wallets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ITokenRepository
import com.dimension.maskbook.wallet.repository.ITransactionRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class TokenDetailViewModel(
    private val id: String,
    private val tokenRepository: ITokenRepository,
    private val transactionRepository: ITransactionRepository,
    private val walletRepository: IWalletRepository,
): ViewModel() {
    val dWebData by lazy {
        walletRepository.dWebData
    }
    val tokenData by lazy {
        tokenRepository.getTokenByAddress(id).asStateIn(viewModelScope, null).mapNotNull { it }
    }
    val walletTokenData by lazy {
        walletRepository.currentWallet.mapNotNull { it }.map {
            it.tokens.firstOrNull { it.tokenData.address == id }
        }.asStateIn(viewModelScope, null).mapNotNull { it }
    }
    val transaction by lazy {
        combine(walletRepository.currentWallet.mapNotNull { it }, tokenData) { wallet, token ->
            transactionRepository.getTransactionByToken(wallet, token)
        }
    }
}