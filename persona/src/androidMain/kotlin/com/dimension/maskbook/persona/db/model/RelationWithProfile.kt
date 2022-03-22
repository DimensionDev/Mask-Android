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

import androidx.room.DatabaseView
import com.dimension.maskbook.persona.export.model.LinkedProfileDetailsState
import com.dimension.maskbook.persona.export.model.Network

@DatabaseView(
    "SELECT relation.personaIdentifier, relation.profileIdentifier, relation.favor, " +
        "relation.updatedAt, relation.createdAt, " +
        "profile.nickname, profile.network, " +
        "link.personaIdentifier as linkedPersona, link.state " +
        "FROM DbRelationRecord as relation " +
        "INNER JOIN DbProfileRecord as profile ON profile.identifier=relation.profileIdentifier " +
        "INNER JOIN DbLinkedProfileRecord as link ON link.profileIdentifier=relation.profileIdentifier"
)
data class RelationWithProfile(
    val personaIdentifier: String,
    val profileIdentifier: String,
    val favor: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val nickname: String? = null,
    val network: Network? = null,
    val linkedPersona: String = "",
    val state: LinkedProfileDetailsState = LinkedProfileDetailsState.Pending,
)
