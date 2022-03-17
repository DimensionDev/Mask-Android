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
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.labs.R
import com.dimension.maskbook.labs.export.model.AppKey
import com.dimension.maskbook.labs.repository.IAppRepository
import com.dimension.maskbook.wallet.export.WalletServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.android.annotation.KoinViewModel

data class AppDisplayData(
    val key: AppKey,
    @DrawableRes val onIcon: Int,
    @StringRes val name: Int,
    @StringRes val description: Int,
    val enabled: Boolean = true,
    val canExplore: Boolean = false,
)

private val displayDataList = listOf(
    AppDisplayData(
        key = AppKey.Transak,
        name = R.string.scene_app_plugins_transaction,
        description = R.string.scene_app_plugins_transaction_desc,
        onIcon = R.drawable.ic_labs_transak,
        canExplore = true,
    ),
    AppDisplayData(
        key = AppKey.ITO,
        name = R.string.scene_app_plugins_ito,
        description = R.string.scene_app_plugins_ito_desc,
        onIcon = R.drawable.ic_labs_ito,
    ),
    AppDisplayData(
        key = AppKey.LuckDrop,
        name = R.string.scene_app_plugins_lucy_drop,
        description = R.string.scene_app_plugins_lucy_drop_desc,
        onIcon = R.drawable.ic_labs_packet,
    ),
    AppDisplayData(
        key = AppKey.Swap,
        name = R.string.scene_app_plugins_swap,
        description = R.string.scene_app_plugins_swap_desc,
        onIcon = R.drawable.ic_labs_swap,
    ),
    AppDisplayData(
        key = AppKey.FileService,
        name = R.string.scene_app_plugins_file_service,
        description = R.string.scene_app_plugins_file_service_desc,
        onIcon = R.drawable.ic_labs_encrypted_file,
    ),
)

@KoinViewModel
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
