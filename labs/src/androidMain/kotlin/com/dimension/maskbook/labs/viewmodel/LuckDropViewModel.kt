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
package com.dimension.maskbook.labs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.common.ext.decodeJson
import com.dimension.maskbook.common.ext.encodeJson
import com.dimension.maskbook.labs.mapper.toUiLuckyDropData
import com.dimension.maskbook.labs.model.options.RedPacketOptions
import com.dimension.maskbook.labs.model.ui.UiLuckyDropData
import com.dimension.maskbook.labs.util.RedPacketFunctions
import com.dimension.maskbook.wallet.export.WalletServices
import com.dimension.maskbook.wallet.export.model.SendTransactionData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LuckDropViewModel(
    data: String,
    private val walletServices: WalletServices,
) : ViewModel() {

    private val redPacket = flow<RedPacketOptions> { emit(data.decodeJson()) }
    private val currentWallet = walletServices.currentWallet.filterNotNull()
    private val currentChain = walletServices.currentChain.filterNotNull()

    val stateData = combine(redPacket, currentWallet, currentChain) { redPacket, wallet, chain ->
        redPacket.toUiLuckyDropData(wallet, chain)
    }.flowOn(Dispatchers.IO).asStateIn(viewModelScope, UiLuckyDropData())

    suspend fun getSendTransactionData(): String? {
        val stateData = stateData.firstOrNull() ?: return null
        val wallet = stateData.wallet
        val redPacket = stateData.redPacket

        val data = when {
            redPacket.canSend -> {
                val signMessage = walletServices.signMessage(wallet.address, redPacket.password)
                RedPacketFunctions.claim(redPacket.rpId, signMessage, wallet.address)
            }
            redPacket.canRefund -> {
                RedPacketFunctions.refund(redPacket.rpId)
            }
            else -> return null
        }

        return SendTransactionData(
            from = wallet.address,
            to = redPacket.address,
            data = data,
            gas = null,
            maxFee = null,
            maxPriorityFee = null,
            chainId = wallet.chainId,
        ).encodeJson()
    }
}
