package com.dimension.maskbook.wallet.viewmodel.wallets.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ISendHistoryRepository
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.TokenData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapNotNull

class SendTokenViewModel(
    private val tokenData: TokenData,
    private val toAddress: String,
    private val sendHistoryRepository: ISendHistoryRepository,
    private val walletRepository: IWalletRepository,
    private val settingsRepository: ISettingsRepository,
) : ViewModel() {
    private val _password = MutableStateFlow("")
    val password = _password.asStateIn(viewModelScope, "")
    fun setPassword(value: String) {
        _password.value = value
    }

    val canConfirm by lazy {
        combine(settingsRepository.paymentPassword, _password) { current, input ->
            current == input
        }
    }

    private val _amount = MutableStateFlow("0")
    val amount = _amount.asStateIn(viewModelScope, "0")
    fun setAmount(value: String) {
        _amount.value = value
    }

    val addressData by lazy {
        sendHistoryRepository.getByAddress(toAddress)
            .asStateIn(viewModelScope, null)
            .mapNotNull { it }
    }
    val walletTokenData by lazy {
        walletRepository.currentWallet
            .mapNotNull { it }
            .mapNotNull { it.tokens.firstOrNull { it.tokenAddress == tokenData.address } }
            .asStateIn(viewModelScope, null)
            .mapNotNull { it }
    }

}