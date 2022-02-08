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
import com.dimension.maskbook.wallet.repository.NetworkType
import com.dimension.maskbook.wallet.repository.TradeProvider
import kotlinx.coroutines.flow.Flow

class SettingServicesImpl : SettingServices {
    override val biometricEnabled: Flow<Boolean>
        get() = TODO("Not yet implemented")
    override val appearance: Flow<Appearance>
        get() = TODO("Not yet implemented")
    override val paymentPassword: Flow<String>
        get() = TODO("Not yet implemented")
    override val backupPassword: Flow<String>
        get() = TODO("Not yet implemented")
    override val tradeProvider: Flow<Map<NetworkType, TradeProvider>>
        get() = TODO("Not yet implemented")
    override val shouldShowLegalScene: Flow<Boolean>
        get() = TODO("Not yet implemented")

    override fun setBiometricEnabled(value: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setTradeProvider(networkType: NetworkType, tradeProvider: TradeProvider) {
        TODO("Not yet implemented")
    }

    override fun setPaymentPassword(value: String) {
        TODO("Not yet implemented")
    }

    override fun setShouldShowLegalScene(value: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun restoreBackupFromJson(value: String) {
        TODO("Not yet implemented")
    }

    override suspend fun provideBackupMetaFromJson(value: String): BackupMeta? {
        TODO("Not yet implemented")
    }
}
