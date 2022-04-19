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
package com.dimension.maskbook.persona.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val CurrentPersonaKey = stringPreferencesKey("current_persona")
private val ShouldShowContactsTipDialog = booleanPreferencesKey("ShouldShowContactsTipDialog")
private val IsMigratorIndexedDb = booleanPreferencesKey("IsMigratorIndexedDb")
val Context.personaDataStore: DataStore<Preferences> by preferencesDataStore(name = "persona")

class PreferenceRepository(
    private val dataStore: DataStore<Preferences>,
    private val appScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
) : IPreferenceRepository {

    override val data: Flow<Preferences>
        get() = dataStore.data

    override val currentPersonaIdentifier: Flow<String>
        get() = dataStore.data.map {
            it[CurrentPersonaKey] ?: ""
        }

    override suspend fun setCurrentPersonaIdentifier(identifier: String) {
        dataStore.edit {
            it[CurrentPersonaKey] = identifier
        }
    }

    override val shouldShowContactsTipDialog: Flow<Boolean>
        get() = dataStore.data.map {
            it[ShouldShowContactsTipDialog] ?: true
        }

    override fun setShowContactsTipDialog(bool: Boolean) {
        appScope.launch(dispatcher) {
            dataStore.edit {
                it[ShouldShowContactsTipDialog] = bool
            }
        }
    }

    override val isMigratorIndexedDb: Flow<Boolean>
        get() = dataStore.data.map {
            it[IsMigratorIndexedDb] ?: false
        }

    override fun setIsMigratorIndexedDb(bool: Boolean) {
        appScope.launch(dispatcher) {
            dataStore.edit {
                it[IsMigratorIndexedDb] = bool
            }
        }
    }

    private val _lastDetectProfileIdentifier = MutableStateFlow("")
    override val lastDetectProfileIdentifier: Flow<String> =
        _lastDetectProfileIdentifier.asStateFlow()

    override fun setLastDetectProfileIdentifier(identifier: String) {
        _lastDetectProfileIdentifier.value = identifier.replace("\\", "")
    }
}
