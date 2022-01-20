package com.dimension.maskbook.wallet.viewmodel.app

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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


data class AppDisplayData(
    val key: AppKey,
    @DrawableRes val onIcon: Int,
    @StringRes val name: Int,
    val description: String,
    val enabled: Boolean = true,
)

private val displayDataList = listOf(
    AppDisplayData(
        key = AppKey.Transak,
        name = R.string.scene_app_plugins_transaction,
        description = "Buy crypto in 60+ countries with Transak support.",
        onIcon = R.drawable.ic_labs_transak,
    ),
    AppDisplayData(
        key = AppKey.ITO,
        name = R.string.scene_app_plugins_ito,
        description = "Enable users to buy tokens directly on Twitter.",
        onIcon = R.drawable.ic_labs_ito,
    ),
    AppDisplayData(
        key = AppKey.LuckDrop,
        name = R.string.scene_app_plugins_lucy_drop,
        description = "Surprise your encrypted friends with Token/NFT Lucky Drops.",
        onIcon = R.drawable.ic_labs_packet,
    ),
    AppDisplayData(
        key = AppKey.Swap,
        name = R.string.scene_app_plugins_swap,
        description = "Pop-up trading widget that allows you to instantly view prices of the hottest Crypto/Stock and trade. Can also invest in the best performing managers.",
        onIcon = R.drawable.ic_labs_swap,
    ),
    AppDisplayData(
        key = AppKey.FileService,
        name = R.string.scene_app_plugins_file_service,
        description = "Decentralized file storage for users.",
        onIcon = R.drawable.ic_labs_encrypted_file,
    ),
)

class LabsViewModel(
    private val repository: IAppRepository,
    private val walletRepository: IWalletRepository,
) : ViewModel() {

    init {
        repository.init()
    }

    val apps by lazy {
        repository.apps
            .map { apps ->
                displayDataList.map { item ->
                    item.copy(
                        enabled = apps.find { it.key === item.key }?.enabled ?: true
                    )
                }.sortedByDescending { it.enabled }
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
