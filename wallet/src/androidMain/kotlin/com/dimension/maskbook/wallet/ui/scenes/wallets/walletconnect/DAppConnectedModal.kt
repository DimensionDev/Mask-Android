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
package com.dimension.maskbook.wallet.ui.scenes.wallets.walletconnect

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.dimension.maskbook.common.ui.widget.HorizontalScenePadding
import com.dimension.maskbook.common.ui.widget.MaskListItem
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.common.ui.widget.button.MaskTextButton
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.walletconnect.WCClientMeta

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DAppConnectedModal(
    apps: List<WCClientMeta>,
    onDisconnect: (client: WCClientMeta) -> Unit,
) {
    MaskModal {
        MaskScaffold(
            modifier = Modifier.fillMaxHeight(0.8f),
            topBar = {
                Column(
                    modifier = Modifier
                        .shadow(4.dp)
                        .background(color = MaterialTheme.colors.background),
                ) {
                    MaskSingleLineTopAppBar(
                        title = {
                            Text("Connected Apps")
                        },
                    )
                }
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(
                    horizontal = HorizontalScenePadding,
                    vertical = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(apps) { app ->
                    MaskListItem(
                        modifier = Modifier.fillMaxWidth(),
                        icon = {
                            Image(rememberImagePainter(app.icons.first()), contentDescription = null)
                        },
                        text = {
                            Text(app.name)
                        },
                        secondaryText = {
                            Text(app.description)
                        },
                        trailing = {
                            MaskTextButton(
                                onClick = {
                                    onDisconnect(app)
                                },
                            ) {
                                Text(
                                    stringResource(R.string.scene_wallet_connect_disconnect),
                                    color = Color.Red,
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}
