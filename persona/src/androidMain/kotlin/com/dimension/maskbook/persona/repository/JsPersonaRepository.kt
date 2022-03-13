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
import com.dimension.maskbook.persona.db.dao.PersonaDao
import com.dimension.maskbook.persona.db.model.DbPersonaRecord
import com.dimension.maskbook.persona.db.sql.asSqlQuery
import com.dimension.maskbook.persona.db.sql.buildQueryPersonaByProfileSql
import com.dimension.maskbook.persona.db.sql.buildQueryPersonaSql
import com.dimension.maskbook.persona.db.sql.buildQueryPersonasSql
import com.dimension.maskbook.persona.model.options.CreatePersonaOptions
import com.dimension.maskbook.persona.model.options.DeletePersonaOptions
import com.dimension.maskbook.persona.model.options.QueryPersonaByProfileOptions
import com.dimension.maskbook.persona.model.options.QueryPersonaOptions
import com.dimension.maskbook.persona.model.options.QueryPersonasOptions
import com.dimension.maskbook.persona.model.options.UpdatePersonaOptions

class JsPersonaRepository(database: PersonaDatabase) {

    private val personaDao = database.personaDao()

    suspend fun createPersona(options: CreatePersonaOptions): DbPersonaRecord? {
        val newPersona = options.persona
        newPersona.createAt = System.currentTimeMillis()
        newPersona.updateAt = System.currentTimeMillis()
        return personaDao.addWithResult(newPersona)
    }

    suspend fun queryPersona(options: QueryPersonaOptions): DbPersonaRecord? {
        val query = buildQueryPersonaSql(
            identifier = options.identifier,
            hasPrivateKey = options.hasPrivateKey,
            includeLogout = options.includeLogout,
            nameContains = options.nameContains,
            initialized = options.initialized,
        )
        return personaDao.findRaw(query.asSqlQuery())
    }

    suspend fun queryPersonaByProfile(options: QueryPersonaByProfileOptions): DbPersonaRecord? {
        val query = buildQueryPersonaByProfileSql(
            profileIdentifier = options.profileIdentifier,
            hasPrivateKey = options.hasPrivateKey,
            includeLogout = options.includeLogout,
            nameContains = options.nameContains,
            initialized = options.initialized,
        )
        return personaDao.findRaw(query.asSqlQuery())
    }

    suspend fun queryPersonas(options: QueryPersonasOptions): List<DbPersonaRecord> {
        val query = buildQueryPersonasSql(
            identifiers = options.identifiers,
            hasPrivateKey = options.hasPrivateKey,
            includeLogout = options.includeLogout,
            nameContains = options.nameContains,
            initialized = options.initialized,
            pageOptions = options.pageOptions,
        )
        return personaDao.findListRaw(query.asSqlQuery())
    }

    suspend fun updatePersona(options: UpdatePersonaOptions): DbPersonaRecord? {
        val oldPersona = personaDao.find(options.persona.identifier)
        val newPersona = options.persona

        if (oldPersona == null) {
            return if (options.options.createWhenNotExist) {
                newPersona.createAt = System.currentTimeMillis()
                newPersona.updateAt = System.currentTimeMillis()
                personaDao.addWithResult(newPersona)
            } else null
        }

        if (options.options.protectPrivateKey) {
            if (newPersona.privateKey.isNullOrEmpty() &&
                !oldPersona.privateKey.isNullOrEmpty()
            ) {
                newPersona.privateKey = oldPersona.privateKey
            }
        }

        // TODO deleteUndefinedFields ?

        val resultPersona = when (options.options.linkedProfileMergePolicy) {
            0 -> {
                newPersona.createAt = oldPersona.createAt
                newPersona
            }
            1 -> {
                oldPersona.updateAt = System.currentTimeMillis()
                oldPersona.merge(newPersona)
            }
            else -> oldPersona
        }
        return personaDao.addWithResult(resultPersona)
    }

    suspend fun deletePersona(options: DeletePersonaOptions) {
        personaDao.delete(options.identifier)
    }
}

private suspend fun PersonaDao.addWithResult(persona: DbPersonaRecord): DbPersonaRecord {
    insert(persona) ; return persona
}
