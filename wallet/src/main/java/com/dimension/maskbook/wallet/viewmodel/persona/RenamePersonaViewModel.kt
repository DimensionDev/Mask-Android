package com.dimension.maskbook.wallet.viewmodel.persona

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class RenamePersonaViewModel(
    private val repository: IPersonaRepository,
    private val personaId: String,
) : ViewModel() {
    private val _name = MutableStateFlow("")
    val name = _name.asStateIn(viewModelScope, "")
    init {
        viewModelScope.launch {
            _name.value = repository.persona
                .map { it.firstOrNull { it.id == personaId } }
                .firstOrNull()?.name ?: ""
        }
    }

    fun setName(value: String) {
        _name.value = value
    }

    fun confirm() {
        repository.updatePersona(personaId, _name.value)
    }
}