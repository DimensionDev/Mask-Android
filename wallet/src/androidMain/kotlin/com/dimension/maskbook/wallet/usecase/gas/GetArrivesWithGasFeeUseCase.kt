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

import com.dimension.maskbook.wallet.services.WalletServices
import com.dimension.maskbook.wallet.usecase.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

interface GetArrivesWithGasFeeUseCase {
    operator fun invoke(
        gasFee: GasFeeData,
        suggestGasFee: GasFeeModel
    ): Flow<Result<Double>> // minutes
}

@Factory(binds = [GetArrivesWithGasFeeUseCase::class])
class GetArrivesWithGasFeeUseCaseImpl(
    private val services: WalletServices,
) : GetArrivesWithGasFeeUseCase {
    private val unKnow = -1.0
    override fun invoke(gasFee: GasFeeData, suggestGasFee: GasFeeModel): Flow<Result<Double>> {
        return flow {
            emit(Result.Loading())
            val total = gasFee.total
            runCatching {
                with(services.gasServices.ethGas()) {
                    if (safeLowWait != null && fastestWait != null && fastWait != null && avgWait != null) {
                        when {
                            total > suggestGasFee.high.total -> fastestWait
                            total >= suggestGasFee.high.total -> fastWait
                            total >= suggestGasFee.medium.total -> avgWait
                            total >= suggestGasFee.low.total -> safeLowWait
                            else -> unKnow
                        }
                    } else {
                        unKnow
                    }
                }
            }.onSuccess {
                if (it == unKnow) {
                    emit(Result.Failed(Error("Can't get arrives  with give gas fee:$total")))
                } else {
                    emit(Result.Success(it))
                }
            }.onFailure {
                emit(Result.Failed(it))
            }
        }
    }
}
