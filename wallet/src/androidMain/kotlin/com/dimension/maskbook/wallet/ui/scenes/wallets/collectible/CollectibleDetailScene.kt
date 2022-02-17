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
package com.dimension.maskbook.wallet.ui.scenes.wallets.collectible

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.common.ui.widget.ScaffoldPadding
import com.dimension.maskbook.common.ui.widget.button.MaskBackButton
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.repository.WalletCollectibleData

@Composable
fun CollectibleDetailScene(
    data: WalletCollectibleData,
    onBack: () -> Unit,
    onSend: () -> Unit,
    onReceive: () -> Unit,
) {
    MaskScaffold(
        topBar = {
            MaskSingleLineTopAppBar(
                navigationIcon = {
                    MaskBackButton(
                        onBack = onBack,
                    )
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
            Box(
                modifier = Modifier.weight(1f),
            ) {
                data.items.firstOrNull()?.let { item ->
                    if (!item.videoUrl.isNullOrEmpty()) {
                        // TODO: video
                    } else if (!item.imageUrl.isNullOrEmpty()) {
                        Image(
                            painter = rememberImagePainter(item.imageUrl),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            contentDescription = null
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.mask),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            contentDescription = null
                        )
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = { onSend.invoke() },
                    elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFFFFB915))
                ) {
                    Icon(painterResource(id = R.drawable.upload), contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(R.string.scene_wallet_balance_btn_Send), maxLines = 1)
                }
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = { onReceive.invoke() },
                    elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                ) {
                    Icon(
                        painterResource(id = R.drawable.download),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(R.string.scene_wallet_balance_btn_receive), maxLines = 1)
                }
            }
        }
    }
}
