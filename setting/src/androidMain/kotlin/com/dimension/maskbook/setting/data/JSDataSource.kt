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
package com.dimension.maskbook.setting.data

import com.dimension.maskbook.setting.export.model.Appearance
import com.dimension.maskbook.setting.export.model.DataProvider
import com.dimension.maskbook.setting.export.model.Language
import com.dimension.maskbook.setting.export.model.NetworkType
import com.dimension.maskbook.setting.export.model.TradeProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal class JSDataSource(
    private val jsMethod: JSMethod,
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val _appearance = MutableStateFlow(Appearance.default)
    private val _dataProvider = MutableStateFlow(DataProvider.COIN_GECKO)
    private val _language = MutableStateFlow(Language.auto)
    private val _tradeProvider = MutableStateFlow(
        mapOf(
            NetworkType.Ethereum to TradeProvider.UNISWAP_V3,
            NetworkType.Polygon to TradeProvider.QUICKSWAP,
            NetworkType.Binance to TradeProvider.PANCAKESWAP,
        )
    )

    val language = _language.asSharedFlow()
    val appearance = _appearance.asSharedFlow()
    val dataProvider = _dataProvider.asSharedFlow()
    val tradeProvider: Flow<Map<NetworkType, TradeProvider>>
        get() = _tradeProvider.asSharedFlow()

    fun setLanguage(language: Language) {
        scope.launch {
            jsMethod.setLanguage(language)
            _language.value = jsMethod.getLanguage()
        }
    }

    fun setAppearance(appearance: Appearance) {
        scope.launch {
            jsMethod.setTheme(appearance)
            _appearance.value = jsMethod.getTheme()
        }
    }

    fun setDataProvider(dataProvider: DataProvider) {
        scope.launch {
            jsMethod.setTrendingDataSource(dataProvider)
            _dataProvider.value = jsMethod.getTrendingDataSource()
        }
    }

    fun setTradeProvider(networkType: NetworkType, tradeProvider: TradeProvider) {
        scope.launch {
            jsMethod.setNetworkTraderProvider(networkType, tradeProvider)
            updateTradeProvider()
        }
    }

    private suspend fun updateTradeProvider() {
        _tradeProvider.value = mapOf(
            NetworkType.Ethereum to jsMethod.getNetworkTraderProvider(NetworkType.Ethereum),
            NetworkType.Polygon to jsMethod.getNetworkTraderProvider(NetworkType.Polygon),
            NetworkType.Binance to jsMethod.getNetworkTraderProvider(NetworkType.Binance),
        )
    }

    fun initData() {
        scope.launch {
            awaitAll(
                async { _language.value = jsMethod.getLanguage() },
                async { _appearance.value = jsMethod.getTheme() },
                async { _dataProvider.value = jsMethod.getTrendingDataSource() },
                async { updateTradeProvider() }
            )
        }
    }
}
