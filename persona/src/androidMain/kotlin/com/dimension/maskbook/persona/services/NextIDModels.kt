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
package com.dimension.maskbook.persona.services
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateProofParams(
    val action: String, // create or delete
    val platform: String,
    val identity: String,
    val proof_location: String,
    val public_key: String,
    val extra: String? = null
)

@Serializable
data class NextIDResponseError(
    val message: String
)

@Serializable
data class NextIDProofResponse(
    @SerialName("ids")
    val ids: List<NextID>?
)

@Serializable
data class NextID(
    @SerialName("persona")
    val persona: String?,
    @SerialName("proofs")
    val proofs: List<Proof>?
)

@Serializable
data class Proof(
    @SerialName("identity")
    val identity: String?,
    @SerialName("platform")
    val platform: String?
)
