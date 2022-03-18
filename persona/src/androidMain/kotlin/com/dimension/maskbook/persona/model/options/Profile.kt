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
package com.dimension.maskbook.persona.model.options

import com.dimension.maskbook.persona.export.model.LinkedProfileDetailsState
import com.dimension.maskbook.persona.model.indexed.IndexedDBProfile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateProfileOptions(
    val profile: IndexedDBProfile,
)

@Serializable
data class QueryProfileOptions(
    @SerialName("identifier")
    val profileIdentifier: String,
    val network: String? = null,
    val nameContains: String? = null,
    // val pageOption: PageOptions? = null,
)

@Serializable
data class QueryProfilesOptions(
    @SerialName("identifiers")
    val profileIdentifiers: List<String>? = null,
    val hasLinkedPersona: Boolean = false,
    val network: String? = null,
    val nameContains: String? = null,
    @SerialName("pageOption")
    val pageOptions: PageOptions? = null,
)

@Serializable
data class UpdateProfileOptions(
    val profile: IndexedDBProfile,
    val options: Options,
) {
    @Serializable
    data class Options(
        val createWhenNotExist: Boolean = false,
    )
}

@Serializable
data class DeleteProfileOptions(
    @SerialName("identifier")
    val profileIdentifier: String,
)

@Serializable
data class AttachProfileOptions(
    val personaIdentifier: String,
    val profileIdentifier: String,
    val state: State,
) {
    @Serializable
    data class State(
        val connectionConfirmState: LinkedProfileDetailsState,
    )
}

@Serializable
data class DetachProfileOptions(
    @SerialName("identifier")
    val profileIdentifier: String,
)
