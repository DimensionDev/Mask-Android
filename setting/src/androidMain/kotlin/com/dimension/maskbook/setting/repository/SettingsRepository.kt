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
package com.dimension.maskbook.wallet.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dimension.maskbook.persona.export.PersonaServices
import com.dimension.maskbook.repository.JSMethod
import com.dimension.maskbook.wallet.ui.widget.BackupMeta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val PaymentPasswordKey = stringPreferencesKey("payment_password")
private val BackupPasswordKey = stringPreferencesKey("backup_password")
private val BiometricEnabledKey = booleanPreferencesKey("biometric_enabled")
private val ShouldShowLegalSceneKey = booleanPreferencesKey("ShowLegalSceneKey")
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(
    private val dataStore: DataStore<Preferences>,
    private val personaServices: PersonaServices,
) : ISettingsRepository {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val _language = MutableStateFlow(Language.auto)
    override val biometricEnabled: Flow<Boolean>
        get() = dataStore.data.map {
            it[BiometricEnabledKey] ?: false
        }
    override val language = _language.asSharedFlow()
    private val _appearance = MutableStateFlow(Appearance.default)
    override val appearance = _appearance.asSharedFlow()
    private val _dataProvider = MutableStateFlow(DataProvider.COIN_GECKO)
    override val dataProvider = _dataProvider.asSharedFlow()
    override val paymentPassword = dataStore.data.map {
        it[PaymentPasswordKey] ?: ""
    }
    override val backupPassword = dataStore.data.map {
        it[BackupPasswordKey] ?: ""
    }
    private val _tradeProvider = MutableStateFlow(
        mapOf(
            NetworkType.Ethereum to TradeProvider.UNISWAP_V3,
            NetworkType.Polygon to TradeProvider.QUICKSWAP,
            NetworkType.Binance to TradeProvider.PANCAKESWAP,
        )
    )
    override val tradeProvider: Flow<Map<NetworkType, TradeProvider>>
        get() = _tradeProvider.asSharedFlow()

    override val shouldShowLegalScene: Flow<Boolean>
        get() = dataStore.data.map { it[ShouldShowLegalSceneKey] ?: true }

    override fun setBiometricEnabled(value: Boolean) {
        scope.launch {
            dataStore.edit {
                it[BiometricEnabledKey] = value
            }
        }
    }

    override fun setTradeProvider(networkType: NetworkType, tradeProvider: TradeProvider) {
        scope.launch {
            JSMethod.Setting.setNetworkTraderProvider(networkType, tradeProvider)
            updateTradeProvider()
        }
    }

    override fun setLanguage(language: Language) {
        scope.launch {
            JSMethod.Setting.setLanguage(language)
            _language.value = JSMethod.Setting.getLanguage()
        }
    }

    override fun setAppearance(appearance: Appearance) {
        scope.launch {
            JSMethod.Setting.setTheme(appearance)
            _appearance.value = JSMethod.Setting.getTheme()
        }
    }

    override fun setDataProvider(dataProvider: DataProvider) {
        scope.launch {
            JSMethod.Setting.setTrendingDataSource(dataProvider)
            _dataProvider.value = JSMethod.Setting.getTrendingDataSource()
        }
    }

    override fun setPaymentPassword(value: String) {
        scope.launch {
            dataStore.edit {
                it[PaymentPasswordKey] = value
            }
        }
    }

    override fun setBackupPassword(value: String) {
        scope.launch {
            dataStore.edit {
                it[BackupPasswordKey] = value
            }
        }
    }

    override fun setShouldShowLegalScene(value: Boolean) {
        scope.launch {
            dataStore.edit {
                it[ShouldShowLegalSceneKey] = value
            }
        }
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
        init()
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

    override fun init() {
        scope.launch {
            awaitAll(
                async { _language.value = JSMethod.Setting.getLanguage() },
                async { _appearance.value = JSMethod.Setting.getTheme() },
                async { _dataProvider.value = JSMethod.Setting.getTrendingDataSource() },
                async { updateTradeProvider() }
            )
        }
    }

    private suspend fun updateTradeProvider() {
        _tradeProvider.value = mapOf(
            NetworkType.Ethereum to JSMethod.Setting.getNetworkTraderProvider(NetworkType.Ethereum),
            NetworkType.Polygon to JSMethod.Setting.getNetworkTraderProvider(NetworkType.Polygon),
            NetworkType.Binance to JSMethod.Setting.getNetworkTraderProvider(NetworkType.Binance),
        )
    }

    override fun saveEmailForCurrentPersona(value: String) {
        scope.launch {
            personaServices.currentPersona.firstOrNull()?.let {
                val emailKey = stringPreferencesKey("${it.id}_email")
                dataStore.edit {
                    it[emailKey] = value
                }
            }
        }
    }

    override fun savePhoneForCurrentPersona(value: String) {
        scope.launch {
            personaServices.currentPersona.firstOrNull()?.let {
                val phoneKey = stringPreferencesKey("${it.id}_phone")
                dataStore.edit {
                    it[phoneKey] = value
                }
            }
        }
    }
}
