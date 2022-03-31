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
package com.dimension.maskbook.persona.export.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class IndexedDBPersona(
    val identifier: String,
    val linkedProfiles: Map<String, LinkedProfileDetails>,
    val nickname: String? = null,
    val privateKey: JsonObject? = null,
    val publicKey: JsonObject? = null,
    val localKey: JsonObject? = null,
    val mnemonic: Mnemonic? = null,
    val hasLogout: Boolean = false,
    val uninitialized: Boolean = false,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
) {
    @Serializable
    data class LinkedProfileDetails(
        val connectionConfirmState: LinkedProfileDetailsState
    )

    @Serializable
    data class Mnemonic(
        val words: String,
        val parameter: Parameter,
    ) {
        @Serializable
        data class Parameter(
            val path: String,
            val withPassword: Boolean,
        )
    }
}
