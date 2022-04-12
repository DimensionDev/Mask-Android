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
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class KeyStoreManagerTest {

    @Test
    fun test_bytes_encrypted() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val data = "this is data"

        val keystoreCrypto1 = KeyStoreManager(context)
        val encrypted = keystoreCrypto1.encryptData(data.toByteArray())

        val keystoreCrypto2 = KeyStoreManager(context)
        val decrypted = keystoreCrypto2.decryptData(encrypted)

        assertEquals(data, String(decrypted))
    }

    @Test
    fun test_string_encrypted() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val data = "this is data"

        val keystoreCrypto1 = KeyStoreManager(context)
        val encryptedBase64 = keystoreCrypto1.encryptDataBase64(data)

        val keystoreCrypto2 = KeyStoreManager(context)
        val decrypted = keystoreCrypto2.decryptDataBase64(encryptedBase64)

        assertEquals(data, decrypted)
    }

    @Test
    fun test_large_string_encrypted() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val data = stringOfLength(800).toByteArray()

        val keystoreCrypto1 = KeyStoreManager(context)
        val encrypted = keystoreCrypto1.encryptData(data)

        val keystoreCrypto2 = KeyStoreManager(context)
        val decrypted = keystoreCrypto2.decryptData(encrypted)

        assertContentEquals(data, decrypted)
    }

    private fun stringOfLength(length: Int): String {
        return String(CharArray(length) { 'a' })
    }
}
