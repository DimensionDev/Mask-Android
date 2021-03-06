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

internal class JSMethod(
    private val extensionServices: ExtensionServices,
) {

    suspend fun createPersonaByMnemonic(
        mnemonic: String,
        nickname: String,
        password: String,
    ) {
        extensionServices.execute<Unit>(
            "persona_createPersonaByMnemonic",
            "mnemonic" to mnemonic,
            "nickname" to nickname,
            "password" to password,
        )
    }

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

    suspend fun removePersona(
        identifier: String
    ) {
        extensionServices.execute<Unit>(
            "persona_removePersona",
            "identifier" to identifier,
        )
    }

    suspend fun restoreFromJson(
        json: String
    ) {
        extensionServices.execute<Unit>(
            "persona_restoreFromJson",
            "backup" to json
        )
    }

    suspend fun restoreFromPrivateKey(
        nickname: String,
        privateKey: String,
    ) {
        extensionServices.execute<Unit>(
            "persona_restoreFromPrivateKey",
            "nickname" to nickname,
            "privateKey" to privateKey,
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
        personaIdentifier: String,
        profileIdentifier: String,
    ) {
        extensionServices.execute<Unit>(
            "persona_connectProfile",
            "profileIdentifier" to profileIdentifier,
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

    suspend fun setCurrentPersonaIdentifier(
        identifier: String
    ) {
        extensionServices.execute<Unit>(
            "persona_setCurrentPersonaIdentifier",
            "identifier" to identifier,
        )
    }
}
