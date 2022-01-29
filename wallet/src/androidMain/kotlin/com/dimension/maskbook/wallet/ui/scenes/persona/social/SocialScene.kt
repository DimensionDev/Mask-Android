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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.dimension.maskbook.wallet.repository.Network
import com.dimension.maskbook.wallet.repository.SocialData
import com.dimension.maskbook.wallet.repository.icon
import com.dimension.maskbook.wallet.ui.widget.MaskGridButton
import com.dimension.maskbook.wallet.ui.widget.NameImage
import com.dimension.maskbook.wallet.ui.widget.itemsGridIndexed

private val addIcon = SocialData(
    id = "",
    name = "",
    avatar = "",
    personaId = null,
    network = Network.Twitter,
)

@Composable
fun SocialScene(
    socialList: List<SocialData>,
    onAddSocialClick: () -> Unit,
    onItemClick: (SocialData, isEditing: Boolean) -> Unit,
) {
    var isEditing by rememberSaveable { mutableStateOf(false) }
    val finalSocialList = remember(socialList) {
        listOf(addIcon) + socialList
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 22.5f.dp, vertical = 25.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Accounts",
                    style = MaterialTheme.typography.h5,
                )
                TextButton(
                    onClick = {
                        isEditing = !isEditing
                    }
                ) {
                    Text(
                        text = if (isEditing) {
                            SocialScreenDefaults.done
                        } else {
                            SocialScreenDefaults.edit
                        },
                        color = MaterialTheme.colors.primary,
                    )
                }
            }
        }
        itemsGridIndexed(
            data = finalSocialList,
            rowSize = 3
        ) { _, item ->
            if (item === addIcon) {
                AddIcon(
                    enabled = !isEditing,
                    onClick = onAddSocialClick
                )
            } else {
                SocialItem(
                    item = item,
                    onItemClick = {
                        onItemClick(item, isEditing)
                    },
                    isEditing = isEditing,
                )
            }
        }
    }
}

@Composable
private fun AddIcon(
    enabled: Boolean,
    onClick: () -> Unit
) {
    MaskGridButton(
        enabled = enabled,
        onClick = onClick,
        modifier = Modifier
            .size(
                width = SocialScreenDefaults.itemWidth,
                height = SocialScreenDefaults.itemHeight,
            ),
        icon = {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                modifier = Modifier
                    .size(SocialScreenDefaults.itemIconSize)
                    .shadow(if (enabled) 6.dp else 0.dp, shape = CircleShape, clip = false)
                    .background(MaterialTheme.colors.surface, shape = CircleShape)
                    .padding(10.dp),
            )
        },
        text = {
            Text(
                text = SocialScreenDefaults.add,
                style = MaterialTheme.typography.subtitle2,
                color = LocalContentColor.current.copy(LocalContentAlpha.current),
            )
        }
    )
}

@Composable
private fun SocialItem(
    item: SocialData,
    onItemClick: () -> Unit,
    isEditing: Boolean,
) {
    MaskGridButton(
        onClick = onItemClick,
        modifier = Modifier
            .size(
                width = SocialScreenDefaults.itemWidth,
                height = SocialScreenDefaults.itemHeight,
            ),
        icon = {
            Box {
                AvatarImage(item.avatar, item.name)
                Image(
                    painter = painterResource(item.network.icon),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(SocialScreenDefaults.itemSmallIconSize)
                        .border(1.dp, MaterialTheme.colors.background, shape = CircleShape)
                        .clip(shape = CircleShape),
                )
                if (isEditing) {
                    Icon(
                        imageVector = Icons.Rounded.Remove,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .size(SocialScreenDefaults.itemSmallIconSize)
                            .background(SocialScreenDefaults.deleteColor, shape = CircleShape),
                    )
                }
            }
        },
        text = {
            Text(
                text = item.name,
                style = MaterialTheme.typography.subtitle2,
                color = LocalContentColor.current.copy(LocalContentAlpha.current),
            )
        }
    )
}

@Composable
private fun AvatarImage(avatar: String, name: String) {
    if (avatar.isNotEmpty()) {
        Image(
            painter = rememberImagePainter(avatar),
            contentDescription = null,
            modifier = Modifier
                .size(SocialScreenDefaults.itemIconSize)
                .clip(CircleShape),
            alpha = LocalContentAlpha.current,
        )
    } else {
        NameImage(
            name = name,
            style = MaterialTheme.typography.h4,
            modifier = Modifier.size(SocialScreenDefaults.itemIconSize),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddIconPreview() {
    Row {
        AddIcon(
            enabled = true,
            onClick = {}
        )
        Spacer(Modifier.width(10.dp))
        AddIcon(
            enabled = false,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SocialItemPreview() {
    Row {
        SocialItem(
            item = SocialData(
                id = "",
                name = "AAA",
                avatar = "",
                personaId = null,
                network = Network.Twitter,
            ),
            onItemClick = {},
            isEditing = false,
        )
        Spacer(Modifier.width(10.dp))
        SocialItem(
            item = SocialData(
                id = "",
                name = "AAA",
                avatar = "",
                personaId = null,
                network = Network.Twitter,
            ),
            onItemClick = {},
            isEditing = true,
        )
    }
}

private object SocialScreenDefaults {
    val itemWidth = 100.dp
    val itemHeight = 115.dp
    val itemIconSize = 64.dp
    val itemSmallIconSize = 20.dp

    val deleteColor = Color(0xFFFF5F5F)

    const val add = "Add"
    const val edit = "Edit"
    const val done = "Done"
}
