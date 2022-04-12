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
package com.dimension.maskbook.persona.datasource

import com.dimension.maskbook.persona.db.PersonaDatabase
import com.dimension.maskbook.persona.db.migrator.mapper.toDbRelationRecord
import com.dimension.maskbook.persona.db.migrator.mapper.toIndexedDBRelation
import com.dimension.maskbook.persona.db.model.RelationWithLinkedProfile
import com.dimension.maskbook.persona.export.model.IndexedDBRelation
import com.dimension.maskbook.persona.export.model.Network
import com.dimension.maskbook.persona.model.ContactData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DbRelationDataSource(database: PersonaDatabase) {

    private val relationDao = database.relationDao()

    fun getContactListFlow(
        personaIdentifier: String,
    ): Flow<List<ContactData>> {
        return relationDao.getListFlow(personaIdentifier).map { list ->
            list.map { profile ->
                profile.toContactData()
            }
        }
    }

    suspend fun getAll(): List<IndexedDBRelation> {
        return relationDao.getAll().map {
            it.toIndexedDBRelation()
        }
    }

    suspend fun addAll(relation: List<IndexedDBRelation>) {
        relationDao.insert(
            relation.map {
                it.toDbRelationRecord()
            }
        )
    }
}

fun RelationWithLinkedProfile.toContactData(): ContactData {
    return ContactData(
        id = relation.profileIdentifier,
        name = relation.nickname.orEmpty(),
        personaId = relation.personaIdentifier,
        avatar = relation.avatar.orEmpty(),
        linkedPersona = links.any { it.state.isLinked() },
        network = relation.network ?: Network.Twitter,
    )
}
