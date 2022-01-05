package com.dimension.maskbook.wallet.viewmodel.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import kotlinx.coroutines.flow.MutableStateFlow

class IdentityViewModel(
    private val repository: IPersonaRepository
): ViewModel() {
    private val _identity = MutableStateFlow("")
    val identity = _identity.asStateIn(viewModelScope, "")

    fun setIdentity(text: String) {
        _identity.value = text
    }

    fun onConfirm() {
        repository.createPersonaFromMnemonic(_identity.value.trim().split(" "), "")
    }
}