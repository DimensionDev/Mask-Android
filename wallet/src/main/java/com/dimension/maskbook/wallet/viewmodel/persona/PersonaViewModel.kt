package com.dimension.maskbook.wallet.viewmodel.persona

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class PersonaViewModel(
    private val repository: IPersonaRepository
) : ViewModel() {

    val currentPersona by lazy {
        repository.currentPersona.asStateIn(viewModelScope, null)
    }

    val socialList by lazy {
        combine(
            currentPersona,
            repository.twitter,
            repository.facebook
        ) { persona, twitterList, facebookList ->
            val isEmpty = twitterList.isEmpty() && facebookList.isEmpty()
            if (isEmpty) {
                return@combine emptyList()
            }

            val allList = twitterList + facebookList
            if (persona == null) {
                return@combine allList
            }

            allList.filter { it.personaId == persona.id }
        }.flowOn(Dispatchers.IO).asStateIn(viewModelScope, null)
    }

    init {
        loadPersona()
    }

    private fun loadPersona() = viewModelScope.launch {

    }
}