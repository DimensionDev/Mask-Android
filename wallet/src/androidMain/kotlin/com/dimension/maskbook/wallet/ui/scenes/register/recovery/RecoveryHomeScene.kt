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
package com.dimension.maskbook.wallet.ui.scenes.register.recovery

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ui.widget.MaskBackButton
import com.dimension.maskbook.common.ui.widget.MaskButton
import com.dimension.maskbook.common.ui.widget.MaskListItem
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskTopAppBar
import com.dimension.maskbook.common.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.R

@Composable
fun RecoveryHomeScene(
    onBack: () -> Unit,
    onIdentity: () -> Unit,
    onPrivateKey: () -> Unit,
    onLocalBackup: () -> Unit,
    onRemoteBackup: () -> Unit,
) {
    MaskScaffold(
        topBar = {
            MaskTopAppBar(
                title = {
                    Text(text = stringResource(R.string.scene_identity_empty_recovery_sign_in))
                },
                navigationIcon = {
                    MaskBackButton(
                        onBack = onBack,
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(ScaffoldPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ItemButton(
                icon = R.drawable.ic_recovery_identity,
                text = stringResource(R.string.scene_identity_mnemonic_import_title),
                secondaryText = "Recovering your persona.",
                onClick = onIdentity,
            )
            ItemButton(
                icon = R.drawable.ic_recovery_private_key,
                text = stringResource(R.string.scene_identity_privatekey_import_title),
                secondaryText = "Recovering your persona.",
                onClick = onPrivateKey,
            )
            ItemButton(
                icon = R.drawable.ic_recovery_local_backup,
                text = stringResource(R.string.scene_identity_recovery_local_backup_recovery_button),
                secondaryText = "Recovering your personas and wallets (if backed up).",
                onClick = onLocalBackup,
            )
            ItemButton(
                icon = R.drawable.ic_recovery_icloud_backup,
                text = stringResource(R.string.scene_identity_recovery_cloud_backup_recovery_button_title),
                secondaryText = "Recovering your personas and wallets (if backed up) with Email or phone number.",
                onClick = onRemoteBackup,
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ItemButton(
    @DrawableRes icon: Int,
    text: String,
    secondaryText: String,
    onClick: () -> Unit,
) {
    MaskButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        MaskListItem(
            icon = {
                Image(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(38.dp),
                )
            },
            text = {
                Text(text = text)
            },
            secondaryText = {
                Text(
                    text = secondaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            trailing = {
                Icon(
                    imageVector = Icons.Filled.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
        )
    }
    Spacer(Modifier.height(16.dp))
}
