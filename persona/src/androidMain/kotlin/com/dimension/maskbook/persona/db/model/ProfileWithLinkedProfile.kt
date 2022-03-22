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

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.TypeConverters
import com.dimension.maskbook.persona.db.EncryptJsonObjectConverter
import com.dimension.maskbook.persona.export.model.LinkedProfileDetailsState
import com.dimension.maskbook.persona.export.model.Network
import kotlinx.serialization.json.JsonObject

@DatabaseView(
    "SELECT profile.identifier, profile.nickname, profile.network, profile.avatar, " +
        "profile.updatedAt, profile.createdAt, " +
        "link.personaIdentifier, link.state, " +
        "persona.localKeyRaw " +
        "FROM DbProfileRecord profile " +
        "INNER JOIN DbLinkedProfileRecord link ON link.profileIdentifier=profile.identifier " +
        "INNER JOIN DbPersonaRecord persona ON persona.identifier=link.personaIdentifier"
)
data class ProfileWithLinkedProfile(
    val identifier: String,
    val nickname: String? = null,
    val network: Network? = null,
    val avatar: String? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val personaIdentifier: String = "",
    val state: LinkedProfileDetailsState = LinkedProfileDetailsState.Pending,
    @TypeConverters(EncryptJsonObjectConverter::class)
    @ColumnInfo(name = "localKeyRaw", typeAffinity = ColumnInfo.BLOB)
    val localKey: JsonObject? = null,
)
