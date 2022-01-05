package com.dimension.maskbook.wallet.ext

import android.util.Base64
import java.lang.Exception
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object BackupCrypto {
    fun encrypt(password: String, content: String) {

    }

    private fun createAESFromPassword(password: String) {

    }
}
