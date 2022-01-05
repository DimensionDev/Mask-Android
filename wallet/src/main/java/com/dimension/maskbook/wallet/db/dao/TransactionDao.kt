package com.dimension.maskbook.wallet.db.dao

import androidx.room.*
import com.dimension.maskbook.wallet.db.model.DbTransactionData
import com.dimension.maskbook.wallet.db.model.DbWalletTokenTokenWithWallet
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Transaction
    @Query("SELECT * FROM DbTransactionData")
    fun getAll(): Flow<List<DbTransactionData>>
    @Transaction
    @Query("SELECT * FROM DbTransactionData WHERE id = :id")
    suspend fun getById(id: String): DbTransactionData
    @Query("DELETE FROM DbTransactionData WHERE id = :id")
    suspend fun deleteById(id: String)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(data: List<DbTransactionData>)
}
