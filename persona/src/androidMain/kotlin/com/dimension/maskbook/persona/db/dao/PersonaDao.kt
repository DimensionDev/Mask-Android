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
import androidx.sqlite.db.SupportSQLiteQuery
import com.dimension.maskbook.persona.db.model.DbPersonaRecord

@Dao
interface PersonaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(persona: DbPersonaRecord): Int

    @RawQuery
    suspend fun findRaw(query: SupportSQLiteQuery): DbPersonaRecord?

    @RawQuery
    suspend fun findListRaw(query: SupportSQLiteQuery): List<DbPersonaRecord>

    @Query("SELECT * FROM DbPersonaRecord WHERE identifier=:identifier LIMIT 1")
    suspend fun find(identifier: String): DbPersonaRecord?

    @Query("DELETE FROM DbPersonaRecord WHERE identifier=:identifier")
    suspend fun delete(identifier: String)
}
