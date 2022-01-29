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
package com.dimension.maskbook.wallet.ui.scenes.app.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.NetworkType
import com.dimension.maskbook.wallet.repository.TradeProvider
import com.dimension.maskbook.wallet.ui.widget.CircleCheckboxDefaults
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.MaskSelection
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.viewmodel.app.MarketTrendSettingsViewModel
import org.koin.androidx.compose.getViewModel

private val tradeSources = mapOf(
    NetworkType.Ethereum to listOf(
        TradeProvider.UNISWAP_V2,
        TradeProvider.SUSHISWAP,
        TradeProvider.ZRX,
    ),
    NetworkType.Polygon to listOf(
        TradeProvider.QUICKSWAP,
    ),
    NetworkType.Binance to listOf(
        TradeProvider.PANCAKESWAP,
    )
)

private val NetworkType.text: String
    @Composable
    get() = when (this) {
        NetworkType.Ethereum -> stringResource(R.string.scene_app_swap_network_source_eth)
        NetworkType.Binance -> stringResource(R.string.scene_app_swap_network_source_bsc)
        NetworkType.Polygon -> stringResource(R.string.chain_name_polygon)
        NetworkType.Arbitrum -> TODO()
    }

private val TradeProvider.text: String
    get() = when (this) {
        TradeProvider.UNISWAP_V2 -> "Uniswap"
        TradeProvider.ZRX -> "0x"
        TradeProvider.SUSHISWAP -> "Sushiswap"
        TradeProvider.SASHIMISWAP -> "Sashimiswap"
        TradeProvider.BALANCER -> "Balancer"
        TradeProvider.QUICKSWAP -> "Quickswap"
        TradeProvider.PANCAKESWAP -> "Pancakeswap"
        TradeProvider.DODO -> "Dodo"
        TradeProvider.UNISWAP_V3 -> "Uniswap"
    }

private val TradeProvider.icon
    @Composable
    get() = when (this) {
        TradeProvider.UNISWAP_V2 -> painterResource(id = R.drawable.uniswap)
        TradeProvider.ZRX -> painterResource(id = R.drawable._x)
        TradeProvider.SUSHISWAP -> painterResource(id = R.drawable.sushiswap)
        TradeProvider.SASHIMISWAP -> TODO()
        TradeProvider.BALANCER -> TODO()
        TradeProvider.QUICKSWAP -> painterResource(id = R.drawable.ic_quickswap)
        TradeProvider.PANCAKESWAP -> painterResource(id = R.drawable.pancakeswap)
        TradeProvider.DODO -> TODO()
        TradeProvider.UNISWAP_V3 -> painterResource(id = R.drawable.uniswap)
    }

@Composable
fun MarketTrendSettingsModal() {
    val viewModel = getViewModel<MarketTrendSettingsViewModel>()
    val tradeProvider by viewModel.tradeProvider.observeAsState(initial = emptyMap())
    MaskModal {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(ScaffoldPadding)
                .animateContentSize(),
        ) {
            Text(
                text = "Default trading source",
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(21.dp))
            tradeSources.forEach { item ->
                Text(text = item.key.text, style = MaterialTheme.typography.subtitle1)
                item.value.forEach { provider ->
                    MaskSelection(
                        selected = tradeProvider[item.key] == provider,
                        onClicked = {
                            viewModel.setTradeProvider(item.key, provider)
                        },
                        checkboxColors = CircleCheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colors.primary,
                        ),
                        content = {
                            Image(
                                painter = provider.icon,
                                contentDescription = null,
                                Modifier.size(32.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(text = provider.text)
                        }
                    )
                }
            }
        }
    }
}
