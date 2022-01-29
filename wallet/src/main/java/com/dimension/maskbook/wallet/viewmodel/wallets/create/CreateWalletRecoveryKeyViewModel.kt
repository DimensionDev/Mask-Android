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
package com.dimension.maskbook.wallet.viewmodel.wallets.create

import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.db.model.CoinPlatformType
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.WalletCreateOrImportResult
import com.dimension.maskbook.wallet.viewmodel.base.BaseMnemonicPhraseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID

class CreateWalletRecoveryKeyViewModel(
    private val repository: IWalletRepository
) : BaseMnemonicPhraseViewModel() {
    private val _wallet = MutableStateFlow("")

    fun setWallet(wallet: String) {
        _wallet.value = wallet
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun generateWords(): List<String> {
        return repository.generateNewMnemonic()
    }

    private val _result = MutableStateFlow<WalletCreateOrImportResult?>(null)
    val result = _result.asStateIn(viewModelScope, null)
    override fun confirm() {
        viewModelScope.launch {
            try {
                val platform = repository.dWebData.firstOrNull()?.coinPlatformType ?: CoinPlatformType.Ethereum
                repository.createWallet(_words.value, _wallet.value, platform)
                _result.value = WalletCreateOrImportResult(
                    type = WalletCreateOrImportResult.Type.SUCCESS,
                )
            } catch (e: Throwable) {
                _result.value = WalletCreateOrImportResult(
                    type = WalletCreateOrImportResult.Type.ERROR,
                    message = UUID.randomUUID().toString()
                )
            }
        }
    }
}
