/*
 *  Mask-Android
 *
 *  Copyright (C) 2022  DimensionDev and Contributors
 *
 *  This file is part of Mask-Android.
 *
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Mask-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.wallet.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dimension.maskbook.wallet.db.model.DbChainData
import com.dimension.maskbook.wallet.db.model.DbChainDataWithTokenData
import kotlinx.coroutines.flow.Flow

@Dao
interface ChainDao {

    @Transaction
    @Query("SELECT * FROM DbChainData")
    suspend fun getAll(): List<DbChainDataWithTokenData>

    @Transaction
    @Query("SELECT * FROM DbChainData WHERE chainId = :id")
    fun getByIdFlow(id: Long): Flow<DbChainDataWithTokenData?>

    @Query("DELETE FROM DbChainData WHERE chainId = :id")
    suspend fun deleteById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(data: List<DbChainData>)
}
