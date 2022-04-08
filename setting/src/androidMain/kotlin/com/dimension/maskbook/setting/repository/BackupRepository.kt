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
package com.dimension.maskbook.setting.repository

import android.content.ContentResolver
import android.net.Uri
import androidx.core.net.toUri
import com.dimension.maskbook.common.okhttp.await
import com.dimension.maskbook.setting.export.model.BackupFileMeta
import com.dimension.maskbook.setting.export.model.BackupMetaFile
import com.dimension.maskbook.setting.model.RemoteBackupData
import com.dimension.maskbook.setting.services.BackupServices
import com.dimension.maskbook.setting.services.model.AccountType
import com.dimension.maskbook.setting.services.model.Locale
import com.dimension.maskbook.setting.services.model.Scenario
import com.dimension.maskbook.setting.services.model.SendCodeBody
import com.dimension.maskbook.setting.services.model.UploadBody
import com.dimension.maskbook.setting.services.model.ValidateCodeBody
import com.ensarsarajcic.kotlinx.serialization.msgpack.MsgPack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.serializer
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator
import org.bouncycastle.crypto.params.KeyParameter
import java.io.File
import java.security.SecureRandom
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class BackupRepository(
    private val backupServices: BackupServices,
    private val cacheDir: File,
    private val contentResolver: ContentResolver,
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    suspend fun sendPhoneCode(phone: String) {
        withContext(scope.coroutineContext) {
            backupServices.sendCode(
                SendCodeBody(
                    account_type = AccountType.phone,
                    account = phone,
                    Scenario.backup,
                    Locale.en,
                )
            )
        }
    }

    suspend fun sendEmailCode(email: String) {
        withContext(scope.coroutineContext) {
            backupServices.sendCode(
                SendCodeBody(
                    account_type = AccountType.email,
                    account = email,
                    Scenario.backup,
                    Locale.en,
                )
            )
        }
    }

    suspend fun validatePhoneCode(phone: String, code: String) {
        withContext(scope.coroutineContext) {
            backupServices.validateCode(
                ValidateCodeBody(
                    code = code,
                    account = phone,
                    account_type = AccountType.phone,
                )
            )
        }
    }

    suspend fun validateEmailCode(email: String, code: String) {
        withContext(scope.coroutineContext) {
            backupServices.validateCode(
                ValidateCodeBody(
                    code = code,
                    account = email,
                    account_type = AccountType.email,
                )
            )
        }
    }

    suspend fun getBackupInformationByEmail(email: String, code: String) =
        withContext(scope.coroutineContext) {
            backupServices.download(
                ValidateCodeBody(
                    code = code,
                    account = email,
                    account_type = AccountType.email,
                )
            )
        }

    suspend fun getBackupInformationByPhone(phone: String, code: String) =
        withContext(scope.coroutineContext) {
            backupServices.download(
                ValidateCodeBody(
                    code = code,
                    account = phone,
                    account_type = AccountType.phone,
                )
            )
        }

    suspend fun encryptBackup(
        password: String,
        account: String,
        content: BackupMetaFile
    ): ByteArray = coroutineScope {
        val computedPassword = account.lowercase() + password
        val iv = SecureRandom().generateSeed(16)
        val gen = PKCS5S2ParametersGenerator(SHA256Digest())
        gen.init(MsgPack.encodeToByteArray(String.serializer(), computedPassword), iv, 10000)
        val derivedKey = gen.generateDerivedParameters(256) as KeyParameter
        val key = SecretKeySpec(derivedKey.key, "AES")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encrypted =
            cipher.doFinal(MsgPack.encodeToByteArray(BackupMetaFile.serializer(), content))
        RemoteBackupData(
            pbkdf2IV = iv,
            paramIV = cipher.iv,
            encrypted = encrypted,
        ).toByteArray()
    }

    suspend fun decryptBackup(password: String, account: String, data: ByteArray): BackupMetaFile =
        coroutineScope {
            val computedPassword = account.lowercase() + password
            val remoteBackupData = RemoteBackupData.fromByteArray(data)
            val gen = PKCS5S2ParametersGenerator(SHA256Digest())
            gen.init(
                MsgPack.encodeToByteArray(String.serializer(), computedPassword),
                remoteBackupData.pbkdf2IV,
                10000
            )
            val derivedKey = gen.generateDerivedParameters(256) as KeyParameter
            val key = SecretKeySpec(derivedKey.key, "AES")
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(remoteBackupData.paramIV))
            cipher.doFinal(remoteBackupData.encrypted)
                .let { MsgPack.decodeFromByteArray(BackupMetaFile.serializer(), it) }
        }

    suspend fun downloadBackupWithEmail(email: String, code: String) =
        withContext(scope.coroutineContext) {
            val response = backupServices.download(
                ValidateCodeBody(
                    code = code,
                    account = email,
                    account_type = AccountType.email,
                )
            )
            requireNotNull(response.download_url)
            BackupFileMeta(
                url = downloadFile(response.download_url).toUri().toString(),
                size = response.size,
                uploaded_at = response.uploaded_at,
                abstract = response.abstract,
            )
        }

    suspend fun downloadBackupWithPhone(phone: String, code: String) =
        withContext(scope.coroutineContext) {
            val response = backupServices.download(
                ValidateCodeBody(
                    code = code,
                    account = phone,
                    account_type = AccountType.phone,
                )
            )
            requireNotNull(response.download_url)
            BackupFileMeta(
                url = downloadFile(response.download_url).toUri().toString(),
                size = response.size,
                uploaded_at = response.uploaded_at,
                abstract = response.abstract,
            )
        }

    suspend fun downloadFile(url: String) = withContext(scope.coroutineContext) {
        val stream = OkHttpClient.Builder()
            .build()
            .newCall(
                Request
                    .Builder()
                    .url(url)
                    .get()
                    .build()
            )
            .await()
            .body
            ?.byteStream().let {
                requireNotNull(it)
            }
        File(cacheDir, UUID.randomUUID().toString()).apply {
            createNewFile()
            writeBytes(stream.readBytes())
        }
    }

    suspend fun uploadBackup(
        code: String,
        account_type: AccountType,
        password: String,
        account: String,
        abstract: String,
        content: BackupMetaFile,
    ) = withContext(scope.coroutineContext) {
        val response = backupServices.upload(
            UploadBody(
                code = code,
                account_type = account_type,
                account = account,
                abstract = abstract
            )
        )
        requireNotNull(response.upload_url)
        OkHttpClient.Builder()
            .build()
            .newCall(
                Request.Builder()
                    .url(response.upload_url)
                    .put(encryptBackup(password, account, content).toRequestBody())
                    .build()
            ).execute()
    }

    suspend fun saveLocality(it: Uri, meta: BackupMetaFile, password: String, account: String) =
        coroutineScope {
            contentResolver.openOutputStream(it)?.use {
                it.write(encryptBackup(password, account, meta))
            }
        }
}
