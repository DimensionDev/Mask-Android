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
package com.dimension.maskbook.wallet.usecase.address

import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.usecase.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

interface GetEnsAddressUseCase {
    operator fun invoke(chainType: ChainType, ensName: String): Flow<Result<String>>
}

@Factory(binds = [GetEnsAddressUseCase::class])
class GetEnsAddressUseCaseImpl(
    val repository: IWalletRepository
) : GetEnsAddressUseCase {
    override fun invoke(chainType: ChainType, ensName: String): Flow<Result<String>> {
        return flow {
            emit(Result.Loading())
            runCatching {
                repository.getEnsAddress(
                    chainType = chainType,
                    name = ensName
                )
            }.onSuccess {
                emit(Result.Success(it))
            }.onFailure {
                emit(Result.Failed(it))
            }
        }
    }
}
