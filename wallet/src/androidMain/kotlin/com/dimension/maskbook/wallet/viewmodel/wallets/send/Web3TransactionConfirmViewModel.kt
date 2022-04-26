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

import com.dimension.maskbook.common.bigDecimal.BigDecimal
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.common.ext.onFinished
import com.dimension.maskbook.extension.export.ExtensionServices
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.export.model.SendTransactionData
import com.dimension.maskbook.wallet.ext.hexWei
import com.dimension.maskbook.wallet.handler.Web3SendResponse
import com.dimension.maskbook.wallet.model.SendTokenRequest
import com.dimension.maskbook.wallet.usecase.GetAddressUseCase
import com.dimension.maskbook.wallet.usecase.GetWalletNativeTokenUseCase
import com.dimension.maskbook.wallet.usecase.GetWalletTokenByAddressUseCase
import com.dimension.maskbook.wallet.usecase.SendTransactionUseCase
import com.dimension.maskbook.wallet.usecase.SetCurrentChainUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class Web3TransactionConfirmViewModel(
    private val data: SendTransactionData,
    private val request: SendTokenRequest?,
    private val setCurrentChain: SetCurrentChainUseCase,
    private val getWalletTokenByAddress: GetWalletTokenByAddressUseCase,
    private val getWalletNativeToken: GetWalletNativeTokenUseCase,
    private val getAddress: GetAddressUseCase,
    private val sendTransaction: SendTransactionUseCase,
    private val extensionServices: ExtensionServices,
) : ViewModel() {

    val chainType = data.chainId?.let { chainId ->
        ChainType.values().firstOrNull { it.chainId == chainId }
    } ?: ChainType.eth

    init {
        viewModelScope.launch {
            setCurrentChain(chainType)
        }
    }

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateIn(viewModelScope)

    fun send(
        gasLimit: Double,
        maxFee: Double,
        maxPriorityFee: Double,
        onResult: (transactionHash: String?) -> Unit
    ) {
        _loadingState.value = true
        data.to?.let { address ->
            viewModelScope.launch {
                sendTransaction(
                    amount = amount.value,
                    address = address,
                    chainType = chainType,
                    gasLimit = gasLimit,
                    maxFee = maxFee,
                    maxPriorityFee = maxPriorityFee,
                    data = data.data ?: "",
                ).onSuccess { transactionHash ->
                    request?.let { request ->
                        extensionServices.sendBackgroundJSEventResponse(
                            Web3SendResponse.success(
                                request.messageId,
                                request.jsonrpc,
                                request.payloadId,
                                transactionHash
                            )
                        )
                    }
                    onResult.invoke(transactionHash)
                }.onFailure {
                    request?.let { request ->
                        extensionServices.sendBackgroundJSEventResponse(
                            Web3SendResponse.error(
                                request.messageId,
                                request.jsonrpc,
                                request.payloadId,
                                it.message ?: "Failed to send transaction"
                            )
                        )
                    }
                    onResult.invoke(null)
                }.onFinished {
                    _loadingState.value = false
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val tokenData by lazy {
        flow { emit(data.to) }
            .mapNotNull { it }
            .flatMapLatest {
                combine(
                    getWalletTokenByAddress(it),
                    getWalletNativeToken(chainType)
                ) { token, native ->
                    token ?: native
                }
            }.mapNotNull {
                it?.tokenData
            }.asStateIn(viewModelScope, null)
    }

    val amount = MutableStateFlow(data.value?.hexWei?.ether ?: BigDecimal.ZERO)

    @OptIn(ExperimentalCoroutinesApi::class)
    val addressData by lazy {
        flow {
            emit(data.to)
        }.mapNotNull {
            it
        }.flatMapLatest {
            getAddress(address = it, addIfNotExists = true)
        }.asStateIn(viewModelScope, null)
    }

    fun cancel() {
        request?.let { request ->
            extensionServices.sendBackgroundJSEventResponse(
                Web3SendResponse.error(
                    request.messageId,
                    request.jsonrpc,
                    request.payloadId,
                    "Cancel"
                )
            )
        }
    }
}
