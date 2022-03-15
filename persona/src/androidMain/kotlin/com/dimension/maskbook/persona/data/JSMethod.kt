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
package com.dimension.maskbook.persona.data

import com.dimension.maskbook.common.ext.execute
import com.dimension.maskbook.extension.export.ExtensionServices
import com.dimension.maskbook.persona.export.model.Network
import com.dimension.maskbook.persona.export.model.Persona

internal class JSMethod(
    private val extensionServices: ExtensionServices,
) {
    // suspend fun getCurrentDetectedProfile(): String? {
    //     return extensionServices.execute("SNSAdaptor_getCurrentDetectedProfile")
    // }

    suspend fun getCurrentDetectedProfileDelegateToSNSAdaptor(): String? {
        // return extensionServices.execute("getCurrentDetectedProfile_delegate_to_SNSAdaptor")
        return null
    }

    suspend fun createPersonaByMnemonic(
        mnemonic: String,
        nickname: String,
        password: String,
    ): Persona? {
        return extensionServices.execute(
            "persona_createPersonaByMnemonic",
            "mnemonic" to mnemonic,
            "nickname" to nickname,
            "password" to password,
        )
    }

    // suspend fun queryPersonas(
    //     identifier: String?,
    //     hasPrivateKey: Boolean
    // ): List<Persona> {
    //     return extensionServices.execute(
    //         "persona_queryPersonas",
    //         *listOfNotNull(
    //             identifier?.let {
    //                 "identifier" to identifier
    //             },
    //             "hasPrivateKey" to hasPrivateKey,
    //         ).toTypedArray()
    //     ) ?: emptyList()
    // }

    // suspend fun queryMyPersonas(
    //     network: Network?,
    // ): List<Persona> {
    //     return extensionServices.execute(
    //         "persona_queryMyPersonas",
    //         *listOfNotNull(
    //             network?.let {
    //                 "network" to network.value
    //             }
    //         ).toTypedArray(),
    //     ) ?: emptyList()
    // }

    suspend fun updatePersonaInfo(
        identifier: String,
        nickname: String
    ) {
        extensionServices.execute<Unit>(
            "persona_updatePersonaInfo",
            "identifier" to identifier,
            "data" to mapOf(
                "nickname" to nickname
            )
        )
    }

    // suspend fun removePersona(
    //     identifier: String
    // ) {
    //     extensionServices.execute<Unit>(
    //         "persona_removePersona",
    //         "identifier" to identifier,
    //     )
    // }

    suspend fun restoreFromJson(
        json: String
    ) {
        extensionServices.execute<Unit>(
            "persona_restoreFromJson",
            "backup" to json
        )
    }

    suspend fun restoreFromBase64(
        backup: String
    ) {
        extensionServices.execute<Unit>(
            "persona_restoreFromBase64",
            "backup" to backup
        )
    }

    suspend fun connectProfile(
        network: Network,
        personaIdentifier: String,
        userName: String,
    ) {
        extensionServices.execute<Unit>(
            "persona_connectProfile",
            "profileIdentifier" to "person:${network.value}/$userName",
            "personaIdentifier" to personaIdentifier,
        )
    }

    suspend fun disconnectProfile(
        identifier: String
    ) {
        extensionServices.execute<Unit>(
            "persona_disconnectProfile",
            "identifier" to identifier
        )
    }

    suspend fun backupMnemonic(
        identifier: String
    ): String? {
        return extensionServices.execute(
            "persona_backupMnemonic",
            "identifier" to identifier
        )
    }

    suspend fun backupBase64(
        identifier: String
    ): String? {
        return extensionServices.execute(
            "persona_backupBase64",
            "identifier" to identifier
        )
    }

    suspend fun backupJson(
        identifier: String
    ): String? {
        return extensionServices.execute(
            "persona_backupJson",
            "identifier" to identifier,
        )
    }

    suspend fun backupPrivateKey(
        identifier: String
    ): String? {
        return extensionServices.execute(
            "persona_backupPrivateKey",
            "identifier" to identifier,
        )
    }

    // suspend fun queryProfiles(
    //     network: Network
    // ): List<Profile> {
    //     return extensionServices.execute(
    //         "profile_queryProfiles",
    //         "network" to network.value
    //     ) ?: emptyList()
    // }
    //
    // suspend fun queryMyProfile(
    //     network: Network
    // ): List<Profile> {
    //     return extensionServices.execute(
    //         "profile_queryMyProfiles",
    //         "network" to network.value
    //     ) ?: emptyList()
    // }
    //
    // suspend fun updateProfileInfo(
    //     identifier: String,
    //     nickname: String?,
    //     avatarURL: String?
    // ) {
    //     extensionServices.execute<Unit>(
    //         "profile_updateProfileInfo",
    //         "identifier" to identifier,
    //         "data" to listOfNotNull(
    //             nickname?.let {
    //                 "nickname" to nickname
    //             },
    //             avatarURL?.let {
    //                 "avatarURL" to avatarURL
    //             },
    //         ).toMap()
    //     )
    // }
    //
    // suspend fun removeProfile(
    //     identifier: String
    // ) {
    //     extensionServices.execute<Unit>(
    //         "profile_removeProfile",
    //         "identifier" to identifier,
    //     )
    // }
}
