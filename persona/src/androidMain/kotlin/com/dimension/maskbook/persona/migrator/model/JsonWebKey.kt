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
package com.dimension.maskbook.persona.migrator.model

@kotlinx.serialization.Serializable
data class JsonWebKey(
    val alg: String? = null,
    val crv: String? = null,
    val d: String? = null,
    val dp: String? = null,
    val dq: String? = null,
    val e: String? = null,
    val ext: Boolean? = null,
    val k: String? = null,
    val key_ops: List<String>? = null,
    val kty: String? = null,
    val n: String? = null,
    val oth: RsaOtherPrimesInfo? = null,
    val p: String? = null,
    val q: String? = null,
    val qi: String? = null,
    val use: String? = null,
    val x: String? = null,
    val y: String? = null,
) {
    @kotlinx.serialization.Serializable
    data class RsaOtherPrimesInfo(
        val d: String? = null,
        val r: String? = null,
        val t: String? = null,
    )
}
