package com.dimension.maskbook.wallet.viewmodel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.Appearance
import com.dimension.maskbook.wallet.repository.ISettingsRepository

class AppearanceSettingsViewModel(
    private val repository: ISettingsRepository,
): ViewModel() {
    val appearance by lazy {
        repository.appearance.asStateIn(viewModelScope, Appearance.default)
    }

    fun setAppearance(appearance: Appearance) {
        repository.setAppearance(appearance = appearance)
    }
}