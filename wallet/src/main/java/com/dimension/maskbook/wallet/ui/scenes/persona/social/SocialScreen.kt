package com.dimension.maskbook.wallet.ui.scenes.persona.social

import androidx.annotation.StringRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.dimension.maskbook.wallet.repository.Network
import com.dimension.maskbook.wallet.repository.SocialData
import com.dimension.maskbook.wallet.repository.icon
import com.dimension.maskbook.wallet.ui.widget.MaskGridButton
import com.dimension.maskbook.wallet.ui.widget.itemsGridIndexed

private val addIcon = SocialData(
    id = "",
    name = "",
    avatar = "",
    personaId = null,
    network = Network.Twitter,
)

@Composable
fun SocialScreen(
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
                Image(
                    painter = rememberImagePainter(item.avatar),
                    contentDescription = null,
                    modifier = Modifier
                        .size(SocialScreenDefaults.itemIconSize)
                        .background(Color.Gray, shape = CircleShape), // TODO load avatar
                    alpha = LocalContentAlpha.current,
                )
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
