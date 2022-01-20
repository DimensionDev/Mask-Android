package com.dimension.maskbook.wallet.viewmodel.register

import com.dimension.maskbook.wallet.repository.IPersonaRepository
import com.dimension.maskbook.wallet.viewmodel.base.BaseMnemonicPhraseViewModel

class CreateIdentityViewModel(
    private val personaName: String,
    private val repository: IPersonaRepository
) : BaseMnemonicPhraseViewModel() {

    override fun generateWords(): List<String> {
        return repository.generateNewMnemonic()
    }

    override fun confirm() {
        repository.createPersonaFromMnemonic(_words.value, personaName)
    }
}
