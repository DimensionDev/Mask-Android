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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.dimension.maskbook.common.ui.theme.MaskTheme
import com.dimension.maskbook.common.ui.widget.MaskBackButton
import com.dimension.maskbook.common.ui.widget.MaskButton
import com.dimension.maskbook.common.ui.widget.MaskInputField
import com.dimension.maskbook.common.ui.widget.MaskListItem
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.common.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.export.model.TokenData
import com.dimension.maskbook.wallet.export.model.WalletTokenData
import com.dimension.maskbook.wallet.ext.humanizeDollar
import com.dimension.maskbook.wallet.ext.humanizeToken
import java.math.BigDecimal

@Composable
fun SearchTokenScene(
    onBack: () -> Unit,
    query: String,
    onQueryChanged: (String) -> Unit,
    tokens: List<WalletTokenData>,
    onSelect: (WalletTokenData) -> Unit
) {
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskSingleLineTopAppBar(
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    },
                    title = { Text(text = stringResource(R.string.scene_sendTransaction_tokenList_title)) }
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
                        .padding(horizontal = 23.dp),
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    placeholder = {
                        Text(text = stringResource(R.string.scene_sendTransaction_tokenList_placeholder))
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn(
                    contentPadding = ScaffoldPadding,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(tokens.filter { it.tokenData.name.contains(query, ignoreCase = true) }) {
                        SearchResultItem(
                            modifier = Modifier.clickable {
                                onSelect.invoke(it)
                            },
                            count = it.count,
                            tokenData = it.tokenData,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    modifier: Modifier = Modifier,
    tokenData: TokenData,
    count: BigDecimal,
) {
    MaskButton(onClick = {}) {
        MaskListItem(
            modifier = modifier,
            icon = {
                Image(
                    painter = rememberImagePainter(tokenData.logoURI),
                    contentDescription = null,
                    modifier = Modifier.size(38.dp),
                )
            },
            text = {
                Text(tokenData.name)
            },
            secondaryText = {
                Text(count.humanizeToken())
            },
            trailing = {
                Text((count * tokenData.price).humanizeDollar())
            }
        )
    }
}
