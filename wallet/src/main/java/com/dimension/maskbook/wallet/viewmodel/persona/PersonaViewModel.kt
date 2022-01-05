package com.dimension.maskbook.wallet.viewmodel.persona

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import kotlinx.coroutines.launch

class PersonaViewModel(
    private val repository: IPersonaRepository
): ViewModel() {
    val persona = repository.currentPersona.asStateIn(viewModelScope, null)

    init {
        loadPersona()
    }

    private fun loadPersona() = viewModelScope.launch {

    }
}