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

import com.dimension.maskbook.wallet.export.model.WalletTokenData
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.usecase.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

interface GetWalletTokenByAddressUseCase {
    operator fun invoke(tokenAddress: String): Flow<Result<WalletTokenData>>
}

@Factory(binds = [GetWalletTokenByAddressUseCase::class])
class GetWalletTokenByAddressUseCaseImpl(
    val repository: IWalletRepository,
) : GetWalletTokenByAddressUseCase {
    override fun invoke(tokenAddress: String): Flow<Result<WalletTokenData>> {
        return repository.currentWallet.map {
            it?.tokens?.firstOrNull { token ->
                token.tokenAddress == tokenAddress
            }?.let { token ->
                Result.Success(token)
            } ?: Result.Failed(Error("Can't find any token with given address:$tokenAddress"))
        }.catch {
            emit(Result.Failed(it))
        }
    }
}
