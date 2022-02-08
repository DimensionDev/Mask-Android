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
package com.dimension.maskbook.setting

import com.dimension.maskbook.setting.export.SettingServices
import com.dimension.maskbook.setting.export.model.BackupMeta
import com.dimension.maskbook.wallet.repository.Appearance
import com.dimension.maskbook.wallet.repository.BackupRepository
import com.dimension.maskbook.wallet.repository.NetworkType
import com.dimension.maskbook.wallet.repository.SettingsRepository
import com.dimension.maskbook.wallet.repository.TradeProvider
import kotlinx.coroutines.flow.Flow

class SettingServicesImpl(
    private val settingsRepository: SettingsRepository,
    private val backupRepository: BackupRepository,
) : SettingServices {

    override val biometricEnabled: Flow<Boolean>
        get() = settingsRepository.biometricEnabled

    override val appearance: Flow<Appearance>
        get() = settingsRepository.appearance

    override val paymentPassword: Flow<String>
        get() = settingsRepository.paymentPassword
    override val backupPassword: Flow<String>
        get() = settingsRepository.backupPassword

    override val tradeProvider: Flow<Map<NetworkType, TradeProvider>>
        get() = settingsRepository.tradeProvider

    override val shouldShowLegalScene: Flow<Boolean>
        get() = settingsRepository.shouldShowLegalScene

    override fun setBiometricEnabled(value: Boolean) {
        settingsRepository.setBiometricEnabled(value)
    }

    override fun setTradeProvider(networkType: NetworkType, tradeProvider: TradeProvider) {
        settingsRepository.setTradeProvider(networkType, tradeProvider)
    }

    override fun setPaymentPassword(value: String) {
        settingsRepository.setPaymentPassword(value)
    }

    override fun setShouldShowLegalScene(value: Boolean) {
        settingsRepository.setShouldShowLegalScene(value)
    }

    override suspend fun restoreBackupFromJson(value: String) {
        settingsRepository.restoreBackupFromJson(value)
    }

    override suspend fun provideBackupMetaFromJson(value: String): BackupMeta? {
        return settingsRepository.provideBackupMetaFromJson(value)
    }

    override suspend fun downloadBackupWithPhone(phone: String, code: String): String {
        return backupRepository.downloadBackupWithPhone(phone, code).toString()
    }

    override suspend fun downloadBackupWithEmail(email: String, code: String): String {
        return backupRepository.downloadBackupWithEmail(email, code).toString()
    }

    override suspend fun validatePhoneCode(phone: String, code: String) {
        backupRepository.validatePhoneCode(phone, code)
    }

    override suspend fun validateEmailCode(email: String, code: String) {
        backupRepository.validateEmailCode(email, code)
    }

    override suspend fun sendPhoneCode(phone: String) {
        backupRepository.sendPhoneCode(phone)
    }

    override suspend fun sendEmailCode(email: String) {
        backupRepository.sendEmailCode(email)
    }
}
