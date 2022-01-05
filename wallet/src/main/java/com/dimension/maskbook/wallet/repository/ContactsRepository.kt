package com.dimension.maskbook.wallet.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

data class ContactData(
    val id: String,
    val name: String,
    val personaId: String,
)

interface IContactsRepository {
    val contacts: Flow<List<ContactData>>
}

class FakeContactsRepository: IContactsRepository {
    private val _contacts = MutableStateFlow(emptyList<ContactData>())
    override val contacts: Flow<List<ContactData>> = _contacts.asSharedFlow()
}