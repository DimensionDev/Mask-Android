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
import com.dimension.maskbook.common.ext.onFinished
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.ext.gwei
import com.dimension.maskbook.wallet.ext.humanizeMinutes
import com.dimension.maskbook.wallet.repository.GasPriceEditMode
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.usecase.GasFeeData
import com.dimension.maskbook.wallet.usecase.GasFeeModel
import com.dimension.maskbook.wallet.usecase.GetArrivesWithGasFeeUseCase
import com.dimension.maskbook.wallet.usecase.GetSuggestGasFeeUseCase
import com.dimension.maskbook.wallet.usecase.GetWalletNativeTokenUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import java.math.BigDecimal

class GasFeeViewModel(
    initialGasLimit: Double = 21000.0,
    private val getSuggestGasFee: GetSuggestGasFeeUseCase,
    private val getArrivesWithGasFee: GetArrivesWithGasFeeUseCase,
    private val getWalletNativeToken: GetWalletNativeTokenUseCase,
    private val walletRepository: IWalletRepository,
) : ViewModel() {
    private val chainType = walletRepository.currentChain.mapNotNull {
        it?.chainType
    }.asStateIn(viewModelScope, ChainType.eth)

    // native token on current chain
    @OptIn(ExperimentalCoroutinesApi::class)
    private val nativeToken by lazy {
        chainType.flatMapLatest {
            getWalletNativeToken(it)
        }.map {
            it?.tokenData
        }
    }

    private val _gasPriceEditMode = MutableStateFlow(GasPriceEditMode.MEDIUM)
    val gasPriceEditMode = _gasPriceEditMode.asStateIn(viewModelScope, GasPriceEditMode.MEDIUM)
    fun setGasPriceEditMode(value: GasPriceEditMode) {
        _gasPriceEditMode.value = value
    }

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateIn(viewModelScope)

    private val _suggestGasFee = MutableStateFlow(GasFeeModel())

    init {
        refreshSuggestGasFee()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun refreshSuggestGasFee() {
        _loadingState.value = true
        viewModelScope.launch {
            getSuggestGasFee(chainType.value)
                .onSuccess { _suggestGasFee.value = it }
                .onFinished { _loadingState.value = false }
        }
    }

    private val _gasLimit = MutableStateFlow(initialGasLimit)
    val gasLimit = _gasLimit.asStateIn(viewModelScope, -1.0)

    fun setGasLimit(value: Double) {
        _gasLimit.value = value
    }

    private val _maxPriorityFeePerGas = MutableStateFlow(-1.0)
    val maxPriorityFeePerGas =
        combine(_maxPriorityFeePerGas, _suggestGasFee, _gasPriceEditMode) { max, suggest, mode ->
            if (max != -1.0) {
                max
            } else {
                when (mode) {
                    GasPriceEditMode.LOW -> suggest.low.maxPriorityFeePerGas
                    GasPriceEditMode.MEDIUM -> suggest.medium.maxPriorityFeePerGas
                    GasPriceEditMode.HIGH -> suggest.high.maxPriorityFeePerGas
                    GasPriceEditMode.CUSTOM -> max
                }
            }
        }.asStateIn(viewModelScope, -1.0)

    fun setMaxPriorityFee(value: Double) {
        _maxPriorityFeePerGas.value = value
    }

    private val _maxFeePerGas = MutableStateFlow(-1.0)
    val maxFeePerGas =
        combine(_maxFeePerGas, _suggestGasFee, _gasPriceEditMode) { max, suggest, mode ->
            if (max != -1.0) {
                max
            } else {
                when (mode) {
                    GasPriceEditMode.LOW -> suggest.low.maxFeePerGas
                    GasPriceEditMode.MEDIUM -> suggest.medium.maxFeePerGas
                    GasPriceEditMode.HIGH -> suggest.high.maxFeePerGas
                    GasPriceEditMode.CUSTOM -> max
                }
            }
        }.asStateIn(viewModelScope, -1.0)

    fun setMaxFee(value: Double) {
        _maxFeePerGas.value = value
    }

    // gasTotal = (maxFeePerGas + maxPriorityFeePerGas) * GasLimit
    val gasTotal by lazy {
        combine(gasLimit, maxPriorityFeePerGas, maxFeePerGas) { limit, maxFee, maxPriorityFee ->
            ((maxFee.toBigDecimal() + maxPriorityFee.toBigDecimal()).gwei.ether) * limit.toBigDecimal()
        }
    }

    val gasUsdTotal by lazy {
        combine(gasTotal, nativeToken) { total, token ->
            token?.let { total * it.price }
        }.mapNotNull { it }.asStateIn(viewModelScope, BigDecimal.ZERO)
    }

    val gasFeeUnit by lazy {
        nativeToken.mapNotNull {
            it?.symbol
        }.asStateIn(viewModelScope, "")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val arrives = combine(maxFeePerGas, maxPriorityFeePerGas) { maxFee, maxPriorityFee ->
        GasFeeData(maxFeePerGas = maxFee, maxPriorityFeePerGas = maxPriorityFee)
    }.map { gasFee ->
        getArrivesWithGasFee(gasFee = gasFee, suggestGasFee = _suggestGasFee.value).getOrNull()
    }.mapNotNull {
        it?.humanizeMinutes()
    }.asStateIn(viewModelScope, "")
}
