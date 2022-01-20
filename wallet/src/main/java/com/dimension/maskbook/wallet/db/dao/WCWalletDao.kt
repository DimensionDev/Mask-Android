package com.dimension.maskbook.wallet.db.dao

import androidx.room.*
import com.dimension.maskbook.wallet.db.model.DbSendHistory
import com.dimension.maskbook.wallet.db.model.DbSendHistoryWithContact
import com.dimension.maskbook.wallet.db.model.DbWCWallet
import kotlinx.coroutines.flow.Flow

@Dao
interface WCWalletDao {
    @Query("SELECT * FROM DbWCWallet")
    fun getAll(): Flow<List<DbWCWallet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(data: List<DbWCWallet>)
}