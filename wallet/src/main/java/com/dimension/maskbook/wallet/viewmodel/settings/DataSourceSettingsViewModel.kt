/*
 *  Mask-Android
 *
 *  Copyright (C) DimensionDev and Contributors
 * 
 *  This file is part of Mask-Android.
 * 
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Mask-Android. If not, see <http://www.gnu.org/licenses/>.
 */
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
