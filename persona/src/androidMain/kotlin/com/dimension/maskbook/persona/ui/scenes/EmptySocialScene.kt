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
package com.dimension.maskbook.persona.ui.scenes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ui.widget.MaskGridButton
import com.dimension.maskbook.common.ui.widget.MaskIconButton
import com.dimension.maskbook.common.ui.widget.itemsGridIndexed
import com.dimension.maskbook.persona.export.model.Network
import com.dimension.maskbook.persona.model.icon
import com.dimension.maskbook.persona.model.title

private class ConnectData(
    val enable: Boolean,
    val network: Network,
)

private val showList = listOf(
    ConnectData(
        enable = true,
        network = Network.Twitter,
    ),
    ConnectData(
        enable = true,
        network = Network.Facebook,
    ),
    ConnectData(
        enable = false,
        network = Network.Instagram,
    ),
    ConnectData(
        enable = false,
        network = Network.Minds,
    )
)

@Composable
fun EmptySocialScene(
    onItemClick: (Network) -> Unit
) {
    var isShowTipDialog by rememberSaveable { mutableStateOf(true) }
    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(
                horizontal = PersonaEmptySceneDefaults.contentHorizontalPadding,
                vertical = PersonaEmptySceneDefaults.contentVerticalPadding,
            ),
            verticalArrangement = Arrangement.spacedBy(PersonaEmptySceneDefaults.itemPadding),
        ) {
            item {
                Text(
                    text = PersonaEmptySceneDefaults.body,
                    style = MaterialTheme.typography.h5,
                )
            }
            itemsGridIndexed(
                data = showList,
                spacing = PersonaEmptySceneDefaults.itemPadding,
                rowSize = 3,
            ) { _, item ->
                ConnectItem(
                    item = item,
                    onItemClick = { onItemClick(item.network) }
                )
            }
        }
        if (isShowTipDialog) {
            TipDialog(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClose = {
                    isShowTipDialog = false
                }
            )
        }
    }
}

@Composable
private fun ConnectItem(
    item: ConnectData,
    onItemClick: () -> Unit
) {
    MaskGridButton(
        enabled = item.enable,
        onClick = onItemClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(PersonaEmptySceneDefaults.itemHeight),
        icon = {
            Image(
                painter = painterResource(item.network.icon),
                contentDescription = null,
                modifier = Modifier.size(PersonaEmptySceneDefaults.itemIconSize),
                alpha = LocalContentAlpha.current,
            )
        },
        text = {
            Text(
                text = item.network.title,
                style = MaterialTheme.typography.subtitle2,
                color = LocalContentColor.current.copy(LocalContentAlpha.current),
            )
        }
    )
}

@Composable
private fun TipDialog(
    modifier: Modifier,
    onClose: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = 22.dp, vertical = 44.dp)
            .shadow(12.dp, MaterialTheme.shapes.medium)
            .background(
                brush = PersonaEmptySceneDefaults.tipDialogBackGround,
                shape = MaterialTheme.shapes.medium
            )
            .padding(start = 16.dp, end = 0.dp, top = 10.dp, bottom = 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = PersonaEmptySceneDefaults.tipMessage,
            color = Color.White,
            modifier = Modifier.weight(1f),
        )
        MaskIconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                tint = Color.White,
            )
        }
    }
}

private object PersonaEmptySceneDefaults {
    const val body = "Connect Social accounts"
    const val tipMessage = "Select the social account to connect your persona. Enjoy a web 3.0 social expereince."

    val contentHorizontalPadding = 22.5f.dp
    val contentVerticalPadding = 16.dp
    val itemPadding = 8.dp
    val itemHeight = 100.dp
    val itemIconSize = 48.dp

    val tipDialogBackGround = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1C68F3),
            Color(0xFF499DFF)
        ),
    )
}
