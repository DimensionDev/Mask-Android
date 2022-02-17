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
package com.dimension.maskbook.setting.ui.scenes

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.MaskSelection
import com.dimension.maskbook.setting.export.model.Language
import com.dimension.maskbook.setting.viewmodel.LanguageSettingsViewModel
import org.koin.androidx.compose.getViewModel

val languageMap = Language.values().map {
    it to it.value
}.toMap()

@Composable
fun LanguageSettings(
    onBack: () -> Unit,
) {
    val viewModel: LanguageSettingsViewModel = getViewModel()
    val language by viewModel.language.observeAsState(initial = Language.auto)
    MaskModal {
        Column {
            languageMap.forEach {
                MaskSelection(
                    selected = it.key == language,
                    onClicked = {
                        viewModel.setLanguage(it.key)
                        onBack.invoke()
                    },
                    content = {
                        Text(text = it.value)
                    }
                )
            }
        }
    }
}
