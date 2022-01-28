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
package com.dimension.maskbook.wallet.ui.scenes.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.Appearance
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.MaskSelection
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.viewmodel.settings.AppearanceSettingsViewModel
import org.koin.androidx.compose.getViewModel

val appearanceMap = mapOf(
    Appearance.default to R.string.scene_setting_detail_automatic,
    Appearance.light to R.string.scene_setting_detail_light,
    Appearance.dark to R.string.scene_setting_detail_dark,
)

@Composable
fun AppearanceSettings(
    onBack: () -> Unit,
) {
    val viewModel: AppearanceSettingsViewModel = getViewModel()
    val appearance by viewModel.appearance.observeAsState(initial = Appearance.default)
    MaskModal {
        Column(
            modifier = Modifier.padding(ScaffoldPadding)
        ) {
            appearanceMap.forEach {
                MaskSelection(
                    selected = it.key == appearance,
                    onClicked = {
                        viewModel.setAppearance(it.key); onBack.invoke()
                    },
                    content = {
                        Text(text = stringResource(it.value))
                    }
                )
            }
        }
    }
}
