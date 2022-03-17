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

import androidx.paging.PagingData
import com.dimension.maskbook.wallet.export.model.WalletCollectibleCollectionData
import com.dimension.maskbook.wallet.repository.ICollectibleRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.usecase.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

interface GetWalletCollectibleCollectionsUseCase {
    operator fun invoke(): Flow<Result<PagingData<WalletCollectibleCollectionData>>>
}

@Factory(binds = [GetWalletCollectibleCollectionsUseCase::class])
class GetWalletCollectibleCollectionsUseCaseImpl(
    val repository: ICollectibleRepository,
    val walletRepository: IWalletRepository,
) : GetWalletCollectibleCollectionsUseCase {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(): Flow<Result<PagingData<WalletCollectibleCollectionData>>> {
        return walletRepository.currentWallet.filterNotNull().flatMapLatest {
            repository.getCollectibleCollectionsByWallet(it)
        }.map<PagingData<WalletCollectibleCollectionData>, Result<PagingData<WalletCollectibleCollectionData>>> {
            Result.Success(it)
        }.catch {
            emit(Result.Failed(it))
        }
    }
}
