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
package com.dimension.maskbook.persona.db.base

import android.content.Context
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import com.dimension.maskbook.common.manager.KeyStoreManager
import com.dimension.maskbook.persona.db.EncryptJsonObjectConverter
import com.dimension.maskbook.persona.db.EncryptStringConverter
import com.dimension.maskbook.persona.db.PersonaDatabase

abstract class PersonaDatabaseTest : BaseDaoTest<PersonaDatabase>() {

    protected lateinit var keyStoreManager: KeyStoreManager

    override fun onCreateDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        keyStoreManager = KeyStoreManager(context)

        super.onCreateDb()
    }

    override fun RoomDatabase.Builder<PersonaDatabase>.onDatabaseBuilder(): RoomDatabase.Builder<PersonaDatabase> {
        return addTypeConverter(EncryptStringConverter(keyStoreManager))
            .addTypeConverter(EncryptJsonObjectConverter(keyStoreManager))
    }

    override fun getDBClass() = PersonaDatabase::class.java
}
