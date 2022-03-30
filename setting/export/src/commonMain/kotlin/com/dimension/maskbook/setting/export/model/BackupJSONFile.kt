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

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement

@Serializable
data class BackupJSONFile(
    val wallets: List<Wallet>,
    @SerialName("_meta_")
    val meta: Meta,
    val grantedHostPermissions: List<String>,
    val posts: List<Post>,
    val profiles: List<Profile>,
    val personas: List<Persona>,
    val relations: List<Relation>,
    val plugin: Map<String, JsonElement>? = null, // TODO: unknown value type
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
        val interestedMeta: String? // encoded by MessagePack
    ) {
        @Serializable
        sealed class Recipients {
            class StringValue(val value: String) : Recipients()
            class UnionArrayValue(val value: List<RecipientElement>) : Recipients()
            @Serializable
            sealed class RecipientElement {
                class RecipientClassValue(val value: RecipientClass) : RecipientElement() {
                    @Serializable
                    data class RecipientClass(
                        val reason: List<Reason>
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
        val type: String
    ) {
        companion object {
            val Default: Meta get() = Meta(
                maskbookVersion = "2.5.0",
                createdAt = System.currentTimeMillis(),
                version = 2,
                type = "maskbook-backup"
            )
        }
    }

    @Serializable
    data class Persona(
        val updatedAt: Long,
        val createdAt: Long,
        val publicKey: JsonWebKey,
        val identifier: String,
        val linkedProfiles: List<List<LinkedProfileElement>>,
        val nickname: String? = null,
        val mnemonic: Mnemonic? = null,
        val privateKey: JsonWebKey? = null,
        val localKey: JsonWebKey? = null
    ) {
        @Serializable
        sealed class LinkedProfileElement {
            class LinkedProfileClassValue(val value: LinkedProfileClass) : LinkedProfileElement() {
                @Serializable
                data class LinkedProfileClass(
                    val connectionConfirmState: String
                )
            }
            class StringValue(val value: String) : LinkedProfileElement()
        }
    }

    @Serializable
    data class Mnemonic(
        val parameter: Parameter,
        val words: String
    ) {
        @Serializable
        data class Parameter(
            val withPassword: Boolean,
            val path: String
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
        val favor: RelationFavor,
        val persona: String,
        val profile: String
    ) {
        @Serializable
        enum class RelationFavor(val value: Int) {
            COLLECTED(-1),
            UNCOLLECTED(1),
            DEPRECATED(0);

            @OptIn(ExperimentalSerializationApi::class)
            @Serializer(forClass = RelationFavor::class)
            object RelationFavorSerializer : KSerializer<RelationFavor> {
                override val descriptor = PrimitiveSerialDescriptor("RelationFavor", PrimitiveKind.INT)

                override fun deserialize(decoder: Decoder): RelationFavor {
                    return RelationFavor.values().first { it.value == decoder.decodeInt() }
                }

                override fun serialize(encoder: Encoder, value: RelationFavor) {
                    encoder.encodeInt(value.value)
                }
            }
        }
    }
}

@Serializable
data class JsonWebKey(
    var kty: String = "",
    var kid: String? = null,
    var use: String? = null,
    var key_ops: List<String>? = null,
    var alg: String? = null,
    var ext: Boolean? = null,
    var crv: String? = null,
    var x: String? = null,
    var y: String? = null,
    var d: String? = null,
    var n: String? = null,
    var e: String? = null,
    var p: String? = null,
    var q: String? = null,
    var dp: String? = null,
    var dq: String? = null,
    var qi: String? = null,
    var oth: List<RsaOtherPrimesInfo>? = null,
    var k: String? = null
) {
    @Serializable
    data class RsaOtherPrimesInfo(val r: String, val d: String, val t: String)
}
