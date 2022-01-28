package com.dimension.maskbook.wallet.viewmodel.wallets.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ITokenRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.TokenData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import java.math.BigDecimal

class SendTokenDataViewModel(
    tokenAddress: String,
    private val walletRepository: IWalletRepository,
    tokenRepository: ITokenRepository,
): ViewModel() {

    private val _tokenData = MutableStateFlow<TokenData?>(null)

    val tokenData = merge(
        tokenRepository.getTokenByAddress(tokenAddress),
        _tokenData,
    ).asStateIn(viewModelScope, null)

    val noTokenFound by lazy {
        walletRepository.currentWallet
            .map { wallet ->
                if (wallet == null) return@map true
                val token = wallet.tokens.find { it.tokenData.address == tokenAddress } ?: return@map true
                token.count == BigDecimal.ZERO
            }
            .asStateIn(viewModelScope, false)
    }

    fun setTokenData(value: TokenData) {
        _tokenData.value = value
    }

    val walletTokens by lazy {
        walletRepository.currentWallet.mapNotNull { it }.map { it.tokens }
    }

    val walletTokenData by lazy {
        combine(
            walletRepository.currentWallet.filterNotNull(),
            tokenData.filterNotNull()
        ) { wallet, token ->
            wallet.tokens.firstOrNull { it.tokenAddress == token.address }
        }.asStateIn(viewModelScope, null)
    }
}