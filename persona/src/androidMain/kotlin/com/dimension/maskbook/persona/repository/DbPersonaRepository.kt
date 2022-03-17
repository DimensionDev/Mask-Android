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
import com.dimension.maskbook.persona.export.model.PersonaData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DbPersonaRepository(database: PersonaDatabase) {

    private val personaDao = database.personaDao()

    suspend fun deletePersona(personaIdentifier: String) {
        personaDao.delete(personaIdentifier)
    }

    suspend fun getPersona(personaIdentifier: String): PersonaData? {
        return personaDao.find(personaIdentifier)?.toPersonaData()
    }

    suspend fun getPersonaFirst(): PersonaData? {
        return personaDao.findFirst()?.toPersonaData()
    }

    fun getPersonaFlow(personaIdentifier: String): Flow<PersonaData?> {
        return personaDao.getFlow(personaIdentifier).map {
            it?.toPersonaData()
        }
    }

    suspend fun getPersonaList(): List<PersonaData> {
        return personaDao.findList().map {
            it.toPersonaData()
        }
    }

    fun getPersonaListFlow(): Flow<List<PersonaData>> {
        return personaDao.getListFlow().map { list ->
            list.map { it.toPersonaData() }
        }
    }

    suspend fun updateEmail(personaIdentifier: String, email: String) {
        personaDao.updateEmail(personaIdentifier, email)
    }

    suspend fun updatePhone(personaIdentifier: String, phone: String) {
        personaDao.updatePhone(personaIdentifier, phone)
    }

    suspend fun updateNickName(personaIdentifier: String, name: String) {
        personaDao.updateNickName(personaIdentifier, name)
    }

    suspend fun contains(personaIdentifier: String): Boolean {
        return personaDao.count(personaIdentifier) > 0
    }

    suspend fun containsMnemonic(mnemonic: String): Boolean {
        return personaDao.countOfMnemonic(mnemonic) > 0
    }

    suspend fun isEmpty(): Boolean {
        return personaDao.count() == 0
    }
}

private fun DbPersonaRecord.toPersonaData(): PersonaData {
    return PersonaData(
        identifier = identifier,
        name = nickname.orEmpty(),
        email = email,
        phone = phone,
    )
}
