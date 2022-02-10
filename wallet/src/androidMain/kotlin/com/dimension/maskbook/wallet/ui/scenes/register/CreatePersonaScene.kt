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
package com.dimension.maskbook.wallet.ui.scenes.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ui.widget.MaskBackButton
import com.dimension.maskbook.common.ui.widget.MaskInputField
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskTopAppBar
import com.dimension.maskbook.common.ui.widget.PrimaryButton
import com.dimension.maskbook.common.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.R

@Composable
fun CreatePersonaScene(
    onBack: () -> Unit,
    onDone: (String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    MaskScaffold(
        topBar = {
            MaskTopAppBar(
                title = {
                    Text(text = stringResource(R.string.scene_persona_welcome_title))
                },
                subTitle = {
                    Text(text = stringResource(R.string.scene_persona_welcome_description))
                },
                navigationIcon = {
                    MaskBackButton(onBack = onBack)
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(ScaffoldPadding)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = stringResource(R.string.scene_persona_welcome_persona))
            Spacer(modifier = Modifier.height(8.dp))
            MaskInputField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.weight(1f))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (name.isNotBlank()) {
                        onDone.invoke(name)
                    }
                },
            ) {
                Text(text = stringResource(R.string.common_controls_next))
            }
        }
    }
}
