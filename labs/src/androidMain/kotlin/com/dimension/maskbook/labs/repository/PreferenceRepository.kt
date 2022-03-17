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
package com.dimension.maskbook.labs.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

private val ShouldShowPluginSettingsTipDialog = booleanPreferencesKey("ShouldShowPluginSettingsTipDialog")
private val Context.labsDataStore: DataStore<Preferences> by preferencesDataStore(name = "labs")

@Single(binds = [IPreferenceRepository::class])
class PreferenceRepository(
    context: Context,
) : IPreferenceRepository {
    private val dataStore: DataStore<Preferences> = context.labsDataStore
    private val scope = CoroutineScope(Dispatchers.IO)

    override val shouldShowPluginSettingsTipDialog: Flow<Boolean>
        get() = dataStore.data.map {
            it[ShouldShowPluginSettingsTipDialog] ?: true
        }

    override fun setShowPluginSettingsTipDialog(bool: Boolean) {
        scope.launch {
            dataStore.edit {
                it[ShouldShowPluginSettingsTipDialog] = bool
            }
        }
    }
}
