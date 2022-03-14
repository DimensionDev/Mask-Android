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

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
@Entity
data class DbPersonaRecord(
    @PrimaryKey val identifier: String,
    var mnemonic: String? = null,
    var path: String? = null,
    var withPassword: Boolean? = null,
    var publicKey: JsonObject? = null,
    var privateKey: JsonObject? = null,
    var localKey: JsonObject? = null,
    var nickname: String? = null,
    var createAt: Long = 0,
    var updateAt: Long = 0,
    var hasLogout: Boolean? = null,
    var initialized: Boolean? = null,
    var avatar: String? = null,
) {
    fun merge(record: DbPersonaRecord): DbPersonaRecord {
        if (record.mnemonic != null && record.mnemonic != mnemonic) mnemonic = record.mnemonic
        if (record.path != null && record.path != path) path = record.path
        if (record.withPassword != null && record.withPassword != withPassword) withPassword = record.withPassword
        if (record.publicKey != null && record.publicKey != publicKey) publicKey = record.publicKey
        if (record.privateKey != null && record.privateKey != privateKey) privateKey = record.privateKey
        if (record.localKey != null && record.localKey != localKey) localKey = record.localKey
        if (record.nickname != null && record.nickname != nickname) nickname = record.nickname
        if (record.hasLogout != null && record.hasLogout != hasLogout) hasLogout = record.hasLogout
        if (record.initialized != null && record.initialized != initialized) initialized = record.initialized
        if (record.avatar != null && record.avatar != avatar) avatar = record.avatar
        return this
    }
}
