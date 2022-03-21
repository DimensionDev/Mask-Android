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
package com.dimension.maskbook.persona.mock.model

import com.dimension.maskbook.persona.db.model.DbPersonaRecord
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.jetbrains.annotations.TestOnly

@TestOnly
fun mockDbPersonaRecord(
    identifier: String,
    nickname: String,
    mnemonic: String = "this is words",
    hasLogout: Boolean = false,
    privateKey: JsonObject? = JsonObject(
        mapOf(
            "key_ops" to JsonArray(
                listOf(
                    JsonPrimitive("derive1"),
                    JsonPrimitive("derive2"),
                )
            ),
            "x" to JsonPrimitive("xxxxx"),
            "y" to JsonPrimitive("yyyyy"),
        )
    ),
) = DbPersonaRecord(
    identifier = identifier,
    nickname = nickname,
    hasLogout = hasLogout,
    mnemonic = mnemonic,
    publicKey = JsonObject(
        mapOf(
            "key_ops" to JsonArray(
                listOf(
                    JsonPrimitive("derive1"),
                    JsonPrimitive("derive2"),
                )
            ),
            "x" to JsonPrimitive("xxxxx"),
            "y" to JsonPrimitive("yyyyy"),
        )
    ),
    privateKey = privateKey,
    localKey = JsonObject(
        mapOf(
            "key_ops" to JsonArray(
                listOf(
                    JsonPrimitive("derive1"),
                    JsonPrimitive("derive2"),
                )
            ),
            "k" to JsonPrimitive("123"),
        )
    ),
    createAt = 1646386534519,
    updateAt = 1646386534519,
)
