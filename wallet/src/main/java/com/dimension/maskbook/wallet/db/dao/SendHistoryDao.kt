package com.dimension.maskbook.wallet.db.dao

import androidx.room.*
import com.dimension.maskbook.wallet.db.model.DbSendHistory
import com.dimension.maskbook.wallet.db.model.DbSendHistoryWithContact
import kotlinx.coroutines.flow.Flow

@Dao
interface SendHistoryDao {
    @Transaction
    @Query("SELECT * FROM DbSendHistory")
    fun getAll(): Flow<List<DbSendHistoryWithContact>>
    @Transaction
    @Query("SELECT * FROM DbSendHistory WHERE address = :address")
    suspend fun getByAddress(address: String): DbSendHistoryWithContact?
    @Transaction
    @Query("SELECT * FROM DbSendHistory WHERE address = :address")
    fun getByAddressFlow(address: String): Flow<DbSendHistoryWithContact?>
    @Query("DELETE FROM DbSendHistory WHERE id = :id")
    suspend fun deleteById(id: String)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(data: List<DbSendHistory>)
}