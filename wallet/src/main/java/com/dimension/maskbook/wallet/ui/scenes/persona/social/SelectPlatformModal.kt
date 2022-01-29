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
package com.dimension.maskbook.wallet.ui.scenes.persona.social

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.repository.Network
import com.dimension.maskbook.wallet.repository.PlatformType
import com.dimension.maskbook.wallet.repository.icon
import com.dimension.maskbook.wallet.repository.platform
import com.dimension.maskbook.wallet.repository.title
import com.dimension.maskbook.wallet.ui.widget.MaskGridButton
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding

private val items = listOf(
    Network.Twitter,
    Network.Facebook,
)

@Composable
fun SelectPlatformModal(
    onDone: (PlatformType) -> Unit
) {
    MaskModal {
        Column(
            modifier = Modifier.padding(ScaffoldPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Connect Social accounts",
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.height(21.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                items.forEach { item ->
                    MaskGridButton(
                        modifier = Modifier.size(SelectPlatformModalDefaults.itemSize),
                        onClick = {
                            item.platform?.let(onDone)
                        },
                        icon = {
                            Image(
                                painter = painterResource(item.icon),
                                contentDescription = null,
                                modifier = Modifier.size(SelectPlatformModalDefaults.iconSize)
                            )
                        },
                        text = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.subtitle2,
                            )
                        }
                    )
                }
                Spacer(Modifier.width(SelectPlatformModalDefaults.itemSize))
            }
        }
    }
}

private object SelectPlatformModalDefaults {
    val itemSize = 100.dp
    val iconSize = 48.dp
}
