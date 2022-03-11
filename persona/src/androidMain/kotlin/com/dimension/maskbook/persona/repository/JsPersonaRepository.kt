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
import com.dimension.maskbook.persona.db.dao.PersonaDao
import com.dimension.maskbook.persona.db.model.DbPersonaRecord
import com.dimension.maskbook.persona.model.options.CreatePersonaOptions
import com.dimension.maskbook.persona.model.options.DeletePersonaOptions
import com.dimension.maskbook.persona.model.options.QueryPersonaByProfileOptions
import com.dimension.maskbook.persona.model.options.QueryPersonaOptions
import com.dimension.maskbook.persona.model.options.QueryPersonasOptions
import com.dimension.maskbook.persona.model.options.UpdatePersonaOptions

class JsPersonaRepository(database: PersonaDatabase) {

    private val personaDao = database.personaDao()

    suspend fun createPersona(options: CreatePersonaOptions): DbPersonaRecord? {
        return personaDao.addWithResult(options.persona)
    }

    suspend fun queryPersona(options: QueryPersonaOptions): DbPersonaRecord? {
        val query = buildString {
            append("SELECT * FROM DbPersonaRecord WHERE identifier = ${options.identifier} ")
            val whereSql = buildWhereSql(
                hasPrivateKey = options.hasPrivateKey,
                includeLogout = options.includeLogout,
                nameContains = options.nameContains,
                initialized = options.initialized
            )
            if (whereSql.isNotEmpty()) {
                append("$whereSql ")
            }
            append("LIMIT 1")
        }
        return personaDao.findRaw(SimpleSQLiteQuery(query))
    }

    suspend fun queryPersonaByProfile(options: QueryPersonaByProfileOptions): DbPersonaRecord? {
        val query = buildString {
            append("SELECT * FROM DbPersonaRecord WHERE identifier in (SELECT personaIdentifier FROM DbRelationRecord WHERE profileIdentifier = ${options.profileIdentifier}) ")
            val whereSql = buildWhereSql(
                hasPrivateKey = options.hasPrivateKey,
                includeLogout = options.includeLogout,
                nameContains = options.nameContains,
                initialized = options.initialized
            )
            if (whereSql.isNotEmpty()) {
                append("$whereSql ")
            }
            append("LIMIT 1")
        }
        return personaDao.findRaw(SimpleSQLiteQuery(query))
    }

    suspend fun queryPersonas(options: QueryPersonasOptions): List<DbPersonaRecord> {
        val query = buildString {
            append("SELECT * FROM DbPersonaRecord ")
            val whereSql = buildWhereSql(
                identifiers = options.identifiers,
                hasPrivateKey = options.hasPrivateKey,
                includeLogout = options.includeLogout,
                nameContains = options.nameContains,
                initialized = options.initialized
            )
            if (whereSql.isNotEmpty()) {
                append("WHERE $whereSql ")
            }
            if (options.pageOption != null) {
                append("LIMIT ${options.pageOption.limitStart} OFFSET ${options.pageOption.limitOffset}")
            }
        }
        return personaDao.findListRaw(SimpleSQLiteQuery(query))
    }

    suspend fun updatePersona(options: UpdatePersonaOptions): DbPersonaRecord? {
        val oldPersona = personaDao.find(options.persona.identifier)
        val newPersona = options.persona

        if (oldPersona == null) {
            return if (options.options.createWhenNotExist) {
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

        // deleteUndefinedFields ?

        val resultPersona = when (options.options.linkedProfileMergePolicy) {
            0 -> newPersona
            1 -> oldPersona.merge(newPersona)
            else -> newPersona
        }
        return personaDao.addWithResult(resultPersona)
    }

    suspend fun deletePersona(options: DeletePersonaOptions) {
        personaDao.delete(options.identifier)
    }
}

private fun buildWhereSql(
    identifiers: List<String>? = null,
    hasPrivateKey: Boolean? = null,
    includeLogout: Boolean? = null,
    nameContains: String? = null,
    initialized: Boolean? = null,
): String {
    return listOfNotNull(
        if (!identifiers.isNullOrEmpty()) {
            "identifier in (${identifiers.joinToString(",")})"
        } else null,
        if (hasPrivateKey != null) {
            "privateKey IS NOT NULL"
        } else null,
        if (includeLogout != null) {
            "hasLogout = $includeLogout"
        } else null,
        if (!nameContains.isNullOrEmpty()) {
            "nickname LIKE '%$nameContains%'"
        } else null,
        if (initialized != null) {
            "initialized = $initialized"
        } else null
    ).joinToString(separator = " AND ")
}

private suspend fun PersonaDao.addWithResult(persona: DbPersonaRecord): DbPersonaRecord? {
    return if (add(persona) > 0) persona else null
}
