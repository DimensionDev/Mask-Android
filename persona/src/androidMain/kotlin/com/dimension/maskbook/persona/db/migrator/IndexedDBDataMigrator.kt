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
package com.dimension.maskbook.persona.db.migrator

import com.dimension.maskbook.persona.db.PersonaDatabase
import com.dimension.maskbook.persona.db.migrator.mapper.toDbPersonaRecord
import com.dimension.maskbook.persona.db.migrator.mapper.toDbProfileRecord
import com.dimension.maskbook.persona.db.migrator.mapper.toDbRelationRecord
import com.dimension.maskbook.persona.db.migrator.mapper.toLinkedProfiles
import com.dimension.maskbook.persona.model.indexed.IndexedDBAllRecord

object IndexedDBDataMigrator {

    suspend fun migrate(database: PersonaDatabase, records: IndexedDBAllRecord) {
        val personas = records.personas
        for (persona in personas) {
            database.personaDao().insert(persona.toDbPersonaRecord())
            database.linkedProfileDao().insert(persona.toLinkedProfiles())
        }

        val profiles = records.profiles
        database.profileDao().insert(
            profiles.map { profile ->
                profile.toDbProfileRecord()
            }
        )

        val relations = records.relations
        database.relationDao().insert(
            relations.map { relation ->
                relation.toDbRelationRecord()
            }
        )
    }
}
