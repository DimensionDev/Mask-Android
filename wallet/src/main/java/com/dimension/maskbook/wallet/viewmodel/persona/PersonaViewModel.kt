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
        combine(repository.twitter, repository.facebook) { twitterList, facebookList ->
            val hasAccount = twitterList.isNotEmpty() || facebookList.isNotEmpty()
            if (hasAccount) {
                twitterList + facebookList
            } else {
                emptyList()
            }
        }.flowOn(Dispatchers.IO).asStateIn(viewModelScope, null)
    }

    init {
        loadPersona()
    }

    private fun loadPersona() = viewModelScope.launch {

    }
}