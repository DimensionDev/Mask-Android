package com.dimension.maskbook.wallet.viewmodel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.ISettingsRepository
import com.dimension.maskbook.wallet.repository.Language

class LanguageSettingsViewModel(
    private val repository: ISettingsRepository
) : ViewModel() {
    val language by lazy {
        repository.language.asStateIn(viewModelScope, Language.auto)
    }

    fun setLanguage(language: Language) {
        repository.setLanguage(language = language)
    }
}