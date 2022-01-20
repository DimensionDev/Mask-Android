package com.dimension.maskbook.wallet.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface ICollectibleRepository {
    fun getCollectiblesByWallet(
        walletData: WalletData,
    ): Flow<PagingData<WalletCollectibleData>>

    fun getCollectibleById(
        collectibleId: String,
    ): Flow<WalletCollectibleData?>
}