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
package com.dimension.maskbook.persona.ui.scenes.social

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.dimension.maskbook.common.ui.widget.HorizontalScenePadding
import com.dimension.maskbook.common.ui.widget.NameImage
import com.dimension.maskbook.common.ui.widget.SingleLineText
import com.dimension.maskbook.common.ui.widget.button.MaskGridButton
import com.dimension.maskbook.common.ui.widget.itemsGridIndexed
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.persona.export.model.Network
import com.dimension.maskbook.persona.export.model.SocialData
import com.dimension.maskbook.persona.model.icon

private const val shakeDegrees = 5f
private val shakeDegreesEasing = CubicBezierEasing(1f, 0.5f, 1f, 0.5f)

@Composable
fun SocialScene(
    isEditing: Boolean,
    setIsEditing: (Boolean) -> Unit,
    socialList: List<SocialData?>,
    onAddSocialClick: () -> Unit,
    onItemClick: (SocialData, isEditing: Boolean) -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val shakingAngle: Float = if (isEditing) {
        infiniteTransition.animateFloat(
            initialValue = -shakeDegrees,
            targetValue = shakeDegrees,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 100, easing = shakeDegreesEasing),
                repeatMode = RepeatMode.Reverse,
            )
        ).value
    } else {
        0f
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = HorizontalScenePadding),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.scene_persona_social_accounts),
                    style = MaterialTheme.typography.h5,
                )
                IconButton(
                    onClick = {
                        setIsEditing(!isEditing)
                    }
                ) {
                    Image(
                        painter = painterResource(
                            if (isEditing) {
                                R.drawable.ic_edit_social_finish
                            } else {
                                R.drawable.ic_edit_social
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
        itemsGridIndexed(
            data = socialList,
            rowSize = 3,
        ) { _, item ->
            if (item == null) {
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
                    shakingAngleBubble = shakingAngle,
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
            Image(
                painter = painterResource(if (enabled) R.drawable.ic_add_social else R.drawable.ic_add_social_closed),
                contentDescription = null,
                modifier = Modifier
                    .size(SocialScreenDefaults.itemIconSize),
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
    shakingAngleBubble: Float = 0f,
    isEditing: Boolean,
) {
    MaskGridButton(
        onClick = onItemClick,
        modifier = Modifier
            .graphicsLayer {
                rotationZ = shakingAngleBubble
            }
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
            SingleLineText(
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
