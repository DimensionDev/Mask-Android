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
package com.dimension.maskbook.wallet.usecase.gas

import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.services.WalletServices
import com.dimension.maskbook.wallet.services.model.EthGasFee
import com.dimension.maskbook.wallet.services.model.EthGasFeeResponse
import com.dimension.maskbook.wallet.services.model.MaticGasFeeResponse
import com.dimension.maskbook.wallet.usecase.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class GasFeeData(
    val maxPriorityFeePerGas: Double,
    val maxFeePerGas: Double,
) {
    val total get() = maxPriorityFeePerGas + maxFeePerGas
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
            maxPriorityFeePerGas = 0.0,
            maxFeePerGas = response.safeLow ?: 0.0,
        )
        medium = GasFeeData(
            maxPriorityFeePerGas = 0.0,
            maxFeePerGas = response.standard ?: 0.0,
        )
        high = GasFeeData(
            maxPriorityFeePerGas = 0.0,
            maxFeePerGas = response.fast ?: 0.0,
        )
        baseFee = response.safeLow ?: 0.0
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

    constructor() {
        low = GasFeeData(
            maxPriorityFeePerGas = 0.0,
            maxFeePerGas = 0.0,
        )
        medium = GasFeeData(
            maxPriorityFeePerGas = 0.0,
            maxFeePerGas = 0.0,
        )
        high = GasFeeData(
            maxPriorityFeePerGas = 0.0,
            maxFeePerGas = 0.0,
        )
        baseFee = 0.0
    }
}

interface GetSuggestGasFeeUseCase {
    operator fun invoke(chainType: ChainType?): Flow<Result<GasFeeModel>>
}

internal class GetSuggestGasFeeUseCaseImpl(
    private val services: WalletServices,
) : GetSuggestGasFeeUseCase {
    override fun invoke(chainType: ChainType?): Flow<Result<GasFeeModel>> {
        return flow {
            emit(Result.Loading())
            runCatching {
                when (chainType) {
                    ChainType.eth -> GasFeeModel(
                        services.gasServices.ethGasFee()
                    )
                    ChainType.polygon -> GasFeeModel(
                        services.gasServices.maticGasFee()
                    )
                    ChainType.bsc -> GasFeeModel(5.0)
                    ChainType.arbitrum -> GasFeeModel(
                        3.0
                    )
                    ChainType.xdai -> GasFeeModel(3.0)
                    else -> GasFeeModel(5.0)
                }
            }.onSuccess {
                emit(Result.Success(it))
            }.onFailure {
                emit(Result.Failed(it))
            }
        }
    }
}
