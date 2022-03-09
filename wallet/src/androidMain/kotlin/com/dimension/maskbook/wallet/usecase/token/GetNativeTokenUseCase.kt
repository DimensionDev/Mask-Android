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
package com.dimension.maskbook.wallet.usecase.token

import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.export.model.TokenData
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.usecase.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

interface GetNativeTokenUseCase {
    // if chainType is null use current chain
    operator fun invoke(chainType: ChainType? = null): Flow<Result<TokenData>>
}

class GetNativeTokenUseCaseImpl(
    private val repository: IWalletRepository
) : GetNativeTokenUseCase {
    override fun invoke(chainType: ChainType?): Flow<Result<TokenData>> {
        return flow {
            emit(Result.Loading())
            try {
                val currentChain = repository.currentChain.firstOrNull()
                val token = if (chainType == null || chainType == currentChain?.chainType) {
                    currentChain?.nativeToken
                } else {
                    repository.getChainData(chainType).firstOrNull()?.nativeToken
                }
                token?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failed(Error()))
            } catch (e: Throwable) {
                emit(Result.Failed(Error()))
            }
        }
    }
}
