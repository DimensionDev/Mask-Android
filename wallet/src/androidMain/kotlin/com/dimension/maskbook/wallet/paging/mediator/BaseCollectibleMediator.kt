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
import com.dimension.maskbook.wallet.db.model.DbCollectibleContract
import com.dimension.maskbook.wallet.db.model.DbCollectibleCreator
import com.dimension.maskbook.wallet.db.model.DbCollectibleUrl
import com.dimension.maskbook.wallet.db.model.DbCollection
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.services.OpenSeaServices
import com.dimension.maskbook.wallet.services.model.AssetElement

@OptIn(ExperimentalPagingApi::class)
open class BaseCollectibleMediator<Value : Any>(
    val database: AppDatabase,
    val walletId: String,
    val walletAddress: String,
    val openSeaServices: OpenSeaServices,
    val collectionSlug: String? = null
) : RemoteMediator<Int, Value>() {
    private var cursor: String? = null
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Value>): MediatorResult {
        try {
            cursor = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> cursor
            }

            val result = openSeaServices.assets(walletAddress, cursor = cursor, collection_slug = collectionSlug).also {
                cursor = it.next
            }.assets

            result?.let { assets ->
                database.withTransaction {
                    database.collectibleDao().insertAll(
                        assets.map {
                            mapToDbCollectible(it)
                        }
                    )
                }
            }
            return MediatorResult.Success(endOfPaginationReached = cursor == null)
        } catch (e: Throwable) {
            return MediatorResult.Error(e)
        }
    }

    private fun mapToDbCollectible(element: AssetElement): DbCollectible {
        return DbCollectible(
            _id = "${element.assetContract?.address}@${element.tokenID}",
            name = element.name.orEmpty(),
            description = element.description,
            walletId = walletId,
            chainType = ChainType.eth, // TODO: 2022/1/17
            id = element.id ?: 0L,
            tokenId = element.tokenID.orEmpty(),
            externalLink = element.externalLink,
            permalink = element.permalink,
            creator = with(element.creator) {
                DbCollectibleCreator(
                    userName = this?.user?.username.orEmpty(),
                    profileImgURL = this?.profileImgURL.orEmpty(),
                    address = this?.address.orEmpty(),
                    config = this?.config.orEmpty(),
                )
            },
            collection = with(element.collection) {
                DbCollection(
                    imageURL = this?.imageURL.orEmpty(),
                    name = this?.name.orEmpty(),
                    slug = this?.slug.orEmpty()
                )
            },
            contract = with(element.assetContract) {
                DbCollectibleContract(
                    address = this?.address.orEmpty(),
                    imageUrl = this?.imageURL.orEmpty(),
                    name = this?.name.orEmpty(),
                    symbol = this?.symbol.orEmpty(),
                )
            },
            url = DbCollectibleUrl(
                imageURL = element.imageURL.orEmpty(),
                imagePreviewURL = element.imagePreviewURL.orEmpty(),
                imageThumbnailURL = element.imageThumbnailURL.orEmpty(),
                imageOriginalURL = element.imageOriginalURL.orEmpty(),
                animationURL = element.animationURL.orEmpty(),
                animationOriginalURL = element.animationOriginalURL.orEmpty(),
            )
        )
    }
}
