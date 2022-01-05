package com.dimension.maskbook.wallet.viewmodel.persona.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import com.dimension.maskbook.wallet.repository.SocialData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

abstract class ConnectSocialViewModel : ViewModel() {
    abstract val items: Flow<List<SocialData>>
    abstract fun connect(data: SocialData, personaId: String)
}

class TwitterConnectSocialViewModel(
    private val repository: IPersonaRepository
) : ConnectSocialViewModel() {
    override val items: Flow<List<SocialData>>
        get() = repository.twitter.map { it.sortedBy { it.personaId } }
            .asStateIn(viewModelScope, emptyList())

    override fun connect(data: SocialData, personaId: String) {
        repository.connectTwitter(personaId = personaId, data.name)
    }
}

class FaceBookConnectSocialViewModel(
    private val repository: IPersonaRepository
) : ConnectSocialViewModel() {
    override val items: Flow<List<SocialData>>
        get() = repository.facebook.map { it.sortedBy { it.personaId } }
            .asStateIn(viewModelScope, emptyList())

    override fun connect(data: SocialData, personaId: String) {
        repository.connectFacebook(personaId = personaId, data.name)
    }
}