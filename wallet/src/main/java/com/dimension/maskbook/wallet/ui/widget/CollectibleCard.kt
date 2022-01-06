package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.dimension.maskbook.wallet.repository.WalletCollectibleData
import com.dimension.maskbook.wallet.repository.WalletCollectibleItemData

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CollectibleCard(
    modifier: Modifier = Modifier,
    data: WalletCollectibleData,
    onItemClicked: (WalletCollectibleItemData) -> Unit,
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }
    Box(
        modifier = Modifier
            .padding(horizontal = 22.dp, vertical = 8.dp)
            .animateContentSize()
            .then(modifier),
    ) {
        MaskCard(
            modifier = Modifier.clickable {
                expanded = !expanded
            },
        ) {
            Column {
                ListItem(
                    icon = {
                        if (data.icon != null) {
                            Image(
                                painter = rememberImagePainter(data.icon),
                                contentDescription = null
                            )
                        }
                    },
                    text = {
                        Text(
                            text = data.name,
                        )
                    },
                    trailing = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = data.items.size.toString()
                            )
                            Icon(Icons.Default.ArrowRight, contentDescription = null)
                        }
                    }
                )
                if (expanded) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(12.dp),
                    ) {
                        items(data.items) {
                            if (it.imageUrl != null) {
                                Image(
                                    painter = rememberImagePainter(it.imageUrl),
                                    modifier = Modifier
                                        .size(145.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            onItemClicked.invoke(it)
                                        },
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}