package com.dimension.maskbook.wallet.viewmodel.persona.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import com.dimension.maskbook.wallet.repository.IPostRepository
import com.dimension.maskbook.wallet.repository.PersonaData
import com.dimension.maskbook.wallet.repository.PostData
import kotlinx.coroutines.flow.combine

class PostViewModel(
    repository: IPostRepository,
    personaRepository: IPersonaRepository,
): ViewModel() {
    val items = repository.posts
        .combine(personaRepository.currentPersona) { a: List<PostData>, b: PersonaData? ->
            a.filter { it.personaId == b?.id }
        }
        .asStateIn(viewModelScope, emptyList())
}