package com.dimension.maskbook.wallet.repository

import android.content.ContentResolver
import android.net.Uri
import androidx.core.net.toUri
import com.dimension.maskbook.wallet.ext.await
import com.dimension.maskbook.wallet.services.WalletServices
import com.dimension.maskbook.wallet.services.model.*
import com.dimension.maskbook.wallet.services.model.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*

class BackupRepository(
    private val walletServices: WalletServices,
    private val cacheDir: File,
    private val contentResolver: ContentResolver,
) {
    suspend fun sendPhoneCode(phone: String) {
        walletServices.backupServices.sendCode(
            SendCodeBody(
                account_type = AccountType.phone,
                account = phone,
                Scenario.backup,
                Locale.en,
            )
        )
    }

    suspend fun sendEmailCode(email: String) {
        walletServices.backupServices.sendCode(
            SendCodeBody(
                account_type = AccountType.email,
                account = email,
                Scenario.backup,
                Locale.en,
            )
        )
    }

    suspend fun validatePhoneCode(phone: String, code: String) {
        walletServices.backupServices.validateCode(
            ValidateCodeBody(
                code = code,
                account = phone,
                account_type = AccountType.phone,
            )
        )
    }

    suspend fun validateEmailCode(email: String, code: String) {
        walletServices.backupServices.validateCode(
            ValidateCodeBody(
                code = code,
                account = email,
                account_type = AccountType.email,
            )
        )
    }

    suspend fun getBackupInformationByEmail(email: String, code: String): DownloadResponse {
        return walletServices.backupServices.download(
            ValidateCodeBody(
                code = code,
                account = email,
                account_type = AccountType.email,
            )
        )
    }

    suspend fun getBackupInformationByPhone(phone: String, code: String): DownloadResponse {
        return walletServices.backupServices.download(
            ValidateCodeBody(
                code = code,
                account = phone,
                account_type = AccountType.phone,
            )
        )
    }

    suspend fun downloadBackupWithEmail(email: String, code: String): Uri {
        val response = walletServices.backupServices.download(
            ValidateCodeBody(
                code = code,
                account = email,
                account_type = AccountType.email,
            )
        )
        requireNotNull(response.download_url)
        return downloadFile(response.download_url).toUri()
    }

    suspend fun downloadBackupWithPhone(phone: String, code: String): Uri {
        val response = walletServices.backupServices.download(
            ValidateCodeBody(
                code = code,
                account = phone,
                account_type = AccountType.phone,
            )
        )
        requireNotNull(response.download_url)
        return downloadFile(response.download_url).toUri()
    }

    suspend fun downloadFile(url: String): File {
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
        return File(cacheDir, UUID.randomUUID().toString()).apply {
            createNewFile()
            writeBytes(stream.readBytes())
        }
    }

    suspend fun uploadBackup(
        code: String,
        account_type: AccountType,
        account: String,
        abstract: String,
        content: String,
    ) = withContext(Dispatchers.IO) {
        val response = walletServices.backupServices.upload(
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
                    .put(content.toRequestBody())
                    .build()
            ).execute()
    }

    suspend fun saveLocality(it: Uri, json: String) = coroutineScope {
        contentResolver.openOutputStream(it)?.use {
            it.write(json.toByteArray())
        }
    }
}