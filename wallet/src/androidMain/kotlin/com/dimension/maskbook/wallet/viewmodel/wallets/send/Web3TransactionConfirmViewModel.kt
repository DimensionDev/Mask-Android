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
import com.dimension.maskbook.common.bigDecimal.BigDecimal
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.extension.export.ExtensionServices
import com.dimension.maskbook.extension.export.model.ExtensionResponseMessage
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.ext.hexWei
import com.dimension.maskbook.wallet.repository.ISendHistoryRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.SendTokenConfirmData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull

class Web3TransactionConfirmViewModel(
    private val data: SendTokenConfirmData,
    private val sendHistoryRepository: ISendHistoryRepository,
    private val walletRepository: IWalletRepository,
    private val extensionServices: ExtensionServices,
) : ViewModel() {

    val chainType = data.data.chainId?.let { chainId ->
        ChainType.values().firstOrNull { it.chainId == chainId }
    } ?: ChainType.eth

    init {
        walletRepository.setChainType(chainType)
    }

    fun send(
        gasLimit: Double,
        maxFee: Double,
        maxPriorityFee: Double,
    ) {
        data.data.to?.let { address ->
            walletRepository.transactionWithCurrentWalletAndChainType(
                amount = amount.value,
                address = address,
                chainType = chainType,
                gasLimit = gasLimit,
                maxFee = maxFee,
                maxPriorityFee = maxPriorityFee,
                data = data.data.data ?: "",
                onDone = {
                    it?.let {
                        extensionServices.sendJSEventResponse(
                            ExtensionResponseMessage.success(
                                data.messageId,
                                data.jsonrpc,
                                data.payloadId,
                                it
                            )
                        )
                    } ?: run {
                        extensionServices.sendJSEventResponse(
                            ExtensionResponseMessage.error(
                                data.messageId,
                                data.jsonrpc,
                                data.payloadId,
                                "Failed to send transaction"
                            )
                        )
                    }
                },
                onError = {
                    extensionServices.sendJSEventResponse(
                        ExtensionResponseMessage.error(
                            data.messageId,
                            data.jsonrpc,
                            data.payloadId,
                            it.message ?: "Failed to send transaction"
                        )
                    )
                }
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val nativeToken = walletRepository.currentChain.mapLatest { it?.nativeToken }

    val tokenData by lazy {
        combine(walletRepository.currentWallet, nativeToken) { wallet, nativeToken ->
            wallet?.tokens?.find {
                it.tokenData.address == data.data.to && it.tokenData.address.isNotEmpty()
            }?.tokenData ?: nativeToken
        }
    }

    val amount = MutableStateFlow(data.data.value?.hexWei?.ether ?: BigDecimal.ZERO)

    @OptIn(ExperimentalCoroutinesApi::class)
    val addressData by lazy {
        flow {
            emit(data.data.to)
        }.mapNotNull {
            it
        }.flatMapLatest {
            sendHistoryRepository.getOrCreateByAddress(it)
                .asStateIn(viewModelScope, null)
                .mapNotNull { it }
        }
    }

    fun cancel() {
        extensionServices.sendJSEventResponse(
            ExtensionResponseMessage.error(
                data.messageId,
                data.jsonrpc,
                data.payloadId,
                "Transaction cancelled"
            )
        )
    }
}
