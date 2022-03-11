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
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.dimension.maskbook.common.ext.decodeJson
import com.dimension.maskbook.common.ext.encodeJson
import com.dimension.maskbook.persona.db.dao.PersonaDao
import com.dimension.maskbook.persona.db.model.DbPersonaRecord
import com.dimension.maskbook.persona.db.model.DbPostRecord
import com.dimension.maskbook.persona.db.model.DbProfileRecord
import com.dimension.maskbook.persona.db.model.DbRelationRecord
import kotlinx.serialization.json.JsonObject

@Database(
    entities = [
        DbPersonaRecord::class,
        DbPostRecord::class,
        DbProfileRecord::class,
        DbRelationRecord::class,
    ],
    version = 1,
)
@TypeConverters(JsonObjectConverter::class)
abstract class PersonaDatabase : RoomDatabase() {
    abstract fun personaDao(): PersonaDao
}

internal class JsonObjectConverter {

    @TypeConverter
    fun fromString(value: String?): JsonObject {
        return value?.decodeJson() ?: JsonObject(emptyMap())
    }

    @TypeConverter
    fun fromJsonObject(jsonObject: JsonObject): String {
        return jsonObject.encodeJson()
    }
}
