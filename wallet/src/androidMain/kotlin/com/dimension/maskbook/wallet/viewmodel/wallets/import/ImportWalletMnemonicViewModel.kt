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
package com.dimension.maskbook.wallet.viewmodel.wallets.import

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.WalletCreateOrImportResult
import com.dimension.maskbook.wallet.repository.WalletData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class ImportWalletMnemonicViewModel(
    private val wallet: String,
    private val repository: IWalletRepository,
) : ViewModel() {
    private val _words = MutableStateFlow("")
    val words = _words.asStateIn(viewModelScope, "")
    val hintWords by lazy {
        _words.map {
            generateHintWords(it)
        }
    }

    private fun generateHintWords(inputWords: String): List<String> {
        return if (inputWords.isEmpty()) emptyList() else inputWords.split(" ")
    }

    fun setWords(words: String) {
        _words.value = words
    }

    val canConfirm by lazy {
        _words.map { it.isNotEmpty() && repository.validateMnemonic(it) }
    }

    fun confirm(onResult: (WalletCreateOrImportResult) -> Unit) {
        /*TODO Logic:import wallet with keystore, post result*/
        onResult(
            WalletCreateOrImportResult(
                type = WalletCreateOrImportResult.Type.SUCCESS,
                wallet = WalletData(
                    id = UUID.randomUUID().toString(),
                    name = wallet,
                    address = "",
                    fromWalletConnect = false,
                    imported = false,
                    tokens = emptyList(),
                    balance = emptyMap(),
                )
            )
        )
    }

    fun applyHintWords(hint: String) {
        viewModelScope.launch {
            _words.value = _words.first().split(" ").toMutableList().apply {
                removeAt(lastIndex)
                add(hint)
            }.joinToString(separator = " ")
        }
    }
}
