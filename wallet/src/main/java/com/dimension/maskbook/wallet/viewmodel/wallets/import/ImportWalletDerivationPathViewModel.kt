package com.dimension.maskbook.wallet.viewmodel.wallets.import

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.dimension.maskbook.wallet.db.model.CoinPlatformType
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.WalletCreateOrImportResult
import com.dimension.maskbook.wallet.repository.model.DerivationPath
import com.dimension.maskwalletcore.CoinType
import com.dimension.maskwalletcore.WalletKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class BalanceSource(
    private val wallet: String,
    private val walletKey: WalletKey,
) : PagingSource<Int, ImportWalletDerivationPathViewModel.BalanceRow>() {
    override fun getRefreshKey(state: PagingState<Int, ImportWalletDerivationPathViewModel.BalanceRow>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImportWalletDerivationPathViewModel.BalanceRow> {
        val nextPageNumber = params.key ?: 0
        return (nextPageNumber..params.loadSize).map { index ->
            walletKey.addNewAccountAtPath(
                CoinType.Ethereum,
                DerivationPath(40, 60, 0, 0, index).toString(),
                "${wallet}-${index}",
                ""
            ).let { walletAccount ->
                ImportWalletDerivationPathViewModel.BalanceRow(
                    address = walletAccount.address,
                    balances = 0f,// TODO: Load balances
                    path = DerivationPath(40, 60, 0, 0, index).toString(),
                )
            }
        }.let {
            LoadResult.Page(
                data = it,
                prevKey = null,
                nextKey = nextPageNumber + it.size
            )
        }
    }

}

class ImportWalletDerivationPathViewModel(
    private val wallet: String,
    private val mnemonicCode: List<String>,
    private val walletRepository: IWalletRepository,
) : ViewModel() {
    private val walletKey by lazy {
        WalletKey.fromMnemonic(mnemonic = mnemonicCode.joinToString(" "), "")
    }

    //TODO Logic:get derivation Path*
    val derivationPath = MutableStateFlow("(m/44'/60'/0'/0/0)").asStateIn(viewModelScope, "")

    private val _checked = MutableStateFlow(listOf<String>())
    val checked = _checked.asStateIn(viewModelScope, emptyList())

    val balancesPager by lazy {
        Pager(
            PagingConfig(20)
        ) {
            BalanceSource(wallet, walletKey)
        }.flow.cachedIn(viewModelScope)
    }

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

    fun switchStatus(
        selectRow: BalanceRow
    ) {
        _checked.value.let {
            if (it.any { it == selectRow.path }) {
                _checked.value -= selectRow.path
            } else {
                _checked.value += selectRow.path
            }
        }
    }
    data class BalanceRow(
        val address: String,
        val balances: Float,
        val path: String,
    )
}