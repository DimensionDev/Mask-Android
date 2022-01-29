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
package com.dimension.maskbook.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.dimension.maskbook.wallet.db.AppDatabase
import com.dimension.maskbook.wallet.paging.mediator.CollectibleMediator
import com.dimension.maskbook.wallet.repository.ICollectibleRepository
import com.dimension.maskbook.wallet.repository.WalletCollectibleData
import com.dimension.maskbook.wallet.repository.WalletData
import com.dimension.maskbook.wallet.services.WalletServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CollectibleRepository(
    private val database: AppDatabase,
    private val services: WalletServices,
) : ICollectibleRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getCollectiblesByWallet(walletData: WalletData): Flow<PagingData<WalletCollectibleData>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = CollectibleMediator(
                walletId = walletData.id,
                database = database,
                openSeaServices = services.openSeaServices,
                walletAddress = walletData.address
            ),
        ) {
            database.collectibleDao().getByWallet(walletData.id)
        }.flow.map {
            it.map {
                WalletCollectibleData.fromDb(it)
            }
        }
    }

    override fun getCollectibleById(collectibleId: String): Flow<WalletCollectibleData?> {
        return database.collectibleDao().getById(collectibleId).map {
            it?.let { it1 -> WalletCollectibleData.fromDb(it1) }
        }
    }
}
