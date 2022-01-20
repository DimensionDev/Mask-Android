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