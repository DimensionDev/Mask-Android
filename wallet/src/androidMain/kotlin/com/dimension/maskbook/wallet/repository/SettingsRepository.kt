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

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Serializable
data class BackupPreview(
    val personas: Int,
    val accounts: Int,
    val posts: Int,
    val contacts: Int,
    val files: Int,
    val wallets: Int,
    val createdAt: Long,
)

enum class PlatformType {
    Twitter,
    Facebook
}

enum class Language(val value: String) {
    auto("auto"),
    enUS("en-US"),
    zhCN("zh-CN"),
    zhTW("zh-TW"),
    koKR("ko-KR"),
    jaJP("ja-JP"),
    esES("es-ES"),
    faIR("fa-IR"),
    itIT("it-IT"),
    ruRU("ru-RU"),
    frFR("fr-FR"),
}

enum class DataProvider(val value: Int) {
    COIN_GECKO(0), COIN_MARKET_CAP(1), UNISWAP_INFO(2)
}

enum class NetworkType {
    Ethereum,
    Binance,
    Polygon,
    Arbitrum,
}

enum class TradeProvider(val value: Int) {
    UNISWAP_V2(0),
    ZRX(1),
    SUSHISWAP(2),
    SASHIMISWAP(3),
    BALANCER(4),
    QUICKSWAP(5),
    PANCAKESWAP(6),
    DODO(7),
    UNISWAP_V3(8),
}

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

// class FakeSettingsRepository : ISettingsRepository {
//    private val _language = MutableStateFlow(Language.auto)
//    override val language = _language.asSharedFlow()
//    private val _appearance = MutableStateFlow(Appearance.default)
//    override val appearance = _appearance.asSharedFlow()
//    private val _dataProvider = MutableStateFlow(DataProvider.COIN_GECKO)
//    override val dataProvider = _dataProvider.asSharedFlow()
//    private val _paymentPassword = MutableStateFlow("")
//    override val paymentPassword = _paymentPassword.asSharedFlow()
//    private val _backupPassword = MutableStateFlow("")
//    override val backupPassword = _backupPassword.asSharedFlow()
//    override val tradeProvider: Flow<Map<NetworkType, TradeProvider>>
//        get() = TODO("Not yet implemented")
//
//    override fun setTradeProvider(networkType: NetworkType, tradeProvider: TradeProvider) {
//        TODO("Not yet implemented")
//    }
//
//    override fun setLanguage(language: Language) {
//        _language.value = language
//    }
//
//    override fun setAppearance(appearance: Appearance) {
//        _appearance.value = appearance
//    }
//
//    override fun setDataProvider(dataProvider: DataProvider) {
//        _dataProvider.value = dataProvider
//    }
//
//    override fun setPaymentPassword(value: String) {
//        _paymentPassword.value = value
//    }
//
//    override fun setBackupPassword(value: String) {
//        _backupPassword.value = value
//    }
//
//    override suspend fun provideBackupMeta(): BackupMeta? {
//        return BackupMeta(
//            account = "123",
//            personas = 0,
//            associatedAccount = 0,
//            encryptedPost = 0,
//            contacts = 0,
//            file = 0,
//            wallet = 0,
//            json = ""
//        )
//    }
//
//    override suspend fun provideBackupMetaFromJson(value: String): BackupMeta? {
//        return BackupMeta(
//            account = "123",
//            personas = 0,
//            associatedAccount = 0,
//            encryptedPost = 0,
//            contacts = 0,
//            file = 0,
//            wallet = 0,
//            json = ""
//        )
//    }
//
//    override suspend fun restoreBackupFromJson(value: String) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun createBackupJson(
//        noPosts: Boolean,
//        noWallets: Boolean,
//        noPersonas: Boolean,
//        noProfiles: Boolean,
//        hasPrivateKeyOnly: Boolean
//    ): String {
//        TODO("Not yet implemented")
//    }
//
//    override fun init() {
//
//    }
// }
