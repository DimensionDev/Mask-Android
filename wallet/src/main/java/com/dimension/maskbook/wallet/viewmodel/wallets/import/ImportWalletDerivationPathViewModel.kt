package com.dimension.maskbook.wallet.viewmodel.wallets.import

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.db.model.CoinPlatformType
import com.dimension.maskbook.wallet.ext.asStateIn
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

    //TODO Logic:get derivation Path*
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
        _checked.value.let {
            if (it.any { it == selectPath }) {
                _checked.value -= selectPath
            } else {
                _checked.value += selectPath
            }
        }
    }

    private val balancesFlows = hashMapOf<Int, StateFlow<List<BalanceRow>>>()

    fun getBalances(page: Int): StateFlow<List<BalanceRow>> {
        return balancesFlows.getOrPut(page) {
            createBalanceFlow(page)
        }
    }

    private fun createBalanceFlow(page: Int) = flow {
        val startIndex = page * pageSize
        val list =
            (startIndex until startIndex + pageSize).map { index ->
                val walletAccount = walletKey.addNewAccountAtPath(
                    CoinType.Ethereum,
                    DerivationPath(44, 60, 0, 0, index).toString(),
                    "${wallet}-${index}",
                    ""
                )
                BalanceRow(
                    address = walletAccount.address,
                    balances = 0f,// TODO: Load balances
                    path = DerivationPath(44, 60, 0, 0, index).toString(),
                )
            }
        emit(list)
    }.flowOn(Dispatchers.IO).asStateIn(viewModelScope, emptyList())

    override fun onCleared() {
        super.onCleared()
        balancesFlows.clear()
    }

    data class BalanceRow(
        val address: String,
        val balances: Float,
        val path: String,
    )

    companion object {
        private const val pageSize = 10
    }
}
