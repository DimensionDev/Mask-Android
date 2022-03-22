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
package com.dimension.maskbook.persona.db

import androidx.room.Database
import androidx.room.ProvidedTypeConverter
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.dimension.maskbook.common.ext.decodeJson
import com.dimension.maskbook.common.ext.encodeJson
import com.dimension.maskbook.common.manager.KeyStoreManager
import com.dimension.maskbook.persona.db.dao.LinkedProfileDao
import com.dimension.maskbook.persona.db.dao.PersonaDao
import com.dimension.maskbook.persona.db.dao.PostDao
import com.dimension.maskbook.persona.db.dao.ProfileDao
import com.dimension.maskbook.persona.db.dao.RelationDao
import com.dimension.maskbook.persona.db.model.DbLinkedProfileRecord
import com.dimension.maskbook.persona.db.model.DbPersonaRecord
import com.dimension.maskbook.persona.db.model.DbPostRecord
import com.dimension.maskbook.persona.db.model.DbProfileRecord
import com.dimension.maskbook.persona.db.model.DbRelationRecord
import com.dimension.maskbook.persona.db.model.RelationWithProfile
import com.dimension.maskbook.persona.export.model.LinkedProfileDetailsState
import com.dimension.maskbook.persona.export.model.Network
import kotlinx.serialization.json.JsonObject

@Database(
    entities = [
        DbPersonaRecord::class,
        DbProfileRecord::class,
        DbRelationRecord::class,
        DbLinkedProfileRecord::class,
        DbPostRecord::class,
    ],
    views = [
        RelationWithProfile::class,
    ],
    version = 4,
)
@TypeConverters(
    JsonObjectConverter::class,
    LinkedProfileDetailsStateConverter::class,
    NetworkConverter::class,
    MutableMapJsonObjectConverter::class,
    EncryptStringConverter::class,
    EncryptJsonObjectConverter::class,
)
abstract class PersonaDatabase : RoomDatabase() {
    abstract fun personaDao(): PersonaDao
    abstract fun profileDao(): ProfileDao
    abstract fun linkedProfileDao(): LinkedProfileDao
    abstract fun relationDao(): RelationDao
    abstract fun postDao(): PostDao
}

internal class JsonObjectConverter {
    @TypeConverter
    fun fromJsonObject(value: String?): JsonObject? {
        return value?.decodeJson()
    }

    @TypeConverter
    fun toJsonObject(jsonObject: JsonObject?): String? {
        return jsonObject?.encodeJson()
    }
}

internal class LinkedProfileDetailsStateConverter {
    @TypeConverter
    fun fromLinkedProfileDetailsState(value: LinkedProfileDetailsState): String {
        return value.name
    }

    @TypeConverter
    fun toLinkedProfileDetailsState(value: String): LinkedProfileDetailsState {
        return LinkedProfileDetailsState.valueOf(value)
    }
}

internal class NetworkConverter {
    @TypeConverter
    fun fromNetwork(value: Network?): String? {
        return value?.value
    }

    @TypeConverter
    fun toNetwork(value: String?): Network? {
        return Network.withHost(value)
    }
}

internal class MutableMapJsonObjectConverter {
    @TypeConverter
    fun fromMutableMapJsonObject(value: MutableMap<String, JsonObject>?): String? {
        return value?.encodeJson()
    }

    @TypeConverter
    fun toMutableMapJsonObject(value: String?): MutableMap<String, JsonObject>? {
        return value?.decodeJson()
    }
}

@ProvidedTypeConverter
class EncryptStringConverter(
    private val keyStoreManager: KeyStoreManager,
) {
    @TypeConverter
    fun fromEncryptString(value: String?): ByteArray? {
        return value
            ?.toByteArray()
            ?.let { keyStoreManager.encryptData(it) }
    }

    @TypeConverter
    fun toEncryptString(value: ByteArray?): String? {
        return value
            ?.let { keyStoreManager.decryptData(it) }
            ?.let { String(it) }
    }
}

@ProvidedTypeConverter
class EncryptJsonObjectConverter(
    private val keyStoreManager: KeyStoreManager,
) {
    @TypeConverter
    fun fromJsonObject(jsonObject: JsonObject?): ByteArray? {
        return jsonObject
            ?.encodeJson()
            ?.let { keyStoreManager.encryptData(it.toByteArray()) }
    }

    @TypeConverter
    fun toJsonObject(value: ByteArray?): JsonObject? {
        return value
            ?.let { String(keyStoreManager.decryptData(it)) }
            ?.decodeJson()
    }
}
