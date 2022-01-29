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
package com.dimension.maskbook.wallet.ui.scenes.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.DataProvider
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.MaskSelection
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.viewmodel.settings.DataSourceSettingsViewModel
import org.koin.androidx.compose.getViewModel

val dataProviderMap = mapOf(
    DataProvider.COIN_GECKO to "CoinGecko",
    DataProvider.COIN_MARKET_CAP to "CoinMarketCap",
    DataProvider.UNISWAP_INFO to "Uniswap Info",
)

@Composable
fun DataSourceSettings(
    onBack: () -> Unit,
) {
    val viewModel: DataSourceSettingsViewModel = getViewModel()
    val dataProvider by viewModel.dataProvider.observeAsState(initial = DataProvider.COIN_GECKO)
    MaskModal {
        Column(
            modifier = Modifier.padding(ScaffoldPadding)
        ) {
            dataProviderMap.forEach {
                MaskSelection(
                    selected = it.key == dataProvider,
                    onClicked = {
                        viewModel.setDataProvider(it.key)
                        onBack.invoke()
                    },
                    content = {
                        Text(text = it.value)
                    }
                )
            }
        }
    }
}
