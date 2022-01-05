package com.dimension.maskbook.wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import kotlinx.coroutines.flow.MutableStateFlow

class WelcomeViewModel(
    private val repository: IPersonaRepository,
): ViewModel() {
    private val _persona = MutableStateFlow("")
    val persona = _persona.asStateIn(viewModelScope, "")

    fun setPersona(text: String) {
        _persona.value = text
    }

    fun onConfirm() {
        repository.updateCurrentPersona(_persona.value)
    }
}