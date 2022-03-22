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
package com.dimension.maskbook.persona.db.migrator.mapper

import com.dimension.maskbook.persona.db.model.DbLinkedProfileRecord
import com.dimension.maskbook.persona.db.model.DbPersonaRecord
import com.dimension.maskbook.persona.db.model.PersonaWithLinkedProfile
import com.dimension.maskbook.persona.model.indexed.IndexedDBPersona
import com.dimension.maskbook.persona.model.indexed.IndexedDBPersona.Key.Companion.fromJsonObject

fun IndexedDBPersona.toDbPersonaRecord(): DbPersonaRecord {
    return DbPersonaRecord(
        identifier = identifier,
        mnemonic = mnemonic?.words,
        path = mnemonic?.parameter?.path,
        withPassword = mnemonic?.parameter?.withPassword ?: false,
        publicKey = publicKey?.toJsonObject(),
        privateKey = privateKey?.toJsonObject(),
        localKey = localKey?.toJsonObject(),
        nickname = nickname,
        hasLogout = hasLogout,
        initialized = !uninitialized,
        updateAt = updatedAt,
        createAt = createdAt,
        email = "",
        phone = "",
    )
}

fun IndexedDBPersona.toLinkedProfiles(): List<DbLinkedProfileRecord> {
    return linkedProfiles.map { entry ->
        DbLinkedProfileRecord(
            personaIdentifier = identifier,
            profileIdentifier = entry.key,
            state = entry.value.connectionConfirmState,
        )
    }
}

fun PersonaWithLinkedProfile.toIndexedDBPersona(): IndexedDBPersona {
    return IndexedDBPersona(
        identifier = persona.identifier,
        mnemonic = IndexedDBPersona.Mnemonic(
            words = persona.mnemonic.orEmpty(),
            parameter = IndexedDBPersona.Mnemonic.Parameter(
                path = persona.path.orEmpty(),
                withPassword = persona.withPassword ?: false,
            )
        ),
        publicKey = persona.publicKey?.fromJsonObject(),
        privateKey = persona.privateKey?.fromJsonObject(),
        localKey = persona.localKey?.fromJsonObject(),
        nickname = persona.nickname,
        hasLogout = persona.hasLogout ?: false,
        uninitialized = persona.initialized?.not() ?: false,
        updatedAt = persona.updateAt,
        createdAt = persona.createAt,
        linkedProfiles = linkedProfiles.associate {
            it.profileIdentifier to IndexedDBPersona.LinkedProfileDetails(it.state)
        },
    )
}
