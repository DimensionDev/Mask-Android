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
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dimension.maskbook.wallet.db.AppDatabase
import com.dimension.maskbook.wallet.db.model.DbCollectible
import com.dimension.maskbook.wallet.db.model.DbCollectibleCollection
import com.dimension.maskbook.wallet.db.model.DbCollectibleContract
import com.dimension.maskbook.wallet.db.model.DbCollectibleCreator
import com.dimension.maskbook.wallet.db.model.DbCollectibleUrl
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.services.OpenSeaServices
import com.dimension.maskbook.wallet.services.model.AssetElement

@OptIn(ExperimentalPagingApi::class)
class CollectibleMediator(
    val database: AppDatabase,
    val walletId: String,
    val walletAddress: String,
    val openSeaServices: OpenSeaServices,
) : RemoteMediator<Int, DbCollectible>() {
    private var page = 0
    override suspend fun load(loadType: LoadType, state: PagingState<Int, DbCollectible>): MediatorResult {
        try {
            page = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> page + 1
            }

            val result = openSeaServices.assets(walletAddress, offset = page).assets?.map {
                mapToDbCollectible(it)
            } ?: emptyList()

            database.withTransaction {
                database.collectibleDao().insertAll(result)
            }
            return MediatorResult.Success(endOfPaginationReached = result.isEmpty())
        } catch (e: Throwable) {
            return MediatorResult.Error(e)
        }
    }

    private fun mapToDbCollectible(element: AssetElement): DbCollectible {
        return DbCollectible(
            _id = "${element.assetContract?.address}@${element.tokenID}",
            name = element.name ?: "",
            description = element.description,
            walletId = walletId,
            chainType = ChainType.eth, // TODO: 2022/1/17
            id = element.id ?: 0L,
            tokenId = element.tokenID ?: "",
            externalLink = element.externalLink,
            permalink = element.permalink,
            creator = with(element.creator) {
                DbCollectibleCreator(
                    userName = this?.user?.username ?: "",
                    profileImgURL = this?.profileImgURL ?: "",
                    address = this?.address ?: "",
                    config = this?.config ?: "",
                )
            },
            collection = with(element.collection) {
                DbCollectibleCollection(
                    imageURL = this?.imageURL ?: "",
                    name = this?.name ?: "",
                )
            },
            contract = with(element.assetContract) {
                DbCollectibleContract(
                    address = this?.address ?: "",
                    imageUrl = this?.imageURL ?: "",
                    name = this?.name ?: "",
                    symbol = this?.symbol ?: "",
                )
            },
            url = DbCollectibleUrl(
                imageURL = element.imageURL,
                imagePreviewURL = element.imagePreviewURL,
                imageThumbnailURL = element.imageThumbnailURL,
                imageOriginalURL = element.imageOriginalURL,
                animationURL = element.animationURL,
                animationOriginalURL = element.animationOriginalURL,
            )
        )
    }
}
