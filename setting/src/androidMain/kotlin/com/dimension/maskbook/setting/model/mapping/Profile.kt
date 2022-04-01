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
package com.dimension.maskbook.setting.model.mapping

import com.dimension.maskbook.common.ext.decodeJson
import com.dimension.maskbook.common.ext.encodeJsonElement
import com.dimension.maskbook.persona.export.model.IndexedDBPersona
import com.dimension.maskbook.persona.export.model.IndexedDBProfile
import com.dimension.maskbook.persona.export.model.IndexedDBRelation
import com.dimension.maskbook.persona.export.model.LinkedProfileDetailsState
import com.dimension.maskbook.setting.export.model.BackupMetaFile

fun IndexedDBRelation.toBackupRelation() = BackupMetaFile.Relation(
    favor = BackupMetaFile.Relation.RelationFavor.values().firstOrNull { it.value == favor }
        ?: BackupMetaFile.Relation.RelationFavor.UNCOLLECTED,
    persona = personaIdentifier,
    profile = profileIdentifier,
)

fun BackupMetaFile.Relation.toIndexedDBRelation() = IndexedDBRelation(
    favor = favor.value,
    personaIdentifier = persona,
    profileIdentifier = profile
)

fun IndexedDBProfile.toBackupProfile() = BackupMetaFile.Profile(
    identifier = identifier,
    updatedAt = updatedAt,
    createdAt = createdAt,
    nickname = nickname,
    linkedPersona = linkedPersona,
    localKey = localKey?.decodeJson(),
)

fun BackupMetaFile.Profile.toIndexedDBProfile() = IndexedDBProfile(
    identifier = identifier,
    updatedAt = updatedAt,
    createdAt = createdAt,
    nickname = nickname,
    linkedPersona = linkedPersona,
    localKey = localKey?.encodeJsonElement(),
)

fun IndexedDBPersona.toBackupPersona() = BackupMetaFile.Persona(
    updatedAt = updatedAt,
    createdAt = createdAt,
    publicKey = publicKey?.decodeJson(),
    identifier = identifier,
    nickname = nickname,
    mnemonic = mnemonic?.let {
        BackupMetaFile.Mnemonic(
            parameter = BackupMetaFile.Mnemonic.Parameter(
                withPassword = it.parameter.withPassword,
                path = it.parameter.path,
            ),
            words = it.words
        )
    },
    privateKey = privateKey?.decodeJson(),
    localKey = localKey?.decodeJson(),
    linkedProfiles = linkedProfiles.map {
        listOf(
            BackupMetaFile.Persona.LinkedProfileElement.StringValue(it.key),
            BackupMetaFile.Persona.LinkedProfileElement.LinkedProfileClassValue(
                value = BackupMetaFile.Persona.LinkedProfileElement.LinkedProfileClassValue.LinkedProfileClass(
                    connectionConfirmState = it.value.connectionConfirmState.name
                )
            )
        )
    }
)

fun BackupMetaFile.Persona.toIndexedDBPersona() = IndexedDBPersona(
    updatedAt = updatedAt,
    createdAt = createdAt,
    publicKey = publicKey?.encodeJsonElement(),
    identifier = identifier,
    nickname = nickname,
    mnemonic = mnemonic?.let {
        IndexedDBPersona.Mnemonic(
            parameter = IndexedDBPersona.Mnemonic.Parameter(
                withPassword = it.parameter.withPassword,
                path = it.parameter.path,
            ),
            words = it.words
        )
    },
    privateKey = privateKey?.encodeJsonElement(),
    localKey = localKey?.encodeJsonElement(),
    linkedProfiles = linkedProfiles.filter { it.size == 2 }.associate {
        (it.first() as BackupMetaFile.Persona.LinkedProfileElement.StringValue).value to
            IndexedDBPersona.LinkedProfileDetails(
                connectionConfirmState = LinkedProfileDetailsState.valueOf(
                    (it[1] as BackupMetaFile.Persona.LinkedProfileElement.LinkedProfileClassValue).value.connectionConfirmState
                )
            )
    }
)
