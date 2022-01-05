package com.dimension.maskbook.wallet.viewmodel.wallets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.Validator
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class SetUpPaymentPasswordViewModel(
    private val repository: ISettingsRepository,
) : ViewModel() {
    private val _newPassword = MutableStateFlow("")
    val newPassword = _newPassword.asStateIn(viewModelScope, "")
    fun setNewPassword(value: String) {
        _newPassword.value = value
    }

    private val _newPasswordConfirm = MutableStateFlow("")
    val newPasswordConfirm = _newPasswordConfirm.asStateIn(viewModelScope, "")
    fun setNewPasswordConfirm(value: String) {
        _newPasswordConfirm.value = value
    }

    val canConfirm by lazy {
        combine(
            newPassword,
            newPasswordConfirm
        ) { newPassword, newPasswordConfirm ->
            newPassword.isNotEmpty() && newPassword == newPasswordConfirm && Validator.isValidPasswordFormat(
                newPassword
            )
        }
    }

    fun confirm() {
        repository.setPaymentPassword(newPassword.value)
    }
}