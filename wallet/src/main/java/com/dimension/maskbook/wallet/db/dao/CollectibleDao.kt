package com.dimension.maskbook.wallet.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.dimension.maskbook.wallet.db.model.DbCollectible
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectibleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(collectible: List<DbCollectible>)

    @Delete
    suspend fun delete(collectible: DbCollectible)

    @Query("SELECT * FROM dbcollectible WHERE walletId = :walletId")
    fun getByWallet(walletId: String): PagingSource<Int, DbCollectible>

    @Query("SELECT * FROM dbcollectible WHERE _id = :collectibleId")
    fun getById(collectibleId: String): Flow<DbCollectible?>
}