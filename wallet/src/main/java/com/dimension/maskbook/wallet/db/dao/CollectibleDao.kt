package com.dimension.maskbook.wallet.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.dimension.maskbook.wallet.db.model.DbCollectible

@Dao
interface CollectibleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(collectible: List<DbCollectible>)

    @Delete
    suspend fun delete(collectible: DbCollectible)

    @Query("SELECT * FROM dbcollectible WHERE walletId = :walletId")
    fun getByWallet(walletId: String): PagingSource<Int, DbCollectible>
}