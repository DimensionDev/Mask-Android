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
import com.dimension.maskbook.persona.db.model.DbPersonaRecord
import com.dimension.maskbook.persona.db.model.PersonaWithLinkedProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(persona: DbPersonaRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<DbPersonaRecord>)

    @Query("DELETE FROM DbPersonaRecord WHERE identifier=:identifier")
    suspend fun delete(identifier: String)

    @Query("SELECT * FROM DbPersonaRecord WHERE identifier=:identifier LIMIT 1")
    suspend fun find(identifier: String): DbPersonaRecord?

    @Query("SELECT * FROM DbPersonaRecord LIMIT 1")
    suspend fun findFirst(): DbPersonaRecord?

    @Query("SELECT * FROM DbPersonaRecord")
    suspend fun findList(): List<DbPersonaRecord>

    @Transaction
    @RawQuery
    suspend fun findRaw(query: SupportSQLiteQuery): PersonaWithLinkedProfile?

    @Transaction
    @RawQuery
    suspend fun findListRaw(query: SupportSQLiteQuery): List<PersonaWithLinkedProfile>

    @Transaction
    @Query("SELECT * FROM DbPersonaRecord")
    suspend fun getListWithProfile(): List<PersonaWithLinkedProfile>

    @Query("SELECT * FROM DbPersonaRecord WHERE identifier=:identifier LIMIT 1")
    fun getFlow(identifier: String): Flow<DbPersonaRecord?>

    @Query("SELECT * FROM DbPersonaRecord WHERE privateKeyRaw IS NOT NULL")
    fun getListFlow(): Flow<List<DbPersonaRecord>>

    @Query("UPDATE DbPersonaRecord SET nickname=:nickname WHERE identifier=:identifier")
    suspend fun updateNickName(identifier: String, nickname: String)

    @Query("UPDATE DbPersonaRecord SET email=:email WHERE identifier=:identifier")
    suspend fun updateEmail(identifier: String, email: String)

    @Query("UPDATE DbPersonaRecord SET phone=:phone WHERE identifier=:identifier")
    suspend fun updatePhone(identifier: String, phone: String)

    @Query("UPDATE DbPersonaRecord SET avatar=:avatar WHERE identifier=:identifier")
    suspend fun updateAvatar(identifier: String, avatar: String?)

    @Query("SELECT COUNT(1) FROM DbPersonaRecord WHERE identifier=:identifier LIMIT 1")
    suspend fun count(identifier: String): Int

    @Query("SELECT COUNT(1) FROM DbPersonaRecord LIMIT 1")
    suspend fun count(): Int
}
