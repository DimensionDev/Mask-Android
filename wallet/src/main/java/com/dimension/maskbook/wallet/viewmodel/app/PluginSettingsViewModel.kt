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
    val enabled: Boolean = true,
)

private val displayDataList = listOf(
    PluginDisplayData(
        key = AppKey.FileService,
        name = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_app_plugins_file_service),
        onIcon = R.drawable.ic_labs_encrypted_file,
    ),
    PluginDisplayData(
        key = AppKey.ITO,
        name = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_app_plugins_ito),
        onIcon = R.drawable.ic_labs_ito,
    ),
    PluginDisplayData(
        key = AppKey.LuckDrop,
        name = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_app_plugins_lucy_drop),
        onIcon = R.drawable.ic_labs_packet,
    ),
    PluginDisplayData(
        key = AppKey.Transak,
        name = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_app_plugins_transaction),
        onIcon = R.drawable.ic_labs_transak,
    ),
    PluginDisplayData(
        key = AppKey.Snapshot,
        name = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_app_plugins_snapshot),
        onIcon = R.drawable.ic_labs_snapshot,
    ),
    PluginDisplayData(
        key = AppKey.DHEDGE,
        name = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_app_plugins_dhedge),
        onIcon = R.drawable.ic_labs_dhedge,
    ),
    PluginDisplayData(
        key = AppKey.GitCoin,
        name = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_app_plugins_gitcoin),
        onIcon = R.drawable.ic_labs_gitcoin,
    ),
    PluginDisplayData(
        key = AppKey.Swap,
        name = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_app_plugins_swap),
        onIcon = R.drawable.ic_labs_swap,
    ),
    PluginDisplayData(
        key = AppKey.Collectibles,
        name = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_app_plugins_collectibles),
        onIcon = R.drawable.ic_labs_collectibles,
    ),
    PluginDisplayData(
        key = AppKey.Valuables,
        name = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_app_plugins_valuables),
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
