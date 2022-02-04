package com.dimension.maskbook.wallet.repository

import com.dimension.maskbook.repository.DataProvider
import com.dimension.maskbook.repository.Language
import com.dimension.maskbook.repository.NetworkType
import com.dimension.maskbook.repository.TradeProvider
import com.dimension.maskbook.wallet.ui.widget.BackupMeta
import kotlinx.coroutines.flow.Flow

interface ISettingsRepository {
    val biometricEnabled: Flow<Boolean>
    val language: Flow<Language>
    val appearance: Flow<Appearance>
    val dataProvider: Flow<DataProvider>
    val paymentPassword: Flow<String>
    val backupPassword: Flow<String>
    val tradeProvider: Flow<Map<NetworkType, TradeProvider>>
    val shouldShowLegalScene: Flow<Boolean>
    fun setBiometricEnabled(value: Boolean)
    fun setTradeProvider(networkType: NetworkType, tradeProvider: TradeProvider)
    fun setLanguage(language: Language)
    fun setAppearance(appearance: Appearance)
    fun setDataProvider(dataProvider: DataProvider)
    fun setPaymentPassword(value: String)
    fun setBackupPassword(value: String)
    suspend fun provideBackupMeta(): BackupMeta?
    suspend fun provideBackupMetaFromJson(value: String): BackupMeta?
    suspend fun restoreBackupFromJson(value: String)
    suspend fun createBackupJson(
        noPosts: Boolean = false,
        noWallets: Boolean = false,
        noPersonas: Boolean = false,
        noProfiles: Boolean = false,
        hasPrivateKeyOnly: Boolean = false,
    ): String
    fun init()
    fun setShouldShowLegalScene(value: Boolean)
}