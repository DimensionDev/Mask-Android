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

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dimension.maskbook.wallet.db.model.DbCollectible
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectibleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(collectible: List<DbCollectible>)

    @Delete
    suspend fun delete(collectible: DbCollectible)

    @Query("SELECT * FROM dbcollectible WHERE walletId = :walletId")
    fun getByWallet(walletId: String): PagingSource<Int, DbCollectible>

    @Query("SELECT * FROM dbcollectible WHERE _id = :collectibleId")
    fun getById(collectibleId: String): Flow<DbCollectible?>
}
