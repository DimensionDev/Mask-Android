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
import com.dimension.maskbook.persona.db.model.DbLinkedProfileRecord
import com.dimension.maskbook.persona.model.LinkedProfileDetailsState

@Dao
interface LinkedProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(relation: DbLinkedProfileRecord)

    @Query("SELECT * FROM DbLinkedProfileRecord WHERE personaIdentifier=:personaIdentifier AND profileIdentifier=:profileIdentifier LIMIT 1")
    suspend fun find(
        personaIdentifier: String,
        profileIdentifier: String,
    ): DbLinkedProfileRecord?

    @Query("SELECT * FROM DbLinkedProfileRecord WHERE profileIdentifier=:profileIdentifier LIMIT 1")
    suspend fun find(
        profileIdentifier: String,
    ): DbLinkedProfileRecord?

    @Query("UPDATE DbLinkedProfileRecord SET state=:state WHERE personaIdentifier=:personaIdentifier AND profileIdentifier=:profileIdentifier")
    suspend fun updateFavor(
        personaIdentifier: String,
        profileIdentifier: String,
        state: LinkedProfileDetailsState,
    )

    @Query("DELETE FROM DbLinkedProfileRecord WHERE personaIdentifier=:personaIdentifier AND profileIdentifier=:profileIdentifier")
    suspend fun delete(
        personaIdentifier: String,
        profileIdentifier: String,
    )

    @Query("DELETE FROM DbLinkedProfileRecord WHERE profileIdentifier=:profileIdentifier")
    suspend fun delete(
        profileIdentifier: String,
    )
}
