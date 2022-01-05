package com.dimension.maskbook.wallet.viewmodel.persona

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class ExportPrivateKeyViewModel(
    private val repository: IPersonaRepository,
) : ViewModel() {
    val privateKey = repository.currentPersona
        .mapNotNull { it }
        .map { repository.backupPrivateKey(it.id) }
        .asStateIn(viewModelScope, "")
}