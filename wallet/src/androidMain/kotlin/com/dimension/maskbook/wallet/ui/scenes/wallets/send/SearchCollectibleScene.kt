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
package com.dimension.maskbook.wallet.ui.scenes.wallets.send

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.dimension.maskbook.common.ui.widget.HorizontalScenePadding
import com.dimension.maskbook.common.ui.widget.MaskInputField
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskScene
import com.dimension.maskbook.common.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.common.ui.widget.ScaffoldPadding
import com.dimension.maskbook.common.ui.widget.button.MaskBackButton
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.export.model.WalletCollectibleCollectionData
import com.dimension.maskbook.wallet.export.model.WalletCollectibleData
import com.dimension.maskbook.wallet.ui.widget.CollectibleCollectionCard

@Composable
fun SearchCollectibleScene(
    onBack: () -> Unit,
    query: String,
    onQueryChanged: (String) -> Unit,
    collections: LazyPagingItems<WalletCollectibleCollectionData>,
    onSelect: (WalletCollectibleData) -> Unit
) {
    MaskScene {
        MaskScaffold(
            topBar = {
                MaskSingleLineTopAppBar(
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    },
                    title = { Text(text = "Search Collectibles") }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                MaskInputField(
                    value = query,
                    onValueChange = onQueryChanged,
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = HorizontalScenePadding),
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    placeholder = {
                        Text(text = "Search Collectible name")
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn(
                    contentPadding = ScaffoldPadding,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(collections) {
                        it?.takeIf { it.name.contains(query, ignoreCase = true) }?.let {
                            CollectibleCollectionCard(
                                data = it,
                                onItemClicked = {
                                    onSelect.invoke(it)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
