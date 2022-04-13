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
package com.dimension.maskbook.setting.export.model

import com.ensarsarajcic.kotlinx.serialization.msgpack.MsgPackDynamicSerializer
import com.ensarsarajcic.kotlinx.serialization.msgpack.internal.MsgPackTypeDecoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class BackupMetaFile(
    val wallets: List<Wallet>,
    @SerialName("_meta_")
    val meta: Meta,
    val grantedHostPermissions: List<String>,
    val posts: List<Post>,
    val profiles: List<Profile>,
    val personas: List<Persona>,
    val relations: List<Relation>,
    // val plugin: Map<String, JsonElement>? = null, // TODO: unknown value type
) {
    @Serializable
    data class Post(
        val postBy: String, // ProfileIdentifier.toText()
        val identifier: String, // PostIVIdentifier.toText()
        val postCryptoKey: JsonWebKey? = null,
        val recipients: Recipients,
        val foundAt: Long, // Unix timestamp
        val encryptBy: String? = null, // PersonaIdentifier.toText()
        val url: String? = null,
        val summary: String? = null,
        val interestedMeta: String?, // encoded by MessagePack
    ) {
        @Serializable
        sealed class Recipients {
            data class StringValue(val value: String) : Recipients()
            data class UnionArrayValue(val value: List<RecipientElement>) : Recipients()

            @Serializable
            sealed class RecipientElement {
                class RecipientClassValue(val value: RecipientClass) : RecipientElement() {
                    @Serializable
                    data class RecipientClass(
                        val reason: List<Reason>,
                    ) {
                        @Serializable
                        data class Reason(
                            val type: ReasonType,
                            val group: String? = null,
                        ) {
                            @Serializable
                            enum class ReasonType {
                                @SerialName("auto-share")
                                AutoShare,

                                @SerialName("direct")
                                Direct,

                                @SerialName("group")
                                Group,
                            }
                        }
                    }
                }

                class StringValue(val value: String) : RecipientElement()
            }
        }
    }

    @Serializable
    data class Wallet(
        val address: String,
        val name: String,
        val passphrase: String? = null,
        val publicKey: JsonWebKey? = null,
        val privateKey: JsonWebKey? = null,
        val mnemonic: Mnemonic? = null,
        val updatedAt: Long,
        val createdAt: Long,
    )

    @Serializable
    data class Meta(
        val maskbookVersion: String,
        val createdAt: Long,
        val version: Long,
        val type: String,
    ) {
        companion object
    }

    @Serializable
    data class Persona(
        val updatedAt: Long,
        val createdAt: Long,
        val publicKey: JsonWebKey? = null,
        val identifier: String,
        @Serializable(with = LinkedProfilesSerializer::class)
        val linkedProfiles: Map<String, LinkedProfileElement.LinkedProfileClassValue.LinkedProfileClass>,
        val nickname: String? = null,
        val mnemonic: Mnemonic? = null,
        val privateKey: JsonWebKey? = null,
        val localKey: JsonWebKey? = null,
    ) {
        object LinkedProfilesItemSerializer : KSerializer<LinkedProfileElement> {
            private val msgPackDynamicSerializer = MsgPackDynamicSerializer()
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("LinkedProfilesItemSerializer", PrimitiveKind.STRING)

            override fun deserialize(decoder: Decoder): LinkedProfileElement {
                return try {
                    if (decoder is MsgPackTypeDecoder) {
                        when (val result = msgPackDynamicSerializer.deserialize(decoder)) {
                            is String -> LinkedProfileElement.StringValue(result)
                            is Map<*, *> -> {
                                LinkedProfileElement.LinkedProfileClassValue(
                                    LinkedProfileElement.LinkedProfileClassValue.LinkedProfileClass(result["connectionConfirmState"].toString())
                                )
                            }
                            else -> throw IllegalArgumentException("Unknown type: $result")
                        }
                    } else {
                        LinkedProfileElement.StringValue(decoder.decodeSerializableValue(String.serializer()))
                    }
                } catch (e: Exception) {
                    decoder.decodeSerializableValue(LinkedProfileElement.LinkedProfileClassValue.serializer())
                }
            }

            override fun serialize(encoder: Encoder, value: LinkedProfileElement) {
                when (value) {
                    is LinkedProfileElement.LinkedProfileClassValue -> {
                        encoder.encodeSerializableValue(
                            LinkedProfileElement.LinkedProfileClassValue.LinkedProfileClass.serializer(),
                            value.value
                        )
                    }
                    is LinkedProfileElement.StringValue -> {
                        encoder.encodeString(value.value)
                    }
                }
            }
        }

        object LinkedProfilesSerializer :
            KSerializer<Map<String, LinkedProfileElement.LinkedProfileClassValue.LinkedProfileClass>> {
            override val descriptor: SerialDescriptor
                get() = buildClassSerialDescriptor(
                    "LinkedProfiles",
                    PrimitiveSerialDescriptor("LinkedProfiles.String", PrimitiveKind.STRING),
                    buildClassSerialDescriptor(
                        "LinkedProfile",
                        PrimitiveSerialDescriptor("connectionConfirmState", PrimitiveKind.STRING),
                    )
                )

            override fun deserialize(decoder: Decoder): Map<String, LinkedProfileElement.LinkedProfileClassValue.LinkedProfileClass> {
                val items =
                    decoder.decodeSerializableValue(ListSerializer(ListSerializer(LinkedProfilesItemSerializer)))
                return items.associate { (it[0] as LinkedProfileElement.StringValue).value to (it[1] as LinkedProfileElement.LinkedProfileClassValue).value }
            }

            override fun serialize(
                encoder: Encoder,
                value: Map<String, LinkedProfileElement.LinkedProfileClassValue.LinkedProfileClass>,
            ) {
                val items = value.map {
                    listOf(
                        LinkedProfileElement.StringValue(it.key),
                        LinkedProfileElement.LinkedProfileClassValue(it.value)
                    )
                }
                encoder.encodeSerializableValue(ListSerializer(ListSerializer(LinkedProfilesItemSerializer)), items)
            }
        }

        @Serializable
        sealed class LinkedProfileElement {
            @Serializable
            data class LinkedProfileClassValue(val value: LinkedProfileClass) : LinkedProfileElement() {
                @Serializable
                data class LinkedProfileClass(
                    val connectionConfirmState: String,
                )
            }

            @Serializable
            data class StringValue(val value: String) : LinkedProfileElement()
        }
    }

    @Serializable
    data class Mnemonic(
        val parameter: Parameter,
        val words: String,
    ) {
        @Serializable
        data class Parameter(
            val withPassword: Boolean,
            val path: String,
        )
    }

    @Serializable
    data class Profile(
        val identifier: String,
        val updatedAt: Long,
        val createdAt: Long,
        val nickname: String? = null,
        val linkedPersona: String? = null,
        val localKey: JsonWebKey? = null,
    )

    @Serializable
    data class Relation(
        @Serializable(with = RelationFavor.RelationFavorSerializer::class)
        val favor: RelationFavor,
        val persona: String,
        val profile: String,
    ) {
        @Serializable
        enum class RelationFavor(val value: Int) {
            COLLECTED(-1),
            UNCOLLECTED(1),
            DEPRECATED(0);

            object RelationFavorSerializer : KSerializer<RelationFavor> {
                private val msgPackDynamicSerializer = MsgPackDynamicSerializer()
                override val descriptor = PrimitiveSerialDescriptor("RelationFavor", PrimitiveKind.INT)

                override fun deserialize(decoder: Decoder): RelationFavor {
                    msgPackDynamicSerializer.deserialize(decoder).let { result ->
                        return RelationFavor.values().first { it.value.toByte() == result }
                    }
                }

                override fun serialize(encoder: Encoder, value: RelationFavor) {
                    encoder.encodeByte(value.value.toByte())
                }
            }
        }
    }
}

@Serializable
data class JsonWebKey(
    val kty: String = "",
    val kid: String? = null,
    val use: String? = null,
    val key_ops: List<String>? = null,
    val alg: String? = null,
    val ext: Boolean? = null,
    val crv: String? = null,
    val x: String? = null,
    val y: String? = null,
    val d: String? = null,
    val n: String? = null,
    val e: String? = null,
    val p: String? = null,
    val q: String? = null,
    val dp: String? = null,
    val dq: String? = null,
    val qi: String? = null,
    val oth: List<RsaOtherPrimesInfo>? = null,
    val k: String? = null,
) {
    @Serializable
    data class RsaOtherPrimesInfo(val r: String, val d: String, val t: String)
}
