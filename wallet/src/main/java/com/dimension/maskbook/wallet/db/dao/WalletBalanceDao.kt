package com.dimension.maskbook.wallet.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.dimension.maskbook.wallet.db.model.DbWalletBalance

@Dao
interface WalletBalanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(data: List<DbWalletBalance>)
}