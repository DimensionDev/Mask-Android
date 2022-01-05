package com.dimension.maskbook.wallet.viewmodel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.wallet.ext.asStateIn
import com.dimension.maskbook.wallet.repository.DataProvider
import com.dimension.maskbook.wallet.repository.ISettingsRepository

class DataSourceSettingsViewModel(
    private val repository: ISettingsRepository,
) : ViewModel() {
    val dataProvider by lazy {
        repository.dataProvider.asStateIn(viewModelScope, DataProvider.COIN_GECKO)
    }

    fun setDataProvider(dataProvider: DataProvider) {
        repository.setDataProvider(dataProvider = dataProvider)
    }
}