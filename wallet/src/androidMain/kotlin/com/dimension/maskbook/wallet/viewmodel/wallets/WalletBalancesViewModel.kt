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

import com.dimension.maskbook.common.bigDecimal.BigDecimal
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.common.ext.humanizeDollar
import com.dimension.maskbook.common.ext.onFinished
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.export.model.TokenData
import com.dimension.maskbook.wallet.export.model.WalletData
import com.dimension.maskbook.wallet.export.model.WalletTokenData
import com.dimension.maskbook.wallet.repository.ICollectibleRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.ui.scenes.wallets.management.BalancesSceneType
import com.dimension.maskbook.wallet.usecase.GetWalletNativeTokenUseCase
import com.dimension.maskbook.wallet.usecase.RefreshWalletUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class WalletBalancesViewModel(
    private val repository: IWalletRepository,
    private val collectibleRepository: ICollectibleRepository,
    private val refreshWalletUseCase: RefreshWalletUseCase,
    private val getWalletNativeToken: GetWalletNativeTokenUseCase
) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.currentChain
                .distinctUntilChanged()
                .collect { chain ->
                    chain?.let {
                        _displayChainType.value = it.chainType
                    }
                }
        }
    }

    val wallets by lazy {
        repository.wallets.asStateIn(viewModelScope, emptyList())
    }
    val currentWallet by lazy {
        repository.currentWallet.asStateIn(viewModelScope, null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val collectible by lazy {
        currentWallet.mapNotNull { it }
            .flatMapLatest { collectibleRepository.getCollectibleCollectionsByWallet(it) }
    }
    val dWebData by lazy {
        repository.dWebData.asStateIn(viewModelScope, null)
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
        if (displayChainType != null) {
            repository.setChainType(displayChainType, true)
        }
    }

    private val _refreshingWallet = MutableStateFlow(false)
    val refreshingWallet = _refreshingWallet.asStateIn(viewModelScope)
    fun refreshWallet() {
        _refreshingWallet.value = true
        viewModelScope.launch {
            refreshWalletUseCase().onFinished { _refreshingWallet.value = false }
        }
    }

    private val chainTokenData = _displayChainType.map {
        if (it != null) repository.getChainTokenData(it) else null
    }

    private val _showTokens = combine(chainTokenData, currentWallet) { chainTokenData, wallet ->
        val list = when {
            wallet == null -> emptyList()
            chainTokenData == null -> wallet.tokens
            else -> wallet.tokens.filter {
                it.tokenData.chainType === chainTokenData.chainType
            }
        }.sortedByDescending {
            it.tokenData.price * it.count
        }

        list.filterNot { it.isExpandable(chainTokenData?.nativeToken) } to
            list.filter { it.isExpandable(chainTokenData?.nativeToken) }
    }.flowOn(Dispatchers.IO).shareIn(
        viewModelScope,
        SharingStarted.Lazily
    )

    val showTokens by lazy {
        _showTokens.map { it.first }.asStateIn(viewModelScope, emptyList())
    }

    val showTokensLess by lazy {
        _showTokens.map { it.second }.asStateIn(viewModelScope, emptyList())
    }

    val showTokensLessAmount by lazy {
        showTokensLess.map { list ->
            list.sumOf { it.tokenData.price * it.count }.humanizeDollar()
        }.asStateIn(viewModelScope, "")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val walletNativeToken = currentWallet.flatMapLatest {
        dWebData
    }.flatMapLatest {
        getWalletNativeToken(it?.chainType)
    }.asStateIn(viewModelScope, null)

    companion object {

        private fun WalletTokenData.isExpandable(mainTokenData: TokenData?): Boolean {
            return (mainTokenData == null || tokenData.address != mainTokenData.address) && isLess()
        }

        private fun WalletTokenData.isLess(): Boolean {
            return tokenData.price * count < lessTokenDataPrice
        }

        private val lessTokenDataPrice = BigDecimal(10)
    }
}
