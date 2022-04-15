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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ui.widget.HorizontalScenePadding
import com.dimension.maskbook.common.ui.widget.SinglelineText
import com.dimension.maskbook.common.ui.widget.button.MaskGridButton
import com.dimension.maskbook.common.ui.widget.itemsGridIndexed
import com.dimension.maskbook.persona.R
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
    LazyColumn(
        contentPadding = PaddingValues(horizontal = HorizontalScenePadding),
    ) {
        item {
            Text(
                text = stringResource(R.string.scene_persona_empty_connect_social),
                style = MaterialTheme.typography.h2,
            )
        }
        item {
            Spacer(Modifier.height(PersonaEmptySceneDefaults.itemPadding))
        }
        item {
            Text(
                text = stringResource(R.string.scene_persona_empty_message_tips),
                style = MaterialTheme.typography.subtitle2,
            )
        }
        item {
            Spacer(Modifier.height(PersonaEmptySceneDefaults.itemPadding))
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
            .fillMaxWidth(),
        icon = {
            Image(
                painter = painterResource(item.network.icon),
                contentDescription = null,
                modifier = Modifier.size(PersonaEmptySceneDefaults.itemIconSize),
                alpha = LocalContentAlpha.current,
            )
        },
        text = {
            SinglelineText(
                text = item.network.title,
                style = MaterialTheme.typography.subtitle2,
                color = LocalContentColor.current.copy(LocalContentAlpha.current),
            )
        }
    )
}

private object PersonaEmptySceneDefaults {
    val contentHorizontalPadding = 22.5f.dp
    val contentVerticalPadding = 16.dp
    val itemPadding = 8.dp
    val itemIconSize = 64.dp
}
