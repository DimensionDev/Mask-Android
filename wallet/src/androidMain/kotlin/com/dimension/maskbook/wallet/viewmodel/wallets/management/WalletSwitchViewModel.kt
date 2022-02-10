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
package com.dimension.maskbook.wallet.viewmodel.wallets.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.export.model.WalletData
import com.dimension.maskbook.wallet.repository.IWalletRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class WalletSwitchViewModel(
    private val walletRepository: IWalletRepository
) : ViewModel() {
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
