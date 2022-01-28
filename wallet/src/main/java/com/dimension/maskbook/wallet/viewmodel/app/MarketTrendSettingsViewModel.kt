/*
 *  Mask-Android
 *
 *  Copyright (C) DimensionDev and Contributors
 * 
 *  This file is part of Mask-Android.
 * 
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Mask-Android. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.wallet.viewmodel.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import com.dimension.maskbook.wallet.repository.NetworkType
import com.dimension.maskbook.wallet.repository.TradeProvider

class MarketTrendSettingsViewModel(
    private val repository: ISettingsRepository
) : ViewModel() {
    val tradeProvider by lazy {
        repository.tradeProvider.asStateIn(viewModelScope, emptyMap())
    }
    fun setTradeProvider(networkType: NetworkType, provider: TradeProvider) {
        repository.setTradeProvider(networkType = networkType, tradeProvider = provider)
    }
}
