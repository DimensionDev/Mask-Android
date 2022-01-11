package com.dimension.maskbook.wallet.viewmodel.app

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.AppKey
import com.dimension.maskbook.wallet.repository.IAppRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map


data class PluginDisplayData(
    val key: AppKey,
    @DrawableRes val onIcon: Int,
    val name: String,
    val canExplore: Boolean = false,
    val settingRoute: String? = null,
    val enabled: Boolean = true,
)

private val displayDataList = listOf(
    PluginDisplayData(
        key = AppKey.FileService,
        name = "File Service",
        onIcon = R.drawable.ic_labs_encrypted_file,
        canExplore = true,
    ),
    PluginDisplayData(
        key = AppKey.ITO,
        name = "ITO",
        onIcon = R.drawable.ic_labs_ito,
        canExplore = true,
    ),
    PluginDisplayData(
        key = AppKey.LuckDrop,
        name = "Lucky Drop",
        onIcon = R.drawable.ic_labs_packet,
        canExplore = true,
    ),
    PluginDisplayData(
        key = AppKey.Transak,
        name = "Transak",
        onIcon = R.drawable.ic_labs_transak,
        canExplore = true,
    ),
    PluginDisplayData(
        key = AppKey.Snapshot,
        name = "Snapshot",
        onIcon = R.drawable.ic_labs_snapshot,
    ),
    PluginDisplayData(
        key = AppKey.DHEDGE,
        name = "DHEDGE",
        onIcon = R.drawable.ic_labs_dhedge,
    ),
    PluginDisplayData(
        key = AppKey.GitCoin,
        name = "Gitcoin",
        onIcon = R.drawable.ic_labs_gitcoin,
    ),
    PluginDisplayData(
        key = AppKey.MarketTrend,
        name = "Market Trend",
        onIcon = R.drawable.ic_labs_market_trend,
        settingRoute = "MarketTrendSettings"
    ),
    PluginDisplayData(
        key = AppKey.Collectibles,
        name = "Collectibles",
        onIcon = R.drawable.ic_labs_collectibles,
    ),
    PluginDisplayData(
        key = AppKey.Valuables,
        name = "Valuables",
        onIcon = R.drawable.ic_labs_union,
    ),
)

class PluginSettingsViewModel(
    private val repository: IAppRepository,
    private val walletRepository: IWalletRepository,
) : ViewModel() {

    val apps by lazy {
        repository.apps
            .map { apps ->
                displayDataList.map { item ->
                    item.copy(
                        enabled = apps.find { it.key === item.key }?.enabled ?: true
                    )
                }
            }
            .flowOn(Dispatchers.IO)
            .asStateIn(viewModelScope, emptyList())
    }

    val wallet by lazy {
        walletRepository.currentWallet.asStateIn(viewModelScope, null)
    }

    fun setEnabled(key: AppKey, enabled: Boolean) {
        repository.setEnabled(key, enabled)
    }
}
