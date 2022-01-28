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

    @Query("SELECT count(1) FROM DbSendHistory WHERE address=:address LIMIT 0, 1")
    suspend fun contains(address: String): Int

    @Query("UPDATE DbSendHistory SET name=:name, lastSend=:lastSend WHERE address=:address")
    suspend fun updateName(address: String, name: String, lastSend: Long): Int

    @Query("UPDATE DbSendHistory SET lastSend=:lastSend WHERE address=:address")
    suspend fun updateLastTime(address: String, lastSend: Long): Int
}