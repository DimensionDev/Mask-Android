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
package com.dimension.maskbook.labs.viewmodel

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.labs.R
import com.dimension.maskbook.labs.export.model.AppKey
import com.dimension.maskbook.labs.repository.IAppRepository
import com.dimension.maskbook.wallet.export.WalletServices
import com.dimension.maskbook.wallet.ext.asStateIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

data class AppDisplayData(
    val key: AppKey,
    @DrawableRes val onIcon: Int,
    @StringRes val name: Int,
    val description: String,
    val enabled: Boolean = true,
    val canExplore: Boolean = false,
)

private val displayDataList = listOf(
    AppDisplayData(
        key = AppKey.Transak,
        name = R.string.scene_app_plugins_transaction,
        description = "Buy crypto in 60+ countries with Transak support.",
        onIcon = R.drawable.ic_labs_transak,
        canExplore = true,
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
    private val walletRepository: WalletServices,
) : ViewModel() {

    init {
        repository.init()
    }

    val apps by lazy {
        repository.apps
            .map { apps ->
                displayDataList.map { item ->
                    item.copy(
                        enabled = item.canExplore && apps.find { it.key === item.key }?.enabled ?: true
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
