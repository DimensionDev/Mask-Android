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
package com.dimension.maskbook.persona.db.model

import android.util.Base64
import com.dimension.maskbook.common.ext.decodeBase64Bytes
import com.dimension.maskbook.common.ext.encodeBase64String
import com.ensarsarajcic.kotlinx.serialization.msgpack.MsgPack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray

@Serializable
data class PersonaPrivateKey(
    @SerialName("crv")
    val crv: String? = null,
    @SerialName("d")
    val d: String? = null,
    @SerialName("ext")
    val ext: Boolean? = null,
    @SerialName("key_ops")
    val keyOps: List<String>? = null,
    @SerialName("kty")
    val kty: String? = null,
    @SerialName("x")
    val x: String? = null,
    @SerialName("y")
    val y: String? = null
) {
    companion object {
        fun PersonaPrivateKey.encode() = MsgPack.encodeToByteArray(this).encodeBase64String(flag = Base64.NO_WRAP)

        fun decode(base64: String) = MsgPack.decodeFromByteArray<PersonaPrivateKey>(base64.decodeBase64Bytes(flag = Base64.NO_WRAP))
    }
}
