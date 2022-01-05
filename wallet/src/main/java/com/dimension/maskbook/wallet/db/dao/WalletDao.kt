package com.dimension.maskbook.wallet.db.dao

import androidx.room.*
import com.dimension.maskbook.wallet.db.model.DbWallet
import com.dimension.maskbook.wallet.db.model.DbWalletTokenTokenWithWallet
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Transaction
    @Query("SELECT * FROM DbWallet")
    fun getAll(): Flow<List<DbWalletTokenTokenWithWallet>>
    @Transaction
    @Query("SELECT * FROM DbWallet WHERE id = :id")
    suspend fun getById(id: String): DbWalletTokenTokenWithWallet?
    @Transaction
    @Query("SELECT * FROM DbWallet WHERE LOWER(address) = LOWER(:address)")
    suspend fun getByAddress(address: String): DbWalletTokenTokenWithWallet?
    @Transaction
    @Query("SELECT * FROM DbWallet WHERE id = :id")
    fun getByIdFlow(id: String): Flow<DbWalletTokenTokenWithWallet?>
    @Query("DELETE FROM DBWALLET WHERE id = :id")
    suspend fun deleteById(id: String)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(data: List<DbWallet>)
}
