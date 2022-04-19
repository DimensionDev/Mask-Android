/*
 *  Mask-Android
 *
 *  Copyright (C) 2022  DimensionDev and Contributors
 *
 *  This file is part of Mask-Android.
 *
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Mask-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.setting.data

import com.dimension.maskbook.setting.export.model.Appearance
import com.dimension.maskbook.setting.export.model.DataProvider
import com.dimension.maskbook.setting.export.model.Language
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal class JSDataSource(
    private val jsMethod: JSMethod,
    private val appScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
) {
    private val _appearance = MutableStateFlow(Appearance.default)
    private val _dataProvider = MutableStateFlow(DataProvider.COIN_GECKO)
    private val _language = MutableStateFlow(Language.auto)

    val language = _language.asSharedFlow()
    val appearance = _appearance.asSharedFlow()
    val dataProvider = _dataProvider.asSharedFlow()

    fun setLanguage(language: Language) {
        appScope.launch(dispatcher) {
            jsMethod.setLanguage(language)
            _language.value = jsMethod.getLanguage()
        }
    }

    fun setAppearance(appearance: Appearance) {
        appScope.launch(dispatcher) {
            jsMethod.setTheme(appearance)
            _appearance.value = jsMethod.getTheme()
        }
    }

    fun setDataProvider(dataProvider: DataProvider) {
        appScope.launch(dispatcher) {
            jsMethod.setTrendingDataSource(dataProvider)
            _dataProvider.value = jsMethod.getTrendingDataSource()
        }
    }

    fun initData() {
        appScope.launch(dispatcher) {
            awaitAll(
                async { _language.value = jsMethod.getLanguage() },
                async { _appearance.value = jsMethod.getTheme() },
                async { _dataProvider.value = jsMethod.getTrendingDataSource() },
            )
        }
    }
}
