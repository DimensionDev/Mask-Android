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
package com.dimension.maskbook.labs.ui.scenes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.ui.widget.MaskListItem
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.common.ui.widget.ScaffoldPadding
import com.dimension.maskbook.common.ui.widget.button.MaskButton
import com.dimension.maskbook.common.ui.widget.button.MaskIconCardButton
import com.dimension.maskbook.labs.R
import com.dimension.maskbook.labs.export.model.AppKey
import com.dimension.maskbook.labs.viewmodel.AppDisplayData
import com.dimension.maskbook.labs.viewmodel.LabsViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun LabsScene(
    onSettingClick: () -> Unit,
    onItemClick: (AppKey) -> Unit,
) {
    val viewModel: LabsViewModel = getViewModel()
    val apps by viewModel.apps.observeAsState(initial = emptyList())
    MaskScaffold(
        topBar = {
            MaskSingleLineTopAppBar(
                title = {
                    Text(text = stringResource(R.string.tab_labs))
                },
                actions = {
                    MaskIconCardButton(onClick = onSettingClick) {
                        Icon(
                            painterResource(id = R.drawable.ic_setting),
                            contentDescription = null,
                        )
                    }
                }
            )
        }
    ) {
        LazyColumn(
            contentPadding = ScaffoldPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(apps) { item ->
                AppItem(
                    item = item,
                    onClick = { onItemClick(item.key) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AppItem(
    item: AppDisplayData,
    onClick: () -> Unit
) {
    MaskButton(
        onClick = onClick,
        enabled = item.enabled,
        contentPadding = PaddingValues(vertical = 6.dp),
    ) {
        MaskListItem(
            modifier = Modifier.alpha(if (item.enabled) 1f else 0.5f),
            icon = {
                Image(
                    painter = painterResource(id = item.onIcon),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                )
            },
            text = {
                Text(
                    text = stringResource(item.name),
                )
            },
            secondaryText = {
                Text(
                    text = stringResource(item.description),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            trailing = {
                Icon(
                    imageVector = Icons.Filled.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
        )
    }
}
