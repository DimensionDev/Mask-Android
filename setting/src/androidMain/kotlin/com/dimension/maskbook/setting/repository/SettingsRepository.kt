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

import com.dimension.maskbook.common.ext.decodeJson
import com.dimension.maskbook.common.ext.encodeJson
import com.dimension.maskbook.labs.export.model.AppKey
import com.dimension.maskbook.persona.export.PersonaServices
import com.dimension.maskbook.setting.data.JSDataSource
import com.dimension.maskbook.setting.data.JSMethod
import com.dimension.maskbook.setting.data.SettingDataSource
import com.dimension.maskbook.setting.export.model.Appearance
import com.dimension.maskbook.setting.export.model.BackupJSONFile
import com.dimension.maskbook.setting.export.model.BackupMeta
import com.dimension.maskbook.setting.export.model.DataProvider
import com.dimension.maskbook.setting.export.model.Language
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

internal class SettingsRepository(
    private val personaServices: PersonaServices,
    private val settingDataSource: SettingDataSource,
    private val jsDataSource: JSDataSource,
    private val jsMethod: JSMethod,
) : ISettingsRepository {
    private val scope = CoroutineScope(Dispatchers.IO)
    override val biometricEnabled: Flow<Boolean>
        get() = settingDataSource.biometricEnabled
    override val language: Flow<Language>
        get() = jsDataSource.language
    override val appearance: Flow<Appearance>
        get() = jsDataSource.appearance
    override val dataProvider: Flow<DataProvider>
        get() = jsDataSource.dataProvider
    override val paymentPassword: Flow<String>
        get() = settingDataSource.paymentPassword
    override val backupPassword: Flow<String>
        get() = settingDataSource.backupPassword
    override val shouldShowLegalScene: Flow<Boolean>
        get() = settingDataSource.shouldShowLegalScene

    override fun setBiometricEnabled(value: Boolean) {
        settingDataSource.setBiometricEnabled(value)
    }

    override fun setLanguage(language: Language) {
        jsDataSource.setLanguage(language)
    }

    override fun setAppearance(appearance: Appearance) {
        jsDataSource.setAppearance(appearance)
    }

    override fun setDataProvider(dataProvider: DataProvider) {
        jsDataSource.setDataProvider(dataProvider)
    }

    override fun setPaymentPassword(value: String) {
        settingDataSource.setPaymentPassword(value)
    }

    override fun setBackupPassword(value: String) {
        settingDataSource.setBackupPassword(value)
    }

    override fun setShouldShowLegalScene(value: Boolean) {
        settingDataSource.setShouldShowLegalScene(value)
    }

    override suspend fun provideBackupMeta(): BackupMeta {
        return createBackupJson(
            noPosts = false,
            noWallets = false,
            noPersonas = false,
            noProfiles = false,
            hasPrivateKeyOnly = false,
        ).let {
            provideBackupMetaFromJson(it)
        }
    }

    override suspend fun provideBackupMetaFromJson(value: String): BackupMeta {
        return value.decodeJson<BackupJSONFile>().let { json ->
            BackupMeta(
                personas = json.personas.size,
                associatedAccount = json.personas.sumOf { it.linkedProfiles.size },
                encryptedPost = json.posts.size,
                contacts = json.profiles.size,
                file = json.plugin?.count { it.key == AppKey.FileService.id } ?: 0,
                wallet = json.wallets.size,
                json = value,
                account = ""
            )
        }
    }

    override suspend fun restoreBackupFromJson(value: String) {
        val file = value.decodeJson<BackupJSONFile>()
        jsMethod.restoreBackup(value)
        jsDataSource.initData()
        // personaServices.refreshPersonaData()
    }

    override suspend fun createBackupJson(
        noPosts: Boolean,
        noWallets: Boolean,
        noPersonas: Boolean,
        noProfiles: Boolean,
        noRelations: Boolean,
        hasPrivateKeyOnly: Boolean
    ): String {
        val personas = if (noPersonas) {
            emptyList()
        } else {
            backupPersona(hasPrivateKeyOnly)
        }
        val profile = if (noProfiles) {
            emptyList()
        } else {
            backProfiles()
        }
        val wallets = if (noWallets) {
            emptyList()
        } else {
            backupWallets()
        }
        val posts = if (noPosts) {
            emptyList()
        } else {
            backupPosts()
        }
        val relations = if (noRelations) {
            emptyList()
        } else {
            backupRelations()
        }
        return BackupJSONFile(
            personas = personas,
            wallets = wallets,
            posts = posts,
            profiles = profile,
            meta = BackupJSONFile.Meta.Default,
            grantedHostPermissions = emptyList(),
            relations = relations,
        ).encodeJson()
    }

    private fun backupRelations(): List<BackupJSONFile.Relation> {
        TODO("Not yet implemented")
    }

    private fun backupPosts(): List<BackupJSONFile.Post> {
        TODO("Not yet implemented")
    }

    private fun backupWallets(): List<BackupJSONFile.Wallet> {
        TODO("Not yet implemented")
    }

    private fun backProfiles(): List<BackupJSONFile.Profile> {
        TODO("Not yet implemented")
    }

    private fun backupPersona(hasPrivateKeyOnly: Boolean): List<BackupJSONFile.Persona> {
        TODO("Not yet implemented")
    }

    override fun saveEmailForCurrentPersona(value: String) {
        personaServices.saveEmailForCurrentPersona(value)
    }

    override fun savePhoneForCurrentPersona(value: String) {
        personaServices.savePhoneForCurrentPersona(value)
    }
}
