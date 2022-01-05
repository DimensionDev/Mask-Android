package com.dimension.maskbook.wallet.db.dao

import androidx.room.*
import com.dimension.maskbook.wallet.db.model.DbWalletContact
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletContactDao {
    @Transaction
    @Query("SELECT * FROM DbWalletContact")
    fun getAll(): Flow<List<DbWalletContact>>
    @Transaction
    @Query("SELECT * FROM DbWalletContact WHERE address = :address")
    suspend fun getByAddress(address: String): DbWalletContact?
    @Transaction
    @Query("SELECT * FROM DbWalletContact WHERE address = :address")
    fun getByAddressFlow(address: String): Flow<DbWalletContact?>
    @Query("DELETE FROM DbWalletContact WHERE id = :id")
    suspend fun deleteById(id: String)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(data: List<DbWalletContact>)
}