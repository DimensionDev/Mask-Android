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

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.dimension.maskbook.common.ext.decodeBase64Bytes
import com.dimension.maskbook.common.ext.encodeBase64String
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.MGF1ParameterSpec
import java.util.Calendar
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import javax.security.auth.x500.X500Principal

/**
 * Thx https://github.com/khoantt91/nab_development_challenge/blob/master/app/src/main/java/com/example/nabchallenge/repository/datastore/dj/DataStoreModule.kt
 */
actual class KeystoreManager(private val context: Context) {

    private val keyStore by lazy {
        KeyStore.getInstance(androidKeyStore).apply {
            load(null)
        }
    }

    private val cipher by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Cipher.getInstance(transformationAboveM)
        } else {
            Cipher.getInstance(transformationBelowM)
        }
    }

    private val keyPair by lazy {
        getOrCreateSecretKeyPair(appAlias)
    }

    actual fun encryptData(dataDecrypted: ByteArray): ByteArray {
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.public, getSpec())
        return cipher.doFinal(dataDecrypted)
    }

    fun encryptDataBase64(dataDecrypted: String): String {
        return encryptData(dataDecrypted.toByteArray()).encodeBase64String()
    }

    actual fun decryptData(dataEncrypted: ByteArray): ByteArray {
        cipher.init(Cipher.DECRYPT_MODE, keyPair.private, getSpec())
        return cipher.doFinal(dataEncrypted)
    }

    fun decryptDataBase64(dataEncrypted: String): String {
        return String(decryptData(dataEncrypted.decodeBase64Bytes()))
    }

    private fun getOrCreateSecretKeyPair(@Suppress("SameParameterValue") alias: String): KeyPair {
        val entry = keyStore.getEntry(alias, null) as? KeyStore.PrivateKeyEntry
        return if (entry is KeyStore.PrivateKeyEntry) {
            KeyPair(entry.certificate.publicKey, entry.privateKey)
        } else {
            createSecretKeyPair(alias)
        }
    }

    private fun createSecretKeyPair(alias: String): KeyPair {
        val generator = KeyPairGenerator.getInstance(algorithmWithRsa, androidKeyStore)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initGeneratorWithKeyGenParameterSpec(generator, alias)
        } else {
            initGeneratorWithKeyPairGeneratorSpec(generator, alias)
        }
        return generator.generateKeyPair()
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun initGeneratorWithKeyGenParameterSpec(generator: KeyPairGenerator, alias: String) {
        val spec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_DECRYPT or KeyProperties.PURPOSE_ENCRYPT,
        ).setDigests(
            KeyProperties.DIGEST_SHA256,
            KeyProperties.DIGEST_SHA512,
        ).setEncryptionPaddings(
            KeyProperties.ENCRYPTION_PADDING_RSA_OAEP,
        ).build()
        generator.initialize(spec)
    }

    private fun initGeneratorWithKeyPairGeneratorSpec(generator: KeyPairGenerator, alias: String) {
        val start = Calendar.getInstance()
        val end = Calendar.getInstance()
        end.add(Calendar.YEAR, 30)

        @Suppress("DEPRECATION")
        val spec = android.security.KeyPairGeneratorSpec.Builder(context)
            .setAlias(alias)
            .setSubject(X500Principal("CN=$alias, O=Android Authority"))
            .setSerialNumber(BigInteger.ONE)
            .setStartDate(start.time)
            .setEndDate(end.time)
            .build()
        generator.initialize(spec)
    }

    private fun getSpec(): AlgorithmParameterSpec? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA1,
                PSource.PSpecified.DEFAULT
            )
        } else null
    }

    companion object {
        private const val appAlias = "Mask-Android"

        private const val algorithmWithRsa = "RSA"
        private const val androidKeyStore = "AndroidKeyStore"

        private const val transformationAboveM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
        private const val transformationBelowM = "RSA/ECB/PKCS1Padding"
    }
}
