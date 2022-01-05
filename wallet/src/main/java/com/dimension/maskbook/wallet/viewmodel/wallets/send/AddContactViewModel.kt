package com.dimension.maskbook.wallet.viewmodel.wallets.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.IWalletContactRepository
import kotlinx.coroutines.flow.MutableStateFlow

class AddContactViewModel(
    private val repository: IWalletContactRepository,
): ViewModel() {
    private val _name = MutableStateFlow("")
    val name = _name.asStateIn(viewModelScope, "")
    fun setName(value: String) {
        _name.value = value
    }

    fun confirm(name: String, address: String) {
        repository.addOrUpdate(name = name, address = address)
    }
}