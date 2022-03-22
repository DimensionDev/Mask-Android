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
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.ext.hexWei
import com.dimension.maskbook.wallet.handler.Web3SendResponse
import com.dimension.maskbook.wallet.repository.SendTokenConfirmData
import com.dimension.maskbook.wallet.usecase.Result
import com.dimension.maskbook.wallet.usecase.address.GetAddressUseCase
import com.dimension.maskbook.wallet.usecase.chain.SetCurrentChainUseCase
import com.dimension.maskbook.wallet.usecase.token.GetWalletNativeTokenUseCase
import com.dimension.maskbook.wallet.usecase.token.GetWalletTokenByAddressUseCase
import com.dimension.maskbook.wallet.usecase.token.SendTransactionUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

class Web3TransactionConfirmViewModel(
    private val data: SendTokenConfirmData,
    private val setCurrentChainUseCase: SetCurrentChainUseCase,
    private val getWalletTokenByAddressUseCase: GetWalletTokenByAddressUseCase,
    private val getWalletNativeTokenUseCase: GetWalletNativeTokenUseCase,
    private val getAddressUseCase: GetAddressUseCase,
    private val sendTransactionUseCase: SendTransactionUseCase,
    private val extensionServices: ExtensionServices,
) : ViewModel() {

    val chainType = data.data.chainId?.let { chainId ->
        ChainType.values().firstOrNull { it.chainId == chainId }
    } ?: ChainType.eth

    init {
        viewModelScope.launch {
            setCurrentChainUseCase(chainType).collect()
        }
    }

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateIn(viewModelScope)

    fun send(
        gasLimit: Double,
        maxFee: Double,
        maxPriorityFee: Double,
        onResult: (success: Boolean) -> Unit
    ) {
        data.data.to?.let { address ->
            viewModelScope.launch {
                sendTransactionUseCase(
                    amount = amount.value,
                    address = address,
                    chainType = chainType,
                    gasLimit = gasLimit,
                    maxFee = maxFee,
                    maxPriorityFee = maxPriorityFee,
                    data = data.data.data ?: "",
                ).collect {
                    when (it) {
                        is Result.Failed -> {
                            extensionServices.sendJSEventResponse(
                                Web3SendResponse.error(
                                    data.messageId,
                                    data.jsonrpc,
                                    data.payloadId,
                                    it.cause.message ?: "Failed to send transaction"
                                )
                            )
                            onResult.invoke(false)
                        }
                        is Result.Loading -> _loadingState.value = true
                        is Result.Success -> {
                            extensionServices.sendJSEventResponse(
                                Web3SendResponse.success(
                                    data.messageId,
                                    data.jsonrpc,
                                    data.payloadId,
                                    it
                                )
                            )
                            onResult.invoke(true)
                        }
                    }
                    if (it !is Result.Loading) _loadingState.value = false
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val tokenData by lazy {
        flow { emit(data.data.to) }
            .mapNotNull { it }
            .flatMapLatest {
                combine(
                    getWalletTokenByAddressUseCase(it),
                    getWalletNativeTokenUseCase(chainType)
                ) { token, native ->
                    when {
                        token is Result.Success -> {
                            token.value.tokenData
                        }
                        native is Result.Success -> {
                            native.value.tokenData
                        }
                        else -> {
                            null
                        }
                    }
                }
            }.asStateIn(viewModelScope, null)
    }

    val amount = MutableStateFlow(data.data.value?.hexWei?.ether ?: BigDecimal.ZERO)

    @OptIn(ExperimentalCoroutinesApi::class)
    val addressData by lazy {
        flow {
            emit(data.data.to)
        }.mapNotNull {
            it
        }.flatMapLatest {
            getAddressUseCase(address = it, addIfNotExists = true)
        }.map {
            when (it) {
                is Result.Success -> it.value
                else -> null
            }
        }.asStateIn(viewModelScope, null)
    }

    fun cancel() {
        extensionServices.sendJSEventResponse(
            Web3SendResponse.error(
                data.messageId,
                data.jsonrpc,
                data.payloadId,
                "Transaction cancelled"
            )
        )
    }
}
