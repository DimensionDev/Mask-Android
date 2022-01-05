package com.dimension.maskbook.wallet.db.dao

import androidx.room.*
import com.dimension.maskbook.wallet.db.model.DbStoreKeyAndWallet
import com.dimension.maskbook.wallet.db.model.DbStoredKey
import kotlinx.coroutines.flow.Flow

@Dao
interface StoredKeyDao {
    @Transaction
    @Query("SELECT * FROM DbStoredKey")
    fun getAll(): Flow<List<DbStoreKeyAndWallet>>
    @Transaction
    @Query("SELECT * FROM DbStoredKey WHERE id = :id")
    suspend fun getById(id: String): DbStoreKeyAndWallet?
    @Transaction
    @Query("SELECT * FROM DbStoredKey WHERE id = :id")
    fun getByIdFlow(id: String): Flow<DbStoreKeyAndWallet?>
    @Query("DELETE FROM DbStoredKey WHERE id = :id")
    suspend fun deleteById(id: String)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(data: List<DbStoredKey>)
}