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
package com.dimension.maskbook.common.util

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.MGF1ParameterSpec
import java.util.Calendar
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import javax.security.auth.x500.X500Principal
import kotlin.math.min

/**
 * Thx: https://github.com/momosecurity/rhizobia_J/blob/master/src/main/java/com/immomo/rhizobia/rhizobia_J/crypto/RSAUtils.java
 *
 * bufferDecryptSize = keySize / 8
 * bufferEncryptSize = bufferDecryptSize - padding
 *
 * padding:
 *  RSA/ECB/PKCS1Padding or RSA             :   11
 *  RSA/ECB/OAEPWithSHA-1AndMGF1Padding     :   42
 *  RSA/ECB/OAEPWithSHA-256AndMGF1Padding   :   66
 */
interface CipherHelper {
    fun encrypt(plainText: ByteArray, publicKey: PublicKey): ByteArray
    fun decrypt(cipherText: ByteArray, privateKey: PrivateKey): ByteArray
    fun createSecretKeyPair(context: Context, alias: String): KeyPair
}

@TargetApi(Build.VERSION_CODES.M)
class CipherHelperAboveM(
    private val provider: String,
    private val keySize: Int,
) : CipherHelper {

    private val bufferDecryptSize: Int = keySize / 8
    private val bufferEncryptSize: Int = bufferDecryptSize - 66

    private val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")

    override fun encrypt(plainText: ByteArray, publicKey: PublicKey): ByteArray {
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, getSpec())
        return cipher.doFinalSplit(plainText, bufferEncryptSize)
    }

    override fun decrypt(cipherText: ByteArray, privateKey: PrivateKey): ByteArray {
        cipher.init(Cipher.DECRYPT_MODE, privateKey, getSpec())
        return cipher.doFinalSplit(cipherText, bufferDecryptSize)
    }

    override fun createSecretKeyPair(context: Context, alias: String): KeyPair {
        val generator = KeyPairGenerator.getInstance("RSA", provider)

        val spec = KeyGenParameterSpec
            .Builder(alias, KeyProperties.PURPOSE_DECRYPT or KeyProperties.PURPOSE_ENCRYPT)
            .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
            .setKeySize(keySize)
            .build()
        generator.initialize(spec)

        return generator.generateKeyPair()
    }

    private fun getSpec(): AlgorithmParameterSpec {
        return OAEPParameterSpec(
            "SHA-256",
            "MGF1",
            MGF1ParameterSpec.SHA1,
            PSource.PSpecified.DEFAULT
        )
    }
}

class CipherHelperBelowM(
    private val provider: String,
    private val keySize: Int,
) : CipherHelper {

    private val bufferDecryptSize: Int = keySize / 8
    private val bufferEncryptSize: Int = bufferDecryptSize - 11

    private val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")

    override fun encrypt(plainText: ByteArray, publicKey: PublicKey): ByteArray {
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinalSplit(plainText, bufferEncryptSize)
    }

    override fun decrypt(cipherText: ByteArray, privateKey: PrivateKey): ByteArray {
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return cipher.doFinalSplit(cipherText, bufferDecryptSize)
    }

    override fun createSecretKeyPair(context: Context, alias: String): KeyPair {
        val generator = KeyPairGenerator.getInstance("RSA", provider)

        val start = Calendar.getInstance()
        val end = Calendar.getInstance()
        end.add(Calendar.YEAR, 20)

        @Suppress("DEPRECATION")
        val spec = android.security.KeyPairGeneratorSpec.Builder(context)
            .setAlias(alias)
            .setSubject(X500Principal("CN=$alias, O=Android Authority"))
            .setSerialNumber(BigInteger.ONE)
            .setStartDate(start.time)
            .setEndDate(end.time)
            .setKeySize(keySize)
            .build()
        generator.initialize(spec)

        return generator.generateKeyPair()
    }
}

private fun Cipher.doFinalSplit(input: ByteArray, segmentSize: Int): ByteArray {
    val inputLen = input.size
    if (inputLen <= segmentSize) {
        return doFinal(input)
    }

    return ByteArrayOutputStream().use {
        var offSet = 0
        while (inputLen - offSet > 0) {
            it.write(doFinal(input, offSet, min(inputLen - offSet, segmentSize)))
            offSet += segmentSize
        }
        it.toByteArray()
    }
}
