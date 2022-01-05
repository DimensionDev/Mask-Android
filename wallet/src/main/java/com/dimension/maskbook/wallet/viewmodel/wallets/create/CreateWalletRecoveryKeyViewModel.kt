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
import java.util.*

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