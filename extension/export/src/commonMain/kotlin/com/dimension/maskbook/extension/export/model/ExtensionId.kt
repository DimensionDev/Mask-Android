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
package com.dimension.maskbook.extension.export.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int

@Serializable(with = ExtensionIdSerializer::class)
class ExtensionId internal constructor(
    internal val intId: Int? = null,
    internal val stringId: String? = null,
) {
    override fun toString(): String {
        if (intId != null) {
            return intId.toString()
        } else if (stringId != null) {
            return stringId
        }
        return ""
    }

    companion object {
        fun fromAny(any: Any?): ExtensionId {
            if (any is Int) {
                return ExtensionId(intId = any)
            } else if (any is String) {
                return ExtensionId(stringId = any)
            }
            return ExtensionId()
        }
    }
}

object ExtensionIdSerializer : KSerializer<ExtensionId> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor(
            "ExtensionId",
            PrimitiveSerialDescriptor("StringId", PrimitiveKind.STRING),
            PrimitiveSerialDescriptor("IntId", PrimitiveKind.INT),
        )
    override fun serialize(encoder: Encoder, value: ExtensionId) {
        if (value.stringId != null) {
            encoder.encodeString(value.stringId)
        } else if (value.intId != null) {
            encoder.encodeInt(value.intId)
        }
    }

    override fun deserialize(decoder: Decoder): ExtensionId {
        if (decoder is JsonDecoder) {
            when (val value = decoder.decodeJsonElement()) {
                is JsonPrimitive -> {
                    return if (value.isString) {
                        ExtensionId(stringId = value.content)
                    } else {
                        ExtensionId(intId = value.int)
                    }
                }
                else -> Unit
            }
        }
        return ExtensionId()
    }
}
