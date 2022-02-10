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
package com.dimension.maskbook.wallet.viewmodel.wallets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.common.ext.asStateIn
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
) : ViewModel() {
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
