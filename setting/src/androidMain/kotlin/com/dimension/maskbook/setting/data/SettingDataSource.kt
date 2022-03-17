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
package com.dimension.maskbook.setting.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

private val PaymentPasswordKey = stringPreferencesKey("payment_password")
private val BackupPasswordKey = stringPreferencesKey("backup_password")
private val BiometricEnabledKey = booleanPreferencesKey("biometric_enabled")
private val ShouldShowLegalSceneKey = booleanPreferencesKey("ShowLegalSceneKey")
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Single
class SettingDataSource(
    context: Context,
) {
    private val dataStore: DataStore<Preferences> = context.settingsDataStore
    private val scope = CoroutineScope(Dispatchers.IO)
    val biometricEnabled: Flow<Boolean>
        get() = dataStore.data.map {
            it[BiometricEnabledKey] ?: false
        }

    val paymentPassword = dataStore.data.map {
        it[PaymentPasswordKey] ?: ""
    }

    val backupPassword = dataStore.data.map {
        it[BackupPasswordKey] ?: ""
    }

    val shouldShowLegalScene: Flow<Boolean>
        get() = dataStore.data.map { it[ShouldShowLegalSceneKey] ?: true }

    fun setPaymentPassword(value: String) {
        scope.launch {
            dataStore.edit {
                it[PaymentPasswordKey] = value
            }
        }
    }

    fun setBackupPassword(value: String) {
        scope.launch {
            dataStore.edit {
                it[BackupPasswordKey] = value
            }
        }
    }

    fun setShouldShowLegalScene(value: Boolean) {
        scope.launch {
            dataStore.edit {
                it[ShouldShowLegalSceneKey] = value
            }
        }
    }

    fun setBiometricEnabled(value: Boolean) {
        scope.launch {
            dataStore.edit {
                it[BiometricEnabledKey] = value
            }
        }
    }
}
