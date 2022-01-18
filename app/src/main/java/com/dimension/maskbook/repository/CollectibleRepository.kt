package com.dimension.maskbook.repository

import androidx.paging.*
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
): ICollectibleRepository {
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