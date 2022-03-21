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
package com.dimension.maskbook.common.manager

import android.content.Context
import android.os.Build
import com.dimension.maskbook.common.ext.decodeBase64Bytes
import com.dimension.maskbook.common.ext.encodeBase64String
import com.dimension.maskbook.common.util.CipherHelperAboveM
import com.dimension.maskbook.common.util.CipherHelperBelowM
import java.security.KeyPair
import java.security.KeyStore

/**
 * Thx https://github.com/khoantt91/nab_development_challenge/blob/master/app/src/main/java/com/example/nabchallenge/repository/datastore/dj/DataStoreModule.kt
 */
actual class KeyStoreManager(private val context: Context) {

    private val keyStore by lazy {
        KeyStore.getInstance(androidKeyStore).apply {
            load(null)
        }
    }

    private val cipherHelper by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CipherHelperAboveM(androidKeyStore, defaultKeySize)
        } else {
            CipherHelperBelowM(androidKeyStore, defaultKeySize)
        }
    }

    private val keyPair by lazy {
        val entry = keyStore.getEntry(appAlias, null) as? KeyStore.PrivateKeyEntry
        if (entry is KeyStore.PrivateKeyEntry) {
            KeyPair(entry.certificate.publicKey, entry.privateKey)
        } else {
            cipherHelper.createSecretKeyPair(context, appAlias)
        }
    }

    actual fun encryptData(plainText: ByteArray): ByteArray {
        return cipherHelper.encrypt(plainText, keyPair.public)
    }

    actual fun decryptData(cipherText: ByteArray): ByteArray {
        return cipherHelper.decrypt(cipherText, keyPair.private)
    }

    fun encryptDataBase64(plainText: String): String {
        return encryptData(plainText.toByteArray()).encodeBase64String()
    }

    fun decryptDataBase64(cipherText: String): String {
        return String(decryptData(cipherText.decodeBase64Bytes()))
    }

    companion object {
        private const val appAlias = "Mask-Android"
        private const val androidKeyStore = "AndroidKeyStore"

        private const val defaultKeySize = 2048
    }
}
