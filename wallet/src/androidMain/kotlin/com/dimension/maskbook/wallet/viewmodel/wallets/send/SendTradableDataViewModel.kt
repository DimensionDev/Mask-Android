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
import com.dimension.maskbook.wallet.export.model.TradableData
import com.dimension.maskbook.wallet.export.model.WalletCollectibleData
import com.dimension.maskbook.wallet.repository.ICollectibleRepository
import com.dimension.maskbook.wallet.repository.ITokenRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class SendTradableDataViewModel(
    tradableId: String,
    private val walletRepository: IWalletRepository,
    private val tokenRepository: ITokenRepository,
    private val collectibleRepository: ICollectibleRepository
) : ViewModel() {

    private val _tokenData = MutableStateFlow<TokenData?>(null)
    val tokenData by lazy {
        _tokenData.map {
            it ?: if (tradableId.isNotEmpty())
                tokenRepository.getTokenByAddress(tradableId).firstOrNull()
            else {
                walletTokens.firstOrNull()?.firstOrNull()?.tokenData
            } ?: walletRepository.currentChain.firstOrNull()?.nativeToken
        }.asStateIn(viewModelScope, null)
    }

    private val _collectibleData = MutableStateFlow<WalletCollectibleData?>(null)
    val collectibleData by lazy {
        _collectibleData.map {
            it ?: if (tradableId.isNotEmpty())
                collectibleRepository.getCollectibleById(tradableId).firstOrNull()
            else {
                null
            }
        }.asStateIn(viewModelScope, null)
    }

    val tradableData = combine(collectibleData, tokenData) { collectible, token ->
        collectible ?: token
    }

    fun setData(value: TradableData) {
        when (value) {
            is TokenData -> {
                _tokenData.value = value
                _collectibleData.value = null
                walletRepository.setChainType(value.chainType)
            }
            is WalletCollectibleData -> {
                _collectibleData.value = value
                _tokenData.value = null
                walletRepository.setChainType(value.chainType)
            }
        }
    }

    val walletTokens by lazy {
        combine(walletRepository.currentWallet, walletRepository.dWebData) { wallet, dWebData ->
            wallet?.tokens?.filter { it.tokenData.chainType == dWebData.chainType } ?: emptyList()
        }
    }

    val walletTokenData by lazy {
        combine(
            walletRepository.currentWallet.filterNotNull(),
            tokenData.filterNotNull()
        ) { wallet, token ->
            wallet.tokens.firstOrNull { it.tokenAddress == token.address }
        }.asStateIn(viewModelScope, null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val walletCollectibleCollections by lazy {
        walletRepository.currentWallet.filterNotNull().flatMapLatest {
            collectibleRepository.getCollectibleCollectionsByWallet(it)
        }
    }

    enum class TransactionType {
        Token,
        Collectible
    }
}
