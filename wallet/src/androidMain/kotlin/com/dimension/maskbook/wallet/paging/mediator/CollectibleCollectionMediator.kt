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
package com.dimension.maskbook.wallet.paging.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.dimension.maskbook.wallet.db.AppDatabase
import com.dimension.maskbook.wallet.db.model.DbCollectible
import com.dimension.maskbook.wallet.export.model.WalletCollectibleCollectionData
import com.dimension.maskbook.wallet.repository.fromDb
import com.dimension.maskbook.wallet.services.OpenSeaServices
import kotlinx.coroutines.flow.mapNotNull

@OptIn(ExperimentalPagingApi::class)
class CollectibleCollectionMediator(
    database: AppDatabase,
    walletId: String,
    walletAddress: String,
    openSeaServices: OpenSeaServices,
) : BaseCollectibleMediator<DbCollectible>(
    database = database,
    walletId = walletId,
    walletAddress = walletAddress,
    openSeaServices = openSeaServices
) {
    companion object {
        fun pager(
            walletId: String,
            walletAddress: String,
            services: OpenSeaServices,
            database: AppDatabase
        ) = Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = CollectibleCollectionMediator(
                walletId = walletId,
                walletAddress = walletAddress,
                openSeaServices = services,
                database = database
            ),
            pagingSourceFactory = {
                database.collectibleDao().getCollectionsByWallet(walletId)
            }
        ).flow.mapNotNull {
            it.map {
                WalletCollectibleCollectionData.fromDb(data = it)
            }
        }
    }
}
