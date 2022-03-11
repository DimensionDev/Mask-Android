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

import androidx.sqlite.db.SimpleSQLiteQuery
import com.dimension.maskbook.persona.db.PersonaDatabase
import com.dimension.maskbook.persona.db.dao.RelationDao
import com.dimension.maskbook.persona.db.model.DbRelationRecord
import com.dimension.maskbook.persona.model.options.CreateRelationOptions
import com.dimension.maskbook.persona.model.options.DeleteRelationOptions
import com.dimension.maskbook.persona.model.options.QueryRelationsOptions
import com.dimension.maskbook.persona.model.options.UpdateRelationOptions

class JsRelationRepository(database: PersonaDatabase) {
    private val relationDao = database.relationDao()

    suspend fun createRelation(options: CreateRelationOptions): DbRelationRecord? {
        val newRelation = options.relation
        newRelation.createdAt = System.currentTimeMillis()
        newRelation.updatedAt = System.currentTimeMillis()
        return relationDao.addWithResult(newRelation)
    }

    suspend fun queryRelations(options: QueryRelationsOptions): List<DbRelationRecord> {
        val query = buildString {
            append("SELECT * FROM DbRelationRecord WHERE personaIdentifier=${options.personaIdentifier} ")
            val whereSql = buildWhereSql(
                network = options.network,
                nameContains = options.nameContains,
                favor = options.favor,
            )
            if (whereSql.isNotEmpty()) {
                append("$whereSql ")
            }
            if (options.pageOption != null) {
                append("LIMIT ${options.pageOption.limitStart} OFFSET ${options.pageOption.limitOffset}")
            }
        }
        return relationDao.findListRaw(SimpleSQLiteQuery(query))
    }

    suspend fun updateRelation(options: UpdateRelationOptions): DbRelationRecord? {
        val oldRelation = relationDao.find(
            personaIdentifier = options.relation.personaIdentifier,
            profileIdentifier = options.relation.profileIdentifier
        )
        val newRelation = options.relation
        newRelation.createdAt = oldRelation?.createdAt ?: System.currentTimeMillis()
        newRelation.updatedAt = System.currentTimeMillis()
        return relationDao.addWithResult(newRelation)
    }

    suspend fun deleteRelation(options: DeleteRelationOptions) {
        relationDao.delete(
            personaIdentifier = options.personaIdentifier,
            profileIdentifier = options.profileIdentifier,
        )
    }
}

private suspend fun RelationDao.addWithResult(relation: DbRelationRecord): DbRelationRecord {
    add(relation) ; return relation
}
