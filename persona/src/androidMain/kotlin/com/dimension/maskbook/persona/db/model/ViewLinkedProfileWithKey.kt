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
import kotlinx.serialization.json.JsonObject

@DatabaseView(
    "SELECT link.personaIdentifier, link.profileIdentifier, link.state, " +
        "persona.publicKeyRaw, persona.privateKeyRaw, persona.localKeyRaw " +
        "FROM DbLinkedProfileRecord link " +
        "LEFT OUTER JOIN DbPersonaRecord persona on persona.identifier=link.personaIdentifier"
)
data class ViewLinkedProfileWithKey(
    val personaIdentifier: String,
    val profileIdentifier: String,
    val state: LinkedProfileDetailsState,
    @TypeConverters(EncryptJsonObjectConverter::class)
    @ColumnInfo(name = "publicKeyRaw", typeAffinity = ColumnInfo.BLOB)
    var publicKey: JsonObject? = null,
    @TypeConverters(EncryptJsonObjectConverter::class)
    @ColumnInfo(name = "privateKeyRaw", typeAffinity = ColumnInfo.BLOB)
    var privateKey: JsonObject? = null,
    @TypeConverters(EncryptJsonObjectConverter::class)
    @ColumnInfo(name = "localKeyRaw", typeAffinity = ColumnInfo.BLOB)
    val localKey: JsonObject? = null,
)
