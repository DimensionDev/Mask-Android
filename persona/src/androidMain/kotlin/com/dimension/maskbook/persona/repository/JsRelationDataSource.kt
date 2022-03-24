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
import com.dimension.maskbook.persona.db.migrator.mapper.toDbRelationRecord
import com.dimension.maskbook.persona.db.migrator.mapper.toIndexedDBRelation
import com.dimension.maskbook.persona.db.sql.buildQueryRelationsSql
import com.dimension.maskbook.persona.model.indexed.IndexedDBRelation
import com.dimension.maskbook.persona.model.options.CreateRelationOptions
import com.dimension.maskbook.persona.model.options.DeleteRelationOptions
import com.dimension.maskbook.persona.model.options.QueryRelationOptions
import com.dimension.maskbook.persona.model.options.QueryRelationsOptions
import com.dimension.maskbook.persona.model.options.UpdateRelationOptions

class JsRelationDataSource(database: PersonaDatabase) {

    private val relationDao = database.relationDao()

    suspend fun createRelation(options: CreateRelationOptions): IndexedDBRelation {
        val newRelation = options.relation.toDbRelationRecord()
        relationDao.insert(newRelation)
        return options.relation
    }

    suspend fun queryRelation(options: QueryRelationOptions): IndexedDBRelation? {
        return relationDao.find(
            personaIdentifier = options.personaIdentifier,
            profileIdentifier = options.profileIdentifier
        )?.toIndexedDBRelation()
    }

    suspend fun queryRelations(options: QueryRelationsOptions): List<IndexedDBRelation> {
        val query = buildQueryRelationsSql(
            personaIdentifier = options.personaIdentifier,
            network = options.network,
            nameContains = options.nameContains,
            favor = options.favor,
            pageOptions = options.pageOptions,
        )
        return relationDao.findListRaw(query).map {
            it.toIndexedDBRelation()
        }
    }

    suspend fun updateRelation(options: UpdateRelationOptions): IndexedDBRelation {
        val newRelation = options.relation.toDbRelationRecord()
        relationDao.insert(newRelation)
        return options.relation
    }

    suspend fun deleteRelation(options: DeleteRelationOptions) {
        relationDao.delete(
            personaIdentifier = options.personaIdentifier,
            profileIdentifier = options.profileIdentifier,
        )
    }
}
