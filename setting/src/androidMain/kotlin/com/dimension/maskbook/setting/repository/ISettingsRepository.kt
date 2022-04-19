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
package com.dimension.maskbook.setting.repository

import com.dimension.maskbook.setting.export.model.Appearance
import com.dimension.maskbook.setting.export.model.BackupMeta
import com.dimension.maskbook.setting.export.model.BackupMetaFile
import com.dimension.maskbook.setting.export.model.DataProvider
import com.dimension.maskbook.setting.export.model.Language
import kotlinx.coroutines.flow.Flow

interface ISettingsRepository {
    val biometricEnabled: Flow<Boolean>
    val language: Flow<Language>
    val appearance: Flow<Appearance>
    val dataProvider: Flow<DataProvider>
    val paymentPassword: Flow<String>
    val backupPassword: Flow<String>
    val shouldShowLegalScene: Flow<Boolean>
    val email: Flow<String>
    val phone: Flow<String>
    suspend fun setBiometricEnabled(value: Boolean)
    suspend fun setLanguage(language: Language)
    suspend fun setAppearance(appearance: Appearance)
    suspend fun setDataProvider(dataProvider: DataProvider)
    suspend fun setPaymentPassword(value: String)
    suspend fun setBackupPassword(value: String)
    suspend fun generateBackupMeta(): BackupMeta
    suspend fun provideBackupMeta(file: BackupMetaFile): BackupMeta
    suspend fun restoreBackup(value: BackupMetaFile)
    suspend fun createBackup(
        noPosts: Boolean = false,
        noWallets: Boolean = false,
        noPersonas: Boolean = false,
        noProfiles: Boolean = false,
        noRelations: Boolean = false,
        hasPrivateKeyOnly: Boolean = false,
    ): BackupMetaFile

    suspend fun setShouldShowLegalScene(value: Boolean)
    suspend fun saveEmail(value: String)
    suspend fun savePhone(value: String)
}
