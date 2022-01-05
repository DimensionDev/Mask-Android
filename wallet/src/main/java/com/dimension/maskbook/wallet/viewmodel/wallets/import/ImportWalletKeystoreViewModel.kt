package com.dimension.maskbook.wallet.viewmodel.wallets.import

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.db.model.CoinPlatformType
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.WalletCreateOrImportResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ImportWalletKeystoreViewModel(
    private val wallet: String,
    private val repository: IWalletRepository,
) : ViewModel() {
    private val _keystore = MutableStateFlow("")
    val keystore = _keystore.asStateIn(viewModelScope, "")

    private val _password = MutableStateFlow("")
    val password = _password.asStateIn(viewModelScope, "")

    val canConfirm by lazy {
        combine(_keystore, _password) { keystore, passowrd ->
            keystore.isNotEmpty() && passowrd.isNotEmpty()
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
        viewModelScope.launch {
            val platform = repository.dWebData.firstOrNull()?.coinPlatformType ?: CoinPlatformType.Ethereum
            repository.importWallet(
                name = wallet,
                keyStore = _keystore.value,
                password = _password.value,
                platformType = platform,
            )
            onResult(
                WalletCreateOrImportResult(
                    type = WalletCreateOrImportResult.Type.SUCCESS,
                )
            )
        }
    }
}