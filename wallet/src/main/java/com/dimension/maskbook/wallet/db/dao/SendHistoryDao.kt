/*
 *  Mask-Android
 *
 *  Copyright (C) DimensionDev and Contributors
 * 
 *  This file is part of Mask-Android.
 * 
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Mask-Android. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.wallet.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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
