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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

private val PaymentPasswordKey = stringPreferencesKey("payment_password")
private val BackupPasswordKey = stringPreferencesKey("backup_password")
private val BiometricEnabledKey = booleanPreferencesKey("biometric_enabled")
private val ShouldShowLegalSceneKey = booleanPreferencesKey("ShowLegalSceneKey")
private val RegisterEmail = stringPreferencesKey("RegisterEmail")
private val RegisterPhone = stringPreferencesKey("RegisterPhone")
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingDataSource(
    private val dataStore: DataStore<Preferences>,
    private val preferenceCoroutineContext: CoroutineContext,
) {
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

    val email: Flow<String> = dataStore.data.map {
        it[RegisterEmail] ?: ""
    }

    val phone: Flow<String> = dataStore.data.map {
        it[RegisterPhone] ?: ""
    }

    suspend fun setPaymentPassword(value: String) {
        withContext(preferenceCoroutineContext) {
            dataStore.edit {
                it[PaymentPasswordKey] = value
            }
        }
    }

    suspend fun setBackupPassword(value: String) {
        withContext(preferenceCoroutineContext) {
            dataStore.edit {
                it[BackupPasswordKey] = value
            }
        }
    }

    suspend fun setShouldShowLegalScene(value: Boolean) {
        withContext(preferenceCoroutineContext) {
            dataStore.edit {
                it[ShouldShowLegalSceneKey] = value
            }
        }
    }

    suspend fun setBiometricEnabled(value: Boolean) {
        withContext(preferenceCoroutineContext) {
            dataStore.edit {
                it[BiometricEnabledKey] = value
            }
        }
    }

    suspend fun setRegisterEmail(value: String) {
        withContext(preferenceCoroutineContext) {
            dataStore.edit {
                it[RegisterEmail] = value
            }
        }
    }

    suspend fun setRegisterPhone(value: String) {
        withContext(preferenceCoroutineContext) {
            dataStore.edit {
                it[RegisterPhone] = value
            }
        }
    }
}
