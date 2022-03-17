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
package com.dimension.maskbook.wallet.usecase.collectible

import com.dimension.maskbook.wallet.export.model.WalletCollectibleData
import com.dimension.maskbook.wallet.repository.ICollectibleRepository
import com.dimension.maskbook.wallet.usecase.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

interface GetWalletCollectibleUseCase {
    operator fun invoke(id: String): Flow<Result<WalletCollectibleData>>
}

@Factory(binds = [GetWalletCollectibleUseCase::class])
class GetWalletCollectibleUseCaseImpl(
    val repository: ICollectibleRepository,
) : GetWalletCollectibleUseCase {
    override fun invoke(id: String): Flow<Result<WalletCollectibleData>> {
        return repository.getCollectibleById(id).map {
            it?.let { token ->
                Result.Success(token)
            } ?: Result.Failed(Error("Can't find any collectible with given id:$id"))
        }.catch {
            emit(Result.Failed(it))
        }
    }
}
