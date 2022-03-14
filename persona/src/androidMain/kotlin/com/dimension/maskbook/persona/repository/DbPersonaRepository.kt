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
package com.dimension.maskbook.persona.repository

import com.dimension.maskbook.persona.db.PersonaDatabase
import com.dimension.maskbook.persona.db.model.DbPersonaRecord
import kotlinx.coroutines.flow.Flow

class DbPersonaRepository(database: PersonaDatabase) {
    private val personaDao = database.personaDao()

    // suspend fun findWithProfiles(personaIdentifier: String): DbPersonaWithProfiles? {
    //     return personaDao.findWithProfiles(personaIdentifier)
    // }

    suspend fun getList(): List<DbPersonaRecord> {
        return personaDao.getList()
    }

    fun getListFlow(): Flow<List<DbPersonaRecord>> {
        return personaDao.getListFlow()
    }

    suspend fun contains(personaIdentifier: String): Boolean {
        return personaDao.count(personaIdentifier) > 0
    }

    suspend fun isEmpty(): Boolean {
        return personaDao.count() == 0
    }
}
