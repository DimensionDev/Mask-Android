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
package com.dimension.maskbook.setting.util

import com.dimension.maskbook.common.ext.JSON
import com.dimension.maskbook.common.ext.msgPack
import com.dimension.maskbook.setting.export.model.BackupMetaFile
import com.dimension.maskbook.setting.export.model.BackupWrongPasswordException
import com.dimension.maskbook.setting.model.RemoteBackupData
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.decodeFromString
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator
import org.bouncycastle.crypto.params.KeyParameter
import org.msgpack.jackson.dataformat.MessagePackMapper
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptUtils {
    fun encryptBackup(password: String, account: String, content: BackupMetaFile): ByteArray {
        val computedPassword = account.lowercase() + password
        val iv = SecureRandom().generateSeed(16)
        val gen = PKCS5S2ParametersGenerator(SHA256Digest())
        gen.init(msgPack.encodeToByteArray(String.serializer(), computedPassword), iv, 10000)
        val derivedKey = gen.generateDerivedParameters(256) as KeyParameter
        val key = SecretKeySpec(derivedKey.key, "AES")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encrypted =
            cipher.doFinal(msgPack.encodeToByteArray(BackupMetaFile.serializer(), content))
        return RemoteBackupData(
            pbkdf2IV = iv,
            paramIV = cipher.iv,
            encrypted = encrypted,
        ).toByteArray()
    }

    fun decryptBackup(password: String, account: String, data: ByteArray): BackupMetaFile {
        val computedPassword = account.lowercase() + password
        val remoteBackupData = RemoteBackupData.fromByteArray(data)
        val gen = PKCS5S2ParametersGenerator(SHA256Digest())
        gen.init(
            msgPack.encodeToByteArray(String.serializer(), computedPassword),
            remoteBackupData.pbkdf2IV,
            10000
        )
        val derivedKey = gen.generateDerivedParameters(256) as KeyParameter
        val key = SecretKeySpec(derivedKey.key, "AES")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(remoteBackupData.paramIV))
        kotlin.runCatching {
            cipher.doFinal(remoteBackupData.encrypted)
        }.onSuccess {
            val map = MessagePackMapper().readValue(it, object : TypeReference<Map<String, Any>>() {})
            val json = ObjectMapper().writeValueAsString(map)
            return JSON.decodeFromString<BackupMetaFile>(json)
        }.onFailure {
            throw BackupWrongPasswordException
        }
        throw BackupWrongPasswordException
    }
}
