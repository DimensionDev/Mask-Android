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

import androidx.room.withTransaction
import com.dimension.maskbook.persona.db.PersonaDatabase
import com.dimension.maskbook.persona.db.migrator.mapper.toDbPersonaRecord
import com.dimension.maskbook.persona.db.migrator.mapper.toIndexedDBPersona
import com.dimension.maskbook.persona.db.migrator.mapper.toLinkedProfiles
import com.dimension.maskbook.persona.db.model.DbLinkedProfileRecord
import com.dimension.maskbook.persona.db.model.DbPersonaRecord
import com.dimension.maskbook.persona.db.model.PersonaPrivateKey
import com.dimension.maskbook.persona.db.model.PersonaPrivateKey.Companion.encode
import com.dimension.maskbook.persona.export.model.IndexedDBPersona
import com.dimension.maskbook.persona.export.model.LinkedProfileDetailsState
import com.dimension.maskbook.persona.export.model.PersonaData
import com.dimension.maskbook.persona.export.model.PersonaQrCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonObject

class DbPersonaDataSource(private val database: PersonaDatabase) {

    private val personaDao = database.personaDao()
    private val linkedProfileDao = database.linkedProfileDao()
    private val relationDao = database.relationDao()

    suspend fun deletePersona(personaIdentifier: String) {
        database.withTransaction {
            personaDao.delete(personaIdentifier)
            linkedProfileDao.deleteWithPersona(personaIdentifier)
            relationDao.deleteWithPersona(personaIdentifier)
        }
    }

    suspend fun getPersona(personaIdentifier: String): PersonaData? {
        return personaDao.find(personaIdentifier)?.toPersonaData()
    }

    suspend fun getPersonaPrivateKey(personaIdentifier: String): JsonObject? {
        return personaDao.find(personaIdentifier)?.privateKey
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

    suspend fun updateNickName(personaIdentifier: String, name: String) {
        personaDao.updateNickName(personaIdentifier, name)
    }

    suspend fun contains(personaIdentifier: String): Boolean {
        return personaDao.count(personaIdentifier) > 0
    }

    suspend fun containsMnemonic(mnemonic: String): Boolean {
        return personaDao.findList().any { it.mnemonic == mnemonic }
    }

    suspend fun containsPrivateKey(privateKey: String): Boolean {
        return personaDao.findList().any {
            PersonaPrivateKey.decode(privateKey) == it.privateKeyData
        }
    }

    suspend fun isEmpty(): Boolean {
        return personaDao.count() == 0
    }

    suspend fun hasConnected(profileIdentifier: String): Boolean {
        return linkedProfileDao.count(profileIdentifier) > 0
    }

    suspend fun updateAvatar(identifier: String, avatar: String?) {
        personaDao.updateAvatar(identifier, avatar)
    }

    suspend fun getPersonaQrCode(identifier: String) = database.personaDao().find(identifier)?.let {
        PersonaQrCode(
            nickName = it.nickname.orEmpty(),
            identifier = it.identifier,
            privateKeyBase64 = it.privateKeyData?.encode() ?: return null,
            identityWords = it.mnemonic.orEmpty(),
            avatar = it.avatar
        )
    }

    suspend fun getIndexDbPersonaRecord(): List<IndexedDBPersona> {
        return personaDao.getListWithProfile().map {
            it.toIndexedDBPersona()
        }
    }

    suspend fun addAll(list: List<IndexedDBPersona>) {
        val currentPersona = getPersonaList()
        val data = list.filter { it.identifier !in currentPersona.map { it.identifier } }
        personaDao.insertAll(data.map { it.toDbPersonaRecord() })
        linkedProfileDao.insert(data.flatMap { it.toLinkedProfiles() })
    }

    suspend fun connectProfile(personaIdentifier: String, profileIdentifier: String) {
        linkedProfileDao.insert(
            DbLinkedProfileRecord(
                personaIdentifier = personaIdentifier,
                profileIdentifier = profileIdentifier,
                state = LinkedProfileDetailsState.Confirmed,
            )
        )
    }

    suspend fun disconnectProfile(personaIdentifier: String, profileIdentifier: String) {
        linkedProfileDao.delete(
            personaIdentifier = personaIdentifier,
            profileIdentifier = profileIdentifier,
        )
    }
}

private fun DbPersonaRecord.toPersonaData(): PersonaData {
    return PersonaData(
        identifier = identifier,
        name = nickname.orEmpty(),
        avatar = avatar,
        owned = privateKey != null
    )
}
