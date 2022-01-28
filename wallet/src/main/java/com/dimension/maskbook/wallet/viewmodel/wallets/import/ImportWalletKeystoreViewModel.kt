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
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImportWalletKeystoreViewModel(
    private val wallet: String,
    private val repository: IWalletRepository,
) : ViewModel() {
    private val _keystore = MutableStateFlow("")
    val keystore = _keystore.asStateIn(viewModelScope, "")

    private val _password = MutableStateFlow("")
    val password = _password.asStateIn(viewModelScope, "")

    val canConfirm by lazy {
        combine(_keystore, _password) { keystore, password ->
            keystore.isNotEmpty() &&
                password.isNotEmpty() &&
                repository.validateKeystore(keyStore = _keystore.value)
        }
    }

    fun setKeystore(keystore: String) {
        _keystore.value = keystore
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    private val _result = MutableStateFlow<WalletCreateOrImportResult?>(null)
    val result = _result.asStateIn(viewModelScope, null)
    fun confirm(onResult: (WalletCreateOrImportResult) -> Unit) {
        val handler = CoroutineExceptionHandler { _, _ ->
            onResult(
                WalletCreateOrImportResult(
                    type = WalletCreateOrImportResult.Type.ERROR,
                )
            )
        }
        viewModelScope.launch(handler) {
            val platform =
                repository.dWebData.firstOrNull()?.coinPlatformType ?: CoinPlatformType.Ethereum
            withContext(Dispatchers.IO) {
                repository.importWallet(
                    name = wallet,
                    keyStore = _keystore.value,
                    password = _password.value,
                    platformType = platform,
                )
            }
            onResult(
                WalletCreateOrImportResult(
                    type = WalletCreateOrImportResult.Type.SUCCESS,
                )
            )
        }
    }
}
