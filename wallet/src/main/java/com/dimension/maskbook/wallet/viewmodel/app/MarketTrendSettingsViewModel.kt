package com.dimension.maskbook.wallet.viewmodel.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import com.dimension.maskbook.wallet.repository.NetworkType
import com.dimension.maskbook.wallet.repository.TradeProvider

class MarketTrendSettingsViewModel(
    private val repository: ISettingsRepository
): ViewModel() {
    val tradeProvider by lazy {
        repository.tradeProvider.asStateIn(viewModelScope, emptyMap())
    }
    fun setTradeProvider(networkType: NetworkType, provider: TradeProvider) {
        repository.setTradeProvider(networkType = networkType, tradeProvider = provider)
    }
}