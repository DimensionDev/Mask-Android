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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.rememberImagePainter
import com.dimension.maskbook.common.ext.onDrawableRes
import com.dimension.maskbook.common.ui.widget.MaskListItem
import com.dimension.maskbook.common.ui.widget.button.MaskButton
import com.dimension.maskbook.common.ui.widget.button.MaskTransparentButton
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.export.model.WalletCollectibleCollectionData
import com.dimension.maskbook.wallet.export.model.WalletCollectibleData
import com.dimension.maskbook.wallet.viewmodel.wallets.collectible.CollectiblesViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CollectibleCollectionCard(
    modifier: Modifier = Modifier,
    data: WalletCollectibleCollectionData,
    onItemClicked: (WalletCollectibleData) -> Unit,
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }
    val viewModel = getViewModel<CollectiblesViewModel>()
    val items = viewModel.getCollectibles(collectionSlug = data.slug).collectAsLazyPagingItems()
    MaskButton(
        onClick = { expanded = !expanded },
        modifier = modifier
    ) {
        Column {
            MaskListItem(
                icon = {
                    Box {
                        Image(
                            painter = rememberImagePainter(data.imageUrl) {
                                placeholder(R.drawable.mask)
                                fallback(R.drawable.mask)
                                error(R.drawable.mask)
                            },
                            contentDescription = null,
                            modifier = Modifier.size(38.dp),
                        )
                        Image(
                            painter = rememberImagePainter(data = data.chainType.onDrawableRes),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp).align(Alignment.BottomEnd)
                                .border(1.dp, MaterialTheme.colors.background, shape = CircleShape)
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
                        Text(text = items.itemCount.toString())
                        Icon(
                            imageVector = if (expanded) {
                                Icons.Default.ExpandMore
                            } else {
                                Icons.Default.ChevronRight
                            },
                            contentDescription = null
                        )
                    }
                }
            )
            AnimatedVisibility(expanded) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(12.dp),
                ) {
                    items(items) {
                        it?.let { data ->
                            MaskTransparentButton(onClick = { onItemClicked(data) }) {
                                Image(
                                    painter = rememberImagePainter(data.previewUrl) {
                                        placeholder(R.drawable.mask)
                                        fallback(R.drawable.mask)
                                        error(R.drawable.mask)
                                    },
                                    modifier = Modifier
                                        .size(145.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
