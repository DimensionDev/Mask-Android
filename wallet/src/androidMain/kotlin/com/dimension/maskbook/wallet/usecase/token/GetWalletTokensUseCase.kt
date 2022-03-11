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
import com.dimension.maskbook.wallet.export.model.WalletTokenData
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.usecase.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine

interface GetWalletTokensUseCase {
    operator fun invoke(
        filterWithChainType: Boolean = true,
        chainType: ChainType? = null, // if chain type is null use current chain
    ): Flow<Result<List<WalletTokenData>>>
}

class GetWalletTokensUseCaseImpl(
    val repository: IWalletRepository,
) : GetWalletTokensUseCase {

    override fun invoke(
        filterWithChainType: Boolean,
        chainType: ChainType?
    ): Flow<Result<List<WalletTokenData>>> {
        return combine(repository.currentWallet, repository.currentChain) { wallet, currentChain ->
            val filterChainType = if (filterWithChainType) chainType
                ?: currentChain?.chainType else null
            wallet?.tokens?.filter {
                if (filterChainType == null) true else it.tokenData.chainType == filterChainType
            }?.let {
                Result.Success(it)
            } ?: Result.Failed(Error("Current wallet is null"))
        }.catch {
            emit(Result.Failed(it))
        }
    }
}
