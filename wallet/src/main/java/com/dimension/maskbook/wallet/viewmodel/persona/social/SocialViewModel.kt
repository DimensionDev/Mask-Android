package com.dimension.maskbook.wallet.viewmodel.persona.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import kotlinx.coroutines.flow.map

class SocialViewModel(
    private val repository: IPersonaRepository,
) : ViewModel() {
    val hasPersona = repository.currentPersona.map { it != null }.asStateIn(viewModelScope, false)

    fun setPersona(it: String) {
        repository.addPersona(it)
    }
}

class PersonaSocialViewModel(
    repository: IPersonaRepository,
): ViewModel() {
    val currentPersonaData = repository.currentPersona.asStateIn(viewModelScope, null)
}