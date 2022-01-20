package com.dimension.maskbook.wallet.viewmodel.wallets.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ISendHistoryRepository
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import com.dimension.maskbook.wallet.repository.IWalletRepository
import com.dimension.maskbook.wallet.repository.TokenData
import kotlinx.coroutines.flow.*

class SendTokenViewModel(
    private val toAddress: String,
    private val sendHistoryRepository: ISendHistoryRepository,
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

}
