/*
 *  Mask-Android
 *
 *  Copyright (C) 2022  DimensionDev and Contributors
 *
 *  This file is part of Mask-Android.
 *
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Mask-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.wallet.viewmodel.wallets.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.wallet.export.model.TokenData
import com.dimension.maskbook.wallet.repository.ITokenRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
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
) : ViewModel() {

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
