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
import com.dimension.maskbook.persona.db.migrator.mapper.toDbPersonaRecord
import com.dimension.maskbook.persona.db.migrator.mapper.toIndexedDBPersona
import com.dimension.maskbook.persona.db.sql.buildQueryPersonaByProfileSql
import com.dimension.maskbook.persona.db.sql.buildQueryPersonaSql
import com.dimension.maskbook.persona.db.sql.buildQueryPersonasSql
import com.dimension.maskbook.persona.model.indexed.IndexedDBPersona
import com.dimension.maskbook.persona.model.options.CreatePersonaOptions
import com.dimension.maskbook.persona.model.options.DeletePersonaOptions
import com.dimension.maskbook.persona.model.options.QueryPersonaByProfileOptions
import com.dimension.maskbook.persona.model.options.QueryPersonaOptions
import com.dimension.maskbook.persona.model.options.QueryPersonasOptions
import com.dimension.maskbook.persona.model.options.UpdatePersonaOptions

class JsPersonaDataSource(database: PersonaDatabase) {

    private val personaDao = database.personaDao()
    private val linkedProfileDao = database.linkedProfileDao()
    private val relationDao = database.relationDao()

    suspend fun createPersona(options: CreatePersonaOptions): IndexedDBPersona {
        val newPersona = options.persona.toDbPersonaRecord()
        personaDao.insert(newPersona)
        return options.persona
    }

    suspend fun queryPersona(options: QueryPersonaOptions): IndexedDBPersona? {
        val query = buildQueryPersonaSql(
            identifier = options.personaIdentifier,
            hasPrivateKey = options.hasPrivateKey,
            includeLogout = options.includeLogout,
            nameContains = options.nameContains,
            initialized = options.initialized,
        )
        return personaDao.findRaw(query)?.toIndexedDBPersona()
    }

    suspend fun queryPersonaByProfile(options: QueryPersonaByProfileOptions): IndexedDBPersona? {
        val query = buildQueryPersonaByProfileSql(
            profileIdentifier = options.profileIdentifier,
            hasPrivateKey = options.hasPrivateKey,
            includeLogout = options.includeLogout,
            nameContains = options.nameContains,
            initialized = options.initialized,
        )
        return personaDao.findRaw(query)?.toIndexedDBPersona()
    }

    suspend fun queryPersonas(options: QueryPersonasOptions): List<IndexedDBPersona> {
        val query = buildQueryPersonasSql(
            identifiers = options.personaIdentifiers,
            hasPrivateKey = options.hasPrivateKey,
            includeLogout = options.includeLogout,
            nameContains = options.nameContains,
            initialized = options.initialized,
            pageOptions = options.pageOptions,
        )
        return personaDao.findListRaw(query).map {
            it.toIndexedDBPersona()
        }
    }

    suspend fun updatePersona(options: UpdatePersonaOptions): IndexedDBPersona? {
        val oldPersona = personaDao.find(options.persona.identifier)
        val newPersona = options.persona.toDbPersonaRecord()

        if (oldPersona == null) {
            return if (options.options.createWhenNotExist) {
                newPersona.createdAt = System.currentTimeMillis()
                newPersona.updatedAt = System.currentTimeMillis()
                personaDao.insert(newPersona)
                options.persona
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
                newPersona.createdAt = oldPersona.createdAt
                newPersona
            }
            1 -> {
                oldPersona.updatedAt = System.currentTimeMillis()
                oldPersona.merge(newPersona)
            }
            else -> oldPersona
        }
        personaDao.insert(resultPersona)
        return options.persona
    }

    suspend fun deletePersona(options: DeletePersonaOptions) {
        personaDao.delete(options.personaIdentifier)
        linkedProfileDao.deleteWithPersona(options.personaIdentifier)
        relationDao.deleteWithPersona(options.personaIdentifier)
    }
}
