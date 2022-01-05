package com.dimension.maskbook.wallet.viewmodel.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import com.dimension.maskbook.wallet.repository.PlatformType
import kotlinx.coroutines.flow.MutableStateFlow

class UserNameModalViewModel(
    private val personaRepository: IPersonaRepository
): ViewModel() {
    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateIn(viewModelScope, "")
    fun setUserName(value: String) {
        _userName.value = value
    }
    fun done(name: String) {
        personaRepository.finishConnectingProcess(name, PlatformType.Twitter)
    }
}