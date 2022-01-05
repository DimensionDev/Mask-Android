package com.dimension.maskbook.wallet.viewmodel.register

import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import com.dimension.maskbook.wallet.viewmodel.base.BaseMnemonicPhraseViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class CreateIdentityViewModel(
    private val repository: IPersonaRepository
) : BaseMnemonicPhraseViewModel() {
    private val _persona = MutableStateFlow("")
    val persona = _persona.asStateIn(viewModelScope, "")

    fun setPersona(text: String) {
        _persona.value = text
    }

    override fun generateWords(): List<String> {
        return repository.generateNewMnemonic()
    }

    override fun confirm() {
        repository.createPersonaFromMnemonic(_words.value, _persona.value)
    }
}