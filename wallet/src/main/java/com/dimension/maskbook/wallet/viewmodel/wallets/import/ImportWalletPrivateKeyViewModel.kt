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
        _privateKey.map { it.isNotEmpty() }
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