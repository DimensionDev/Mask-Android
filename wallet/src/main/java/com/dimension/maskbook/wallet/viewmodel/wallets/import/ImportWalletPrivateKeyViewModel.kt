/*
 *  Mask-Android
 *
 *  Copyright (C) DimensionDev and Contributors
 * 
 *  This file is part of Mask-Android.
 * 
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Mask-Android. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.wallet.viewmodel.wallets.import

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.db.model.CoinPlatformType
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.WalletCreateOrImportResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ImportWalletPrivateKeyViewModel(
    private val wallet: String,
    private val repository: IWalletRepository,
) : ViewModel() {
    private val _privateKey = MutableStateFlow("")
    val privateKey = _privateKey.asStateIn(viewModelScope, "")

    fun setPrivateKey(privateKey: String) {
        _privateKey.value = privateKey
    }

    val canConfirm by lazy {
        _privateKey.map {
            it.isNotEmpty() && repository.validatePrivateKey(privateKey = _privateKey.value)
        }
    }

    fun confirm(onResult: (WalletCreateOrImportResult) -> Unit) {
        viewModelScope.launch {
            val platform = repository.dWebData.firstOrNull()?.coinPlatformType ?: CoinPlatformType.Ethereum
            repository.importWallet(
                name = wallet,
                privateKey = _privateKey.value,
                platformType = platform
            )
            onResult(
                WalletCreateOrImportResult(
                    type = WalletCreateOrImportResult.Type.SUCCESS,
                )
            )
        }
    }
}
