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
import com.dimension.maskbook.persona.db.model.DbRelationRecord
import com.dimension.maskbook.persona.db.model.RelationWithProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface RelationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(relation: DbRelationRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(relations: List<DbRelationRecord>)

    @RawQuery
    suspend fun findListRaw(query: SupportSQLiteQuery): List<DbRelationRecord>

    // @Query("SELECT * FROM DbRelationRecord WHERE profileIdentifier=:profileIdentifier LIMIT 1")
    // suspend fun find(profileIdentifier: String): DbRelationRecord?

    @Query("SELECT * FROM DbRelationRecord WHERE personaIdentifier=:personaIdentifier AND profileIdentifier=:profileIdentifier LIMIT 1")
    suspend fun find(
        personaIdentifier: String,
        profileIdentifier: String,
    ): DbRelationRecord?

    @Transaction
    @Query("SELECT * FROM DbRelationRecord WHERE personaIdentifier=:personaIdentifier")
    fun getListFlow(personaIdentifier: String): Flow<List<RelationWithProfile>>

    @Query("UPDATE DbRelationRecord SET favor=:favor WHERE personaIdentifier=:personaIdentifier AND profileIdentifier=:profileIdentifier")
    suspend fun updateFavor(
        personaIdentifier: String,
        profileIdentifier: String,
        favor: Boolean
    )

    @Query("DELETE FROM DbRelationRecord WHERE personaIdentifier=:personaIdentifier AND profileIdentifier=:profileIdentifier")
    suspend fun delete(
        personaIdentifier: String,
        profileIdentifier: String,
    )
}
