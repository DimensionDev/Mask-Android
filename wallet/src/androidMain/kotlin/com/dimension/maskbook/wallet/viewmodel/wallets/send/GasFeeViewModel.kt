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
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.ext.gwei
import com.dimension.maskbook.wallet.ext.humanizeMinutes
import com.dimension.maskbook.wallet.repository.ChainType
import com.dimension.maskbook.wallet.repository.GasPriceEditMode
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.services.WalletServices
import com.dimension.maskbook.wallet.services.model.EthGasFee
import com.dimension.maskbook.wallet.services.model.EthGasFeeResponse
import com.dimension.maskbook.wallet.services.model.MaticGasFeeResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import java.math.BigDecimal
import kotlin.time.ExperimentalTime

data class GasFeeData(
    val maxPriorityFeePerGas: Double,
    val maxFeePerGas: Double,
) {
    companion object {
        fun fromEthGasFee(data: EthGasFee?) = GasFeeData(
            maxFeePerGas = data?.suggestedMaxFeePerGas?.toDoubleOrNull() ?: 0.0,
            maxPriorityFeePerGas = data?.suggestedMaxPriorityFeePerGas?.toDoubleOrNull() ?: 0.0
        )
    }
}

class GasFeeModel {
    val low: GasFeeData
    val medium: GasFeeData
    val high: GasFeeData
    val baseFee: Double

    constructor(response: EthGasFeeResponse) {
        low = GasFeeData.fromEthGasFee(response.low)
        medium = GasFeeData.fromEthGasFee(response.medium)
        high = GasFeeData.fromEthGasFee(response.high)
        baseFee = response.estimatedBaseFee?.toDoubleOrNull() ?: 0.0
    }

    constructor(response: MaticGasFeeResponse) {
        low = GasFeeData(
            maxPriorityFeePerGas = response.safeLow ?: 0.0,
            maxFeePerGas = 0.0,
        )
        medium = GasFeeData(
            maxPriorityFeePerGas = response.standard ?: 0.0,
            maxFeePerGas = 0.0,
        )
        high = GasFeeData(
            maxPriorityFeePerGas = response.fast ?: 0.0,
            maxFeePerGas = 0.0,
        )
        baseFee = 0.0
    }

    constructor(baseGasFee: Double) {
        low = GasFeeData(
            maxPriorityFeePerGas = baseGasFee - 0.1,
            maxFeePerGas = 0.0,
        )
        medium = GasFeeData(
            maxPriorityFeePerGas = baseGasFee,
            maxFeePerGas = 0.0,
        )
        high = GasFeeData(
            maxPriorityFeePerGas = baseGasFee + 0.1,
            maxFeePerGas = 0.0,
        )
        baseFee = 0.0
    }
}

class GasFeeViewModel(
    initialGasLimit: Double = 21000.0,
    private val services: WalletServices,
    private val walletRepository: IWalletRepository,
) : ViewModel() {
    private val _gasPriceEditMode = MutableStateFlow(GasPriceEditMode.MEDIUM)
    val gasPriceEditMode = _gasPriceEditMode.asStateIn(viewModelScope, GasPriceEditMode.MEDIUM)
    fun setGasPriceEditMode(value: GasPriceEditMode) {
        _gasPriceEditMode.value = value
    }

    val defaultGasFee by lazy {
        flow {
            try {
                emit(services.gasServices.ethGas())
            } catch (e: Throwable) {
                emit(null)
            }
        }.mapNotNull { it }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val gasFeeModel by lazy {
        walletRepository.dWebData.mapLatest {
            when (it.chainType) {
                ChainType.eth -> GasFeeModel(services.gasServices.ethGasFee())
                ChainType.polygon -> GasFeeModel(services.gasServices.maticGasFee())
                ChainType.bsc -> GasFeeModel(5.0)
                ChainType.arbitrum -> GasFeeModel(3.0)
                ChainType.xdai -> GasFeeModel(3.0)
                else -> GasFeeModel(5.0)
            }
        }
    }

    private val _gasLimit = MutableStateFlow(initialGasLimit)
    val gasLimit = _gasLimit.asStateIn(viewModelScope, -1.0)

    fun setGasLimit(value: Double) {
        _gasLimit.value = value
    }

    private val _maxPriorityFee = MutableStateFlow(-1.0)
    val maxPriorityFee = combine(_maxPriorityFee, gasFeeModel, _gasPriceEditMode) { max, model, mode ->
        if (max != -1.0) {
            max
        } else {
            when (mode) {
                GasPriceEditMode.LOW -> model.low.maxPriorityFeePerGas
                GasPriceEditMode.MEDIUM -> model.medium.maxPriorityFeePerGas
                GasPriceEditMode.HIGH -> model.high.maxPriorityFeePerGas
                GasPriceEditMode.CUSTOM -> max
            }
        }
    }.asStateIn(viewModelScope, -1.0)

    fun setMaxPriorityFee(value: Double) {
        _maxPriorityFee.value = value
    }

    private val _maxFee = MutableStateFlow(-1.0)
    val maxFee = combine(_maxFee, gasFeeModel, _gasPriceEditMode) { max, model, mode ->
        if (max != -1.0) {
            max
        } else {
            when (mode) {
                GasPriceEditMode.LOW -> model.low.maxFeePerGas
                GasPriceEditMode.MEDIUM -> model.medium.maxFeePerGas
                GasPriceEditMode.HIGH -> model.high.maxFeePerGas
                GasPriceEditMode.CUSTOM -> max
            }
        }
    }.asStateIn(viewModelScope, -1.0)

    fun setMaxFee(value: Double) {
        _maxFee.value = value
    }

    val gasPrice by lazy {
        rawGasPrice.map {
            (it / BigDecimal.TEN).gwei.ether
        }
    }

    val rawGasPrice by lazy {
        combine(gasPriceEditMode, defaultGasFee) { mode, default ->
            when (mode) {
                GasPriceEditMode.LOW -> default.safeLow
                GasPriceEditMode.MEDIUM -> default.average
                GasPriceEditMode.HIGH -> default.fast
                GasPriceEditMode.CUSTOM -> null
            }?.toBigDecimal() ?: BigDecimal.ZERO
        }
    }

    val gasTotal by lazy {
        combine(gasPrice, gasLimit, gasFeeModel, maxPriorityFee) { price, limit, gasFeeModel, maxPriorityFee ->
            if (gasFeeModel.baseFee != 0.0) {
                (gasFeeModel.baseFee.toBigDecimal() + maxPriorityFee.toBigDecimal()).gwei.ether
            } else {
                price
            } * limit.toBigDecimal()
        }
    }

    val ethPrice by lazy {
        walletRepository.currentWallet
            .mapNotNull { it }
            .map { it.tokens.firstOrNull { it.tokenData.address == "eth" } }
            .mapNotNull { it?.tokenData?.price }
    }

    @OptIn(ExperimentalTime::class)
    val arrives = combine(rawGasPrice, defaultGasFee.mapNotNull { it }) { gas, response ->
        with(response) {
            if (safeLowWait != null && fastestWait != null && fastWait != null && avgWait != null && fast != null && fastest != null && safeLow != null && average != null) {
                when {
                    gas > fastest.toBigDecimal() -> fastestWait.humanizeMinutes()
                    gas > fast.toBigDecimal() -> fastWait.humanizeMinutes()
                    gas >= average.toBigDecimal() -> avgWait.humanizeMinutes()
                    gas >= safeLow.toBigDecimal() -> safeLowWait.humanizeMinutes()
                    else -> ""
                }
            } else {
                ""
            }
        }
    }
}
