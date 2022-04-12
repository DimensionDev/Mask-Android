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
import com.dimension.maskbook.common.ext.encodeJson
import com.dimension.maskbook.common.ext.encodeJsonElement
import com.dimension.maskbook.persona.export.model.IndexedDBPersona
import com.dimension.maskbook.persona.export.model.IndexedDBPost
import com.dimension.maskbook.persona.export.model.IndexedDBProfile
import com.dimension.maskbook.persona.export.model.IndexedDBRelation
import com.dimension.maskbook.persona.export.model.LinkedProfileDetailsState
import com.dimension.maskbook.setting.export.model.BackupMetaFile
import com.dimension.maskbook.setting.ext.fromJWK
import com.dimension.maskbook.setting.ext.toJWK
import com.dimension.maskbook.wallet.export.model.BackupWalletData
import kotlinx.serialization.json.JsonObject

fun IndexedDBPost.toBackupPost() = BackupMetaFile.Post(
    postBy = postBy,
    identifier = identifier,
    postCryptoKey = postCryptoKey?.decodeJson(),
    recipients = recipients?.let {
        BackupMetaFile.Post.Recipients.UnionArrayValue(
            value = it.flatMap {
                listOf(
                    BackupMetaFile.Post.Recipients.RecipientElement.StringValue(it.key),
                    BackupMetaFile.Post.Recipients.RecipientElement.RecipientClassValue(it.value.decodeJson())
                )
            }
        )
    } ?: BackupMetaFile.Post.Recipients.StringValue("everyone"),
    foundAt = foundAt,
    encryptBy = encryptBy,
    url = url,
    summary = summary,
    interestedMeta = interestedMeta.encodeJson(),
)

fun BackupMetaFile.Post.toIndexDbPost() = IndexedDBPost(
    postBy = postBy,
    identifier = identifier,
    postCryptoKey = postCryptoKey?.encodeJsonElement(),
    recipients = recipients.let {
        when (it) {
            is BackupMetaFile.Post.Recipients.UnionArrayValue -> it.value.windowed(2).associate {
                (it[0] as BackupMetaFile.Post.Recipients.RecipientElement.StringValue).value to
                    (it[1] as BackupMetaFile.Post.Recipients.RecipientElement.RecipientClassValue).encodeJsonElement<BackupMetaFile.Post.Recipients.RecipientElement.RecipientClassValue, JsonObject>()
            }.toMutableMap()
            is BackupMetaFile.Post.Recipients.StringValue -> null
        }
    },
    foundAt = foundAt,
    encryptBy = encryptBy,
    url = url,
    summary = summary,
    interestedMeta = interestedMeta?.decodeJson(),
)

fun BackupWalletData.toBackupWallet() = BackupMetaFile.Wallet(
    address = address,
    name = name,
    passphrase = passphrase,
    createdAt = createdAt,
    updatedAt = updatedAt,
    publicKey = publicKey?.toJWK(),
    privateKey = privateKey?.toJWK(),
    mnemonic = mnemonic?.let { mnemonic ->
        derivationPath?.let { derivationPath ->
            BackupMetaFile.Mnemonic(
                words = mnemonic,
                parameter = BackupMetaFile.Mnemonic.Parameter(
                    withPassword = false,
                    path = derivationPath,
                )
            )
        }
    },
)

fun BackupMetaFile.Wallet.toWalletData() = BackupWalletData(
    address = address,
    name = name,
    passphrase = passphrase,
    createdAt = createdAt,
    updatedAt = updatedAt,
    publicKey = publicKey?.fromJWK(),
    privateKey = privateKey?.fromJWK(),
    mnemonic = mnemonic?.words,
    derivationPath = mnemonic?.parameter?.path,
)

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
    linkedProfiles = linkedProfiles.filter { it.size % 2 == 0 }.associate {
        (it.first() as BackupMetaFile.Persona.LinkedProfileElement.StringValue).value to
            IndexedDBPersona.LinkedProfileDetails(
                connectionConfirmState = LinkedProfileDetailsState.valueOf(
                    (it[1] as BackupMetaFile.Persona.LinkedProfileElement.LinkedProfileClassValue).value.connectionConfirmState
                )
            )
    }
)
