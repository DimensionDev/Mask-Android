package com.dimension.maskbook.wallet.viewmodel.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import kotlinx.coroutines.flow.MutableStateFlow

class PrivateKeyViewModel(
    private val repository: IPersonaRepository,
): ViewModel() {
    private val _privateKey = MutableStateFlow("")
    val privateKey = _privateKey.asStateIn(viewModelScope, "")

    fun setPrivateKey(text: String) {
        _privateKey.value = text
    }

    fun onConfirm() {
        repository.createPersonaFromPrivateKey(_privateKey.value.trim())
    }
}