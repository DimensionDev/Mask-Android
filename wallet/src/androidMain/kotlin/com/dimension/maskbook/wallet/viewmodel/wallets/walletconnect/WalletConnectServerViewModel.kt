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
package com.dimension.maskbook.wallet.viewmodel.wallets.walletconnect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.walletconnect.WCClientMeta
import com.dimension.maskbook.wallet.walletconnect.WalletConnectServerManager
import kotlinx.coroutines.flow.MutableStateFlow

class WalletConnectServerViewModel(
    private val uri: String,
    private val manager: WalletConnectServerManager,
    private val repository: IWalletRepository
) : ViewModel() {
    private val _client = MutableStateFlow<WCClientMeta?>(null)
    val client = _client.asStateIn(viewModelScope, null)

    init {
        connect(uri)
    }

    val currentWallet by lazy {
        repository.currentWallet
            .asStateIn(viewModelScope, null)
    }

    val currentChain by lazy {
        repository.currentChain
            .asStateIn(viewModelScope, null)
    }

    private fun connect(uri: String) {
        manager.connectClient(uri) {
            _client.value = it
        }
    }

    fun approve() {
        _client.value?.let { client ->
            currentWallet.value?.let { wallet ->
                manager.approveConnect(
                    clientMeta = client,
                    accounts = listOf(wallet.address),
                    chainId = currentChain.value?.chainId ?: ChainType.eth.chainId
                )
            }
        }
    }

    fun reject() {
        _client.value?.let {
            manager.rejectConnect(it)
        }
    }
}
