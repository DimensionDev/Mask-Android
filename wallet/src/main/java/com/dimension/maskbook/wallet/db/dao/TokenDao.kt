package com.dimension.maskbook.wallet.db.dao

import androidx.room.*
import com.dimension.maskbook.wallet.db.model.DbToken
import kotlinx.coroutines.flow.Flow

@Dao
interface TokenDao {
    @Transaction
    @Query("SELECT * FROM DBTOKEN")
    fun getAll(): Flow<List<DbToken>>
    @Transaction
    @Query("SELECT * FROM DBTOKEN WHERE id = :id")
    suspend fun getById(id: String): DbToken
    @Transaction
    @Query("SELECT * FROM DBTOKEN WHERE id = :id")
    fun getByIdFlow(id: String): Flow<DbToken>
    @Query("DELETE FROM DBTOKEN WHERE id = :id")
    suspend fun deleteById(id: String)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(data: List<DbToken>)
}