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
package com.dimension.maskbook.wallet.ui.scenes.persona.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Pages
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.ui.widget.PrimaryButton
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.persona.viewmodel.post.PostViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostScene() {
    val viewModel = getViewModel<PostViewModel>()
    val items by viewModel.items.observeAsState(initial = emptyList())
    if (!items.any()) {
        EmptyPostScene()
    } else {
        LazyColumn {
            items(items) {
                ListItem(
                    icon = {
                        Icon(Icons.Default.Pages, contentDescription = null)
                    },
                    text = {
                        Text(text = it.title)
                    },
                    trailing = {
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyPostScene() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painterResource(id = R.drawable.ic_group_130),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "You haven’t created any post yet.")
        Spacer(modifier = Modifier.height(24.dp))
        PrimaryButton(onClick = {
        }) {
            Text(text = "Create Post")
        }
    }
}
