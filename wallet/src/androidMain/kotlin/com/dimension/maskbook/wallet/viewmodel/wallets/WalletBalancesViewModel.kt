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
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.export.model.WalletData
import com.dimension.maskbook.wallet.repository.ICollectibleRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.BalancesSceneType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull

class WalletBalancesViewModel(
    private val repository: IWalletRepository,
    private val collectibleRepository: ICollectibleRepository,
) : ViewModel() {
    val wallets by lazy {
        repository.wallets.asStateIn(viewModelScope, emptyList())
    }
    val currentWallet by lazy {
        repository.currentWallet.asStateIn(viewModelScope, null)
    }
    val collectible by lazy {
        currentWallet.mapNotNull { it }.flatMapLatest { collectibleRepository.getCollectibleCollectionsByWallet(it) }
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

    private val _displayChainType = MutableStateFlow<ChainType?>(null)
    val displayChainType = _displayChainType.asStateIn(viewModelScope, null)

    fun setCurrentDisplayChainType(displayChainType: ChainType?) {
        _displayChainType.value = displayChainType
    }

    val showTokens by lazy {
        combine(_displayChainType, currentWallet) { chainType, wallet ->
            when {
                wallet == null -> emptyList()
                chainType == null -> wallet.tokens
                else -> wallet.tokens.filter {
                    it.tokenData.chainType === chainType
                }
            }.sortedByDescending {
                it.tokenData.price * it.count
            }
        }.flowOn(Dispatchers.IO).asStateIn(viewModelScope, emptyList())
    }
}
