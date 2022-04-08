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

import android.util.Log
import com.dimension.maskbook.common.exception.NullTransactionReceiptException
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.common.ext.decodeJson
import com.dimension.maskbook.common.ext.encodeJson
import com.dimension.maskbook.common.ext.toHexString
import com.dimension.maskbook.common.ext.use
import com.dimension.maskbook.common.ext.useSuspend
import com.dimension.maskbook.common.ext.web3j
import com.dimension.maskbook.common.util.EthUtils
import com.dimension.maskbook.common.util.SignUtils
import com.dimension.maskbook.labs.mapper.toRedPacketState
import com.dimension.maskbook.labs.mapper.toUiLuckyDropData
import com.dimension.maskbook.labs.model.RedPacketAvailabilityState
import com.dimension.maskbook.labs.model.options.RedPacketOptions
import com.dimension.maskbook.labs.model.ui.UiLuckyDropData
import com.dimension.maskbook.labs.util.RedPacketUtils
import com.dimension.maskbook.wallet.export.WalletServices
import com.dimension.maskbook.wallet.export.model.SendTransactionData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.web3j.abi.FunctionEncoder
import kotlin.time.Duration.Companion.seconds

class LuckDropViewModel(
    data: String,
    walletServices: WalletServices,
) : ViewModel() {

    private val redPacket = flow<RedPacketOptions> { emit(data.decodeJson()) }
    private val currentWallet = walletServices.currentWallet.filterNotNull()
    private val currentChain = walletServices.currentChain.filterNotNull()

    val stateData = combine(redPacket, currentWallet, currentChain) { redPacket, wallet, chain ->
        redPacket.toUiLuckyDropData(wallet, chain)
    }.flowOn(Dispatchers.IO).asStateIn(viewModelScope, UiLuckyDropData())

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun getSendTransactionData(stateData: UiLuckyDropData): String? {
        val wallet = stateData.wallet
        val redPacket = stateData.redPacket

        val data = when {
            redPacket.canClaim -> {
                val signMessage = SignUtils.signMessage(wallet.address, redPacket.password)
                RedPacketUtils.Functions.claim(redPacket.rpId, signMessage, wallet.address)
            }
            redPacket.canRefund -> {
                RedPacketUtils.Functions.refund(redPacket.rpId)
            }
            else -> return null
        }.let { FunctionEncoder.encode(it) }

        val gasLimit = wallet.chainType.web3j.use { web3j ->
            EthUtils.ethEstimateGas(
                web3j = web3j,
                fromAddress = wallet.address,
                contractAddress = redPacket.contractAddress,
                data = data,
                value = redPacket.amount.toBigInteger(),
            )
        }.onFailure {
            Log.w("LuckDropViewModel", it)
        }.getOrNull() ?: return null

        return SendTransactionData(
            from = wallet.address,
            to = redPacket.contractAddress,
            data = data,
            gasLimit = gasLimit.toHexString(),
            chainId = wallet.chainId,
        ).encodeJson()
    }

    suspend fun getRedPacketAvailabilityState(
        stateData: UiLuckyDropData,
        transactionHash: String,
    ): RedPacketAvailabilityState? {
        _loading.value = true
        var redPacketState: RedPacketAvailabilityState? = null

        val wallet = stateData.wallet
        val redPacket = stateData.redPacket

        wallet.chainType.web3j.useSuspend { web3j ->
            val transactionReceiptResponse = doWhileSuccess {
                EthUtils.ethGetTransactionReceipt(web3j, transactionHash)
            } ?: return@useSuspend

            // contract failure
            if (!transactionReceiptResponse.status) {
                return@useSuspend
            }

            val availability = doWhileSuccess {
                RedPacketUtils.checkAvailability(
                    web3j = web3j,
                    fromAddress = wallet.address,
                    contractAddress = redPacket.contractAddress,
                    rpId = redPacket.rpId,
                )
            } ?: return@useSuspend
            val state = availability.toRedPacketState()
            if ((redPacket.canClaim && state.isClaimed) ||
                (redPacket.canRefund && state.isRefunded)
            ) {
                redPacketState = state
            }
        }

        _loading.value = false
        return redPacketState
    }

    private suspend fun <T> doWhileSuccess(count: Int = 10, block: () -> Result<T>): T? {
        var index = 0
        while (index++ < count) {
            block().onSuccess {
                return it
            }.onFailure {
                if (it !is NullTransactionReceiptException) {
                    Log.w("LuckDropViewModel", it)
                }
            }
            delay(1.5.seconds)
        }
        return null
    }
}
