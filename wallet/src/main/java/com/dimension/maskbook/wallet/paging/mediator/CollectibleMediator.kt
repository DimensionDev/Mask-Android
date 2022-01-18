package com.dimension.maskbook.wallet.paging.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dimension.maskbook.wallet.db.AppDatabase
import com.dimension.maskbook.wallet.db.model.*
import com.dimension.maskbook.wallet.repository.ChainType
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
