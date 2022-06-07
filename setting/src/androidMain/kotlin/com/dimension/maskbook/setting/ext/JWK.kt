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
package com.dimension.maskbook.setting.ext

import com.dimension.maskbook.common.ext.decodeBase64Bytes
import com.dimension.maskbook.common.ext.encodeBase64String
import com.dimension.maskbook.setting.export.model.JsonWebKey

internal fun JsonWebKey.fromJWK() = d?.decodeBase64Bytes()?.toHexString()

@OptIn(ExperimentalUnsignedTypes::class)
fun ByteArray.toHexString() = asUByteArray().joinToString("") { it.toString(16).padStart(2, '0') }

internal fun String.toJWK(): JsonWebKey {
    val privateKeyData = this.encodeToByteArray()
    return JsonWebKey(
        crv = "K-256",
        kty = "EC",
        key_ops = listOf("deriveKey", "deriveBits"),
        d = privateKeyData.encodeBase64String(),
        x = "",
        y = "",
    )
}
