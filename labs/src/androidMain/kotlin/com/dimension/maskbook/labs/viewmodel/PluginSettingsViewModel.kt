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
import com.dimension.maskbook.labs.repository.IPreferenceRepository
import com.dimension.maskbook.wallet.export.WalletServices
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class PluginDisplayData(
    val key: AppKey,
    @DrawableRes val onIcon: Int,
    @StringRes val name: Int,
    val enabled: Boolean = true,
)

private val displayDataList = listOf(
    PluginDisplayData(
        key = AppKey.FileService,
        name = R.string.scene_app_plugins_file_service,
        onIcon = R.drawable.ic_labs_encrypted_file,
    ),
    PluginDisplayData(
        key = AppKey.ITO,
        name = R.string.scene_app_plugins_ito,
        onIcon = R.drawable.ic_labs_ito,
    ),
    PluginDisplayData(
        key = AppKey.LuckDrop,
        name = R.string.scene_app_plugins_lucy_drop,
        onIcon = R.drawable.ic_labs_packet,
    ),
    PluginDisplayData(
        key = AppKey.Transak,
        name = R.string.scene_app_plugins_transaction,
        onIcon = R.drawable.ic_labs_transak,
    ),
    PluginDisplayData(
        key = AppKey.Snapshot,
        name = R.string.scene_app_plugins_snapshot,
        onIcon = R.drawable.ic_labs_snapshot,
    ),
    PluginDisplayData(
        key = AppKey.DHEDGE,
        name = R.string.scene_app_plugins_dhedge,
        onIcon = R.drawable.ic_labs_dhedge,
    ),
    PluginDisplayData(
        key = AppKey.GitCoin,
        name = R.string.scene_app_plugins_gitcoin,
        onIcon = R.drawable.ic_labs_gitcoin,
    ),
    PluginDisplayData(
        key = AppKey.Swap,
        name = R.string.scene_app_plugins_swap,
        onIcon = R.drawable.ic_labs_swap,
    ),
    PluginDisplayData(
        key = AppKey.Collectibles,
        name = R.string.scene_app_plugins_collectibles,
        onIcon = R.drawable.ic_labs_collectibles,
    ),
    PluginDisplayData(
        key = AppKey.Valuables,
        name = R.string.scene_app_plugins_valuables,
        onIcon = R.drawable.ic_labs_union,
    ),
)

class PluginSettingsViewModel(
    private val repository: IAppRepository,
    private val walletRepository: WalletServices,
    private val preferenceRepository: IPreferenceRepository,
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
            .asStateIn(viewModelScope, emptyList())
    }

    val wallet by lazy {
        walletRepository.currentWallet.asStateIn(viewModelScope, null)
    }

    fun setEnabled(key: AppKey, enabled: Boolean) {
        viewModelScope.launch {
            repository.setEnabled(key, enabled)
        }
    }

    val shouldShowPluginSettingsTipDialog by lazy {
        preferenceRepository.shouldShowPluginSettingsTipDialog
            .asStateIn(viewModelScope, false)
    }

    fun setShowPluginSettingsTipDialog(bool: Boolean) {
        viewModelScope.launch {
            preferenceRepository.setShowPluginSettingsTipDialog(bool)
        }
    }
}
