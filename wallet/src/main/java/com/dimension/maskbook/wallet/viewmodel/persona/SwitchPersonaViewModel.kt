package com.dimension.maskbook.wallet.viewmodel.persona

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import com.dimension.maskbook.wallet.repository.PersonaData

class SwitchPersonaViewModel(
    private val repository: IPersonaRepository
): ViewModel() {
    val items = repository.persona.asStateIn(viewModelScope, emptyList())
    val current = repository.currentPersona.asStateIn(viewModelScope, null)
    fun switch(personaData: PersonaData) {
        repository.setCurrentPersona(personaData.id)
    }
}