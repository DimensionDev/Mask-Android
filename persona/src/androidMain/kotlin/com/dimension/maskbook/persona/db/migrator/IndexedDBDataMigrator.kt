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
import com.dimension.maskbook.persona.db.migrator.model.IndexedDBAllRecord
import com.dimension.maskbook.persona.db.model.DbLinkedProfileRecord
import com.dimension.maskbook.persona.db.model.DbPersonaRecord
import com.dimension.maskbook.persona.db.model.DbProfileRecord
import com.dimension.maskbook.persona.db.model.DbRelationRecord
import com.dimension.maskbook.persona.export.model.Network

class IndexedDBDataMigrator(private val database: PersonaDatabase) {
    suspend fun migrate(records: IndexedDBAllRecord) {
        val personas = records.personas
        for (persona in personas) {
            database.personaDao().insert(
                DbPersonaRecord(
                    identifier = persona.identifier,
                    mnemonic = persona.mnemonic?.words,
                    path = persona.mnemonic?.parameter?.path,
                    withPassword = persona.mnemonic?.parameter?.withPassword ?: false,
                    publicKey = persona.publicKey,
                    privateKey = persona.privateKey,
                    localKey = persona.localKey,
                    nickname = persona.nickname,
                    hasLogout = persona.hasLogout,
                    initialized = !persona.uninitialized,
                    updateAt = persona.updatedAt,
                    createAt = persona.createdAt,
                    email = "",
                    phone = "",
                )
            )
            database.linkedProfileDao().insert(
                persona.linkedProfiles.map { entry ->
                    DbLinkedProfileRecord(
                        personaIdentifier = persona.identifier,
                        profileIdentifier = entry.key,
                        state = entry.value.connectionConfirmState,
                    )
                }
            )
        }

        val profiles = records.profiles
        database.profileDao().insert(
            profiles.map { profile ->
                DbProfileRecord(
                    identifier = profile.identifier,
                    nickname = profile.nickname,
                    network = Network.withProfileIdentifier(profile.identifier),
                    updatedAt = profile.updatedAt,
                    createdAt = profile.createdAt,
                )
            }
        )

        val relations = records.relations
        database.relationDao().insert(
            relations.map { relation ->
                DbRelationRecord(
                    personaIdentifier = relation.personaIdentifier,
                    profileIdentifier = relation.profileIdentifier,
                    favor = relation.favor == 1,
                    updatedAt = System.currentTimeMillis(),
                    createdAt = System.currentTimeMillis(),
                )
            }
        )
    }
}
