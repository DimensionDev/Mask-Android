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
package com.dimension.maskbook.wallet.ui.scenes.wallets.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.common.ui.widget.PrimaryButton
import com.dimension.maskbook.common.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.R

@Composable
fun WalletIntroScene(
    onCreate: () -> Unit,
    onImport: () -> Unit,
    onConnect: () -> Unit,
) {
    MaskScaffold(
        topBar = {
            MaskSingleLineTopAppBar()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ScaffoldPadding),
        ) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painterResource(id = R.drawable.ic_wallet_intro),
                    contentDescription = null
                )
            }
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onCreate,
            ) {
                Text(text = stringResource(R.string.common_controls_create_wallet))
            }
            ButtonSpacing()
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onImport,
            ) {
                Text(text = stringResource(R.string.common_controls_import_wallet))
            }
            ButtonSpacing()
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onConnect,
            ) {
                Text(text = stringResource(R.string.scene_wallet_connect_wallet_connect))
            }
            ButtonSpacing()
        }
    }
}

@Composable
private fun ButtonSpacing() {
    Spacer(modifier = Modifier.height(16.dp))
}
