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

import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.wallet.db.model.CoinPlatformType
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.WalletCreateOrImportResult
import com.dimension.maskbook.wallet.repository.model.DerivationPath
import com.dimension.maskwalletcore.CoinType
import com.dimension.maskwalletcore.WalletKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class ImportWalletDerivationPathViewModel(
    private val wallet: String,
    private val mnemonicCode: List<String>,
    private val walletRepository: IWalletRepository,
) : ViewModel() {

    private val walletKey by lazy {
        WalletKey.fromMnemonic(mnemonic = mnemonicCode.joinToString(" "), "")
    }

    // TODO Logic:get derivation Path*
    val derivationPath = MutableStateFlow("(m/44'/60'/0'/0)").asStateIn(viewModelScope, "")

    private val _checked = MutableStateFlow(listOf<String>())
    val checked = _checked.asStateIn(viewModelScope, emptyList())

    fun next(onResult: (WalletCreateOrImportResult) -> Unit) {
        viewModelScope.launch {
            val platform = walletRepository.dWebData.firstOrNull()?.coinPlatformType
                ?: CoinPlatformType.Ethereum
            val path = checked.firstOrNull() ?: emptyList()
            walletRepository.importWallet(
                mnemonicCode = mnemonicCode,
                name = wallet,
                path = path,
                platformType = platform,
            )
            onResult(
                WalletCreateOrImportResult(
                    type = WalletCreateOrImportResult.Type.SUCCESS,
                )
            )
        }
    }

    fun switchStatus(selectPath: String) {
        _checked.value.let { list ->
            if (list.any { it == selectPath }) {
                _checked.value -= selectPath
            } else {
                _checked.value += selectPath
            }
        }
    }

    private val pagerItemsFlows = hashMapOf<Int, StateFlow<List<BalanceRow>>>()
    private val balanceFlows = hashMapOf<Int, MutableStateFlow<SnapshotStateMap<String, String>>>()

    fun getPagerItems(page: Int): StateFlow<List<BalanceRow>> {
        return pagerItemsFlows.getOrPut(page) {
            createPagerItemsFlow(page)
        }
    }

    private fun createPagerItemsFlow(page: Int) = flow {
        val startIndex = page * pageSize
        val list =
            (startIndex until startIndex + pageSize).map { index ->
                val derivationPath = DerivationPath(44, 60, 0, 0, index).toString()
                val walletAccount = walletKey.addNewAccountAtPath(
                    CoinType.Ethereum,
                    derivationPath,
                    "$wallet-$index",
                    ""
                )

                // too slow
                loadBalance(page, walletAccount.address)

                BalanceRow(
                    address = walletAccount.address,
                    path = derivationPath,
                    isAdded = walletRepository.findWalletByAddress(walletAccount.address) != null,
                )
            }.sortedBy { it.isAdded }
        emit(list)
    }.flowOn(Dispatchers.IO).asStateIn(viewModelScope, emptyList())

    private fun loadBalance(page: Int, address: String) = viewModelScope.launch {
        val stateFlow = getBalanceStateMap(page)
        stateFlow.value = stateFlow.value.apply {
            try {
                put(address, walletRepository.getTotalBalance(address).toString())
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun getBalanceStateMap(page: Int): MutableStateFlow<SnapshotStateMap<String, String>> {
        return balanceFlows.getOrPut(page) {
            MutableStateFlow(SnapshotStateMap())
        }
    }

    fun getBalanceMap(page: Int): StateFlow<Map<String, String>> {
        return getBalanceStateMap(page)
    }

    override fun onCleared() {
        super.onCleared()
        pagerItemsFlows.clear()
        balanceFlows.clear()
    }

    data class BalanceRow(
        val address: String,
        val path: String,
        val isAdded: Boolean,
    )

    companion object {
        private const val pageSize = 10
    }
}
