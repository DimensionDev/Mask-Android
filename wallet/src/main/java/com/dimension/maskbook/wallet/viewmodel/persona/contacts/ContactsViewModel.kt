package com.dimension.maskbook.wallet.viewmodel.persona.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ContactData
import com.dimension.maskbook.wallet.repository.IContactsRepository
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import com.dimension.maskbook.wallet.repository.PersonaData
import kotlinx.coroutines.flow.combine

class ContactsViewModel(
    repository: IContactsRepository,
    personaRepository: IPersonaRepository
): ViewModel() {
    val items = repository.contacts
        .combine(personaRepository.currentPersona) { a: List<ContactData>, b: PersonaData? ->
            a.filter { it.personaId == b?.id }
        }
        .asStateIn(viewModelScope, emptyList())

}