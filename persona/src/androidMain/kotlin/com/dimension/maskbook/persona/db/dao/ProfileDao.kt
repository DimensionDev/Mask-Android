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
package com.dimension.maskbook.persona.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SupportSQLiteQuery
import com.dimension.maskbook.persona.db.model.DbProfileRecord
import com.dimension.maskbook.persona.db.model.ProfileWithLinkedProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: DbProfileRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: List<DbProfileRecord>)

    @Transaction
    @RawQuery
    suspend fun findRaw(query: SupportSQLiteQuery): ProfileWithLinkedProfile?

    @Transaction
    @RawQuery
    suspend fun findListRaw(query: SupportSQLiteQuery): List<ProfileWithLinkedProfile>

    @Query("SELECT * FROM DbProfileRecord WHERE identifier=:identifier LIMIT 1")
    suspend fun find(identifier: String): DbProfileRecord?

    @Query("UPDATE DbProfileRecord SET avatar=:avatar WHERE identifier=:identifier")
    suspend fun updateAvatar(identifier: String, avatar: String)

    @Transaction
    @Query("SELECT * FROM DbProfileRecord WHERE identifier IN (SELECT profileIdentifier FROM ViewLinkedProfileWithKey WHERE personaIdentifier=:personaIdentifier AND privateKeyRaw IS NOT NULL)")
    fun getListWithPersonaFlow(personaIdentifier: String): Flow<List<ProfileWithLinkedProfile>>

    @Query("DELETE FROM DbProfileRecord WHERE identifier=:identifier")
    suspend fun delete(identifier: String)
}
