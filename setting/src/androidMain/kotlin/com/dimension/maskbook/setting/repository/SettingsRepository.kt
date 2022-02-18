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

import com.dimension.maskbook.common.repository.JSMethod
import com.dimension.maskbook.persona.export.PersonaServices
import com.dimension.maskbook.setting.data.JSDataSource
import com.dimension.maskbook.setting.data.SettingDataSource
import com.dimension.maskbook.setting.export.model.Appearance
import com.dimension.maskbook.setting.export.model.BackupMeta
import com.dimension.maskbook.setting.export.model.DataProvider
import com.dimension.maskbook.setting.export.model.Language
import com.dimension.maskbook.setting.export.model.NetworkType
import com.dimension.maskbook.setting.export.model.TradeProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SettingsRepository(
    private val personaServices: PersonaServices,
    private val settingDataSource: SettingDataSource,
    private val jsDataSource: JSDataSource,
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
    override val tradeProvider: Flow<Map<NetworkType, TradeProvider>>
        get() = jsDataSource.tradeProvider
    override val paymentPassword: Flow<String>
        get() = settingDataSource.paymentPassword
    override val backupPassword: Flow<String>
        get() = settingDataSource.backupPassword
    override val shouldShowLegalScene: Flow<Boolean>
        get() = settingDataSource.shouldShowLegalScene

    override fun setBiometricEnabled(value: Boolean) {
        settingDataSource.setBiometricEnabled(value)
    }

    override fun setTradeProvider(networkType: NetworkType, tradeProvider: TradeProvider) {
        jsDataSource.setTradeProvider(networkType, tradeProvider)
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

    override suspend fun provideBackupMeta(): BackupMeta? {
        return JSMethod.Setting.createBackupJson().let { json ->
            JSMethod.Setting.getBackupPreviewInfo(json)?.let {
                BackupMeta(
                    personas = it.personas,
                    associatedAccount = it.accounts,
                    encryptedPost = it.posts,
                    contacts = it.contacts,
                    file = it.files,
                    wallet = it.wallets,
                    json = json,
                    account = "",
                )
            }
        }
    }

    override suspend fun provideBackupMetaFromJson(value: String): BackupMeta? {
        return JSMethod.Setting.getBackupPreviewInfo(value)?.let {
            BackupMeta(
                personas = it.personas,
                associatedAccount = it.accounts,
                encryptedPost = it.posts,
                contacts = it.contacts,
                file = it.files,
                wallet = it.wallets,
                json = value,
                account = "",
            )
        }
    }

    override suspend fun restoreBackupFromJson(value: String) {
        JSMethod.Setting.restoreBackup(value)
        jsDataSource.initData()
    }

    override suspend fun createBackupJson(
        noPosts: Boolean,
        noWallets: Boolean,
        noPersonas: Boolean,
        noProfiles: Boolean,
        hasPrivateKeyOnly: Boolean
    ): String {
        return JSMethod.Setting.createBackupJson(
            noPosts, noWallets, noPersonas, noProfiles, hasPrivateKeyOnly
        )
    }

    override fun saveEmailForCurrentPersona(value: String) {
        scope.launch {
            personaServices.currentPersona.firstOrNull()?.let {
                settingDataSource.saveEmailForPersona(it, value)
            }
        }
    }

    override fun savePhoneForCurrentPersona(value: String) {
        scope.launch {
            personaServices.currentPersona.firstOrNull()?.let {
                settingDataSource.savePhoneForPersona(it, value)
            }
        }
    }
}
