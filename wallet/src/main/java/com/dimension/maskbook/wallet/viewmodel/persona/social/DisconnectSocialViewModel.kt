package com.dimension.maskbook.wallet.viewmodel.persona.social

import androidx.lifecycle.ViewModel
import com.dimension.maskbook.wallet.repository.IPersonaRepository

class DisconnectSocialViewModel(
    private val repository: IPersonaRepository,
): ViewModel() {
    fun disconnectTwitter(personaId: String, socialId: String) {
        repository.disconnectTwitter(personaId, socialId)
    }
    fun disconnectFacebook(personaId: String, socialId: String) {
        repository.disconnectFacebook(personaId, socialId)
    }
}