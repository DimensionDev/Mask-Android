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


data class AppDisplayData(
    val key: AppKey,
    @DrawableRes val onIcon: Int,
    val name: String,
    val description: String,
    val canExplore: Boolean = false,
    val settingRoute: String? = null,
    val enabled: Boolean = true,
)

private val displayDataList = listOf(
    AppDisplayData(
        key = AppKey.Transak,
        name = "Transak",
        description = "Buy crypto in 60+ countries with Transak support.",
        onIcon = R.drawable.ic_labs_transak,
        canExplore = true,
    ),
    AppDisplayData(
        key = AppKey.ITO,
        name = "ITO",
        description = "Enable users to buy tokens directly on Twitter.",
        onIcon = R.drawable.ic_labs_ito,
        canExplore = true,
    ),
    AppDisplayData(
        key = AppKey.LuckDrop,
        name = "Lucky Drop",
        description = "Surprise your encrypted friends with Token/NFT Lucky Drops.",
        onIcon = R.drawable.ic_labs_packet,
        canExplore = true,
    ),
    // TODO swap ?
    AppDisplayData(
        key = AppKey.FileService,
        name = "File Service",
        description = "Decentralized file storage for users.",
        onIcon = R.drawable.ic_labs_encrypted_file,
        canExplore = true,
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
