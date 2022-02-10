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
package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R

@Composable
fun WalletConnectFloatingButton(
    count: String,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(
        topStartPercent = 100,
        bottomStartPercent = 100,
        bottomEndPercent = 0,
        topEndPercent = 0
    )
    Card(
        modifier = Modifier
            .clip(cardShape)
            .clickable { onClick.invoke() },
        shape = cardShape,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Box(
            modifier = Modifier.padding(WalletConnectFloatingButtonDefaults.ContentPadding)
                .width(WalletConnectFloatingButtonDefaults.BoxWidth)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_walletconnect),
                contentDescription = "logo",
                modifier = Modifier.size(WalletConnectFloatingButtonDefaults.IconSize)
            )
            Box(
                modifier = Modifier.size(WalletConnectFloatingButtonDefaults.CountSize)
                    .background(
                        color = MaterialTheme.colors.surface,
                        shape = CircleShape
                    )
                    .padding(1.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(
                            color = MaterialTheme.colors.primary,
                            shape = CircleShape
                        )
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colors.onPrimary,
                        text = count,
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        }
    }
}

private object WalletConnectFloatingButtonDefaults {
    val ContentPadding = 8.dp
    val IconSize = 40.dp
    val CountSize = 20.dp
    val BoxWidth = IconSize + CountSize / 2
}
