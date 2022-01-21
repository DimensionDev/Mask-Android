package com.dimension.maskbook.wallet.viewmodel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.Validator
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class PaymentPasswordSettingsViewModel(
    private val repository: ISettingsRepository,
) : ViewModel() {

    val isNext by lazy {
        combine(
            repository.paymentPassword,
            _isNext
        ) { currentPassword, isNext ->
            currentPassword.isEmpty() || isNext
        }
    }

    private val _isNext = MutableStateFlow(false)

    fun goToNext() {
        _isNext.value = true
    }

    val confirmPassword by lazy {
        combine(
            repository.paymentPassword,
            _password,
        ) { currentPassword, password ->
            currentPassword == password
        }
    }

    private val _password = MutableStateFlow("")
    val password = _password.asStateIn(viewModelScope, "")

    fun setPassword(value: String) {
        _password.value = value
    }

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

    val confirmNewPassword by lazy {
        combine(
            newPassword,
            newPasswordConfirm
        ) {  newPassword, newPasswordConfirm ->
            newPassword.isNotEmpty() &&
                newPassword == newPasswordConfirm &&
                Validator.isValidPasswordFormat(newPassword)
        }
    }

    fun confirm() {
        repository.setPaymentPassword(newPassword.value)
    }
}