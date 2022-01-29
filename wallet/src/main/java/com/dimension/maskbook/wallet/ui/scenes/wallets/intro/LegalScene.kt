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
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.MaskBackButton
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskTextButton
import com.dimension.maskbook.wallet.ui.widget.MaskTopAppBar
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding

@Composable
fun LegalScene(
    onBack: () -> Unit,
    onBrowseAgreement: () -> Unit,
    onAccept: () -> Unit,
) {
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskTopAppBar(
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    },
                    title = {
                        Text(text = stringResource(R.string.scene_terms_of_service_title))
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(ScaffoldPadding)
            ) {
                Text(
                    text = stringResource(R.string.scene_terms_of_service_description),
                    style = MaterialTheme.typography.subtitle1,
                )
                Box(
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painterResource(id = R.drawable.ic_legal),
                        contentDescription = null
                    )
                }
                MaskTextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onBrowseAgreement,
                ) {
                    Text(text = stringResource(R.string.scene_terms_of_service_service_agreement))
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Filled.ArrowForwardIos,
                        contentDescription = stringResource(R.string.scene_terms_of_service_service_agreement),
                        modifier = Modifier.size(16.dp),
                    )
                }
                Spacer(modifier = Modifier.height(64.dp))
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onAccept,
                ) {
                    Text(text = stringResource(R.string.common_controls_accept))
                }
            }
        }
    }
}
