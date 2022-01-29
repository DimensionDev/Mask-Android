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
package com.dimension.maskbook.wallet.ui.scenes.wallets.create.import

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.MaskBackButton
import com.dimension.maskbook.wallet.ui.widget.MaskButton
import com.dimension.maskbook.wallet.ui.widget.MaskListItem
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskTopAppBar
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding

@Composable
fun ImportWalletScene(
    onBack: () -> Unit,
    onMnemonic: () -> Unit,
    onPassword: () -> Unit,
    onKeystore: () -> Unit,
) {
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskTopAppBar(
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    },
                    title = {
                        Text(text = stringResource(R.string.scene_wallet_list_wallets_items_import))
                    }
                )
            }
        ) {
            Column(Modifier.padding(ScaffoldPadding)) {
                ImportItem(
                    icon = R.drawable.ic_wallet_mnemonic,
                    title = stringResource(R.string.scene_wallet_import_item_mnemonic_words),
                    subtitle = stringResource(R.string.scene_wallet_import_item_mnemonic_words_desc),
                    onClick = {
                        onMnemonic.invoke()
                    }
                )
                ImportItem(
                    icon = R.drawable.ic_wallet_password,
                    title = stringResource(R.string.scene_identity_privatekey_import_title),
                    subtitle = stringResource(R.string.scene_wallet_import_item_private_key_desc),
                    onClick = {
                        onPassword.invoke()
                    }
                )
                ImportItem(
                    icon = R.drawable.ic_wallet_keystore,
                    title = stringResource(R.string.scene_wallet_import_item_keystore),
                    subtitle = stringResource(R.string.scene_wallet_import_item_keystore_desc),
                    onClick = {
                        onKeystore.invoke()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ImportItem(
    @DrawableRes icon: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    MaskButton(onClick = onClick) {
        MaskListItem(
            icon = {
                Image(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                )
            },
            text = {
                Text(title)
            },
            secondaryText = {
                Text(subtitle)
            },
            trailing = {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
        )
    }
    Spacer(Modifier.height(16.dp))
}
