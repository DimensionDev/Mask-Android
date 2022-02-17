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

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ui.widget.MaskInputField
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskScene
import com.dimension.maskbook.common.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.common.ui.widget.ScaffoldPadding
import com.dimension.maskbook.common.ui.widget.button.MaskBackButton
import com.dimension.maskbook.common.ui.widget.button.MaskButton
import com.dimension.maskbook.common.ui.widget.button.MaskIconButton
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.common.ui.widget.button.clickable
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.repository.SearchAddressData
import com.dimension.maskbook.wallet.viewmodel.wallets.send.EnsData

@Composable
fun SearchAddressScene(
    onBack: () -> Unit,
    tokenAddress: String,
    query: String,
    canConfirm: Boolean,
    ensData: EnsData?,
    selectEnsData: EnsData.Success?,
    onEnsDataClick: (EnsData.Success) -> Unit,
    onQueryChanged: (String) -> Unit,
    contacts: List<SearchAddressData>,
    recent: List<SearchAddressData>,
    noTokenFound: Boolean,
    onBuyToken: () -> Unit,
    onScanQrCode: () -> Unit,
    onSearch: () -> Unit,
    onCopy: (String) -> Unit,
    onClear: () -> Unit,
    onItemSelect: (SearchAddressData) -> Unit,
    onNext: () -> Unit,
) {
    MaskScene {
        MaskScaffold(
            topBar = {
                MaskSingleLineTopAppBar(
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    },
                    title = { Text(text = stringResource(R.string.scene_wallet_balance_btn_Send)) }
                )
            }
        ) {
            if (noTokenFound) {
                EmptyTokenWarning(
                    tokenName = tokenAddress,
                    onBuy = onBuyToken
                )
                return@MaskScaffold
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ScaffoldPadding),
            ) {
                Text(text = stringResource(R.string.scene_sendTransaction_send_Label_To))
                Spacer(modifier = Modifier.height(10.dp))
                SearchInput(
                    value = query,
                    ensData = ensData,
                    readOnly = selectEnsData != null,
                    onValueChanged = onQueryChanged,
                    onScanQrCode = onScanQrCode,
                    onClear = onClear,
                    onSearch = onSearch,
                )
                Spacer(modifier = Modifier.height(10.dp))
                AnimatedVisibility(ensData is EnsData.Success) {
                    if (ensData is EnsData.Success) {
                        EnsCard(
                            ensData = ensData,
                            onClick = { onEnsDataClick(ensData) },
                            onCopy = { onCopy(ensData.address) },
                        )
                    }
                }
                AnimatedVisibility(ensData is EnsData.Failure) {
                    if (ensData is EnsData.Failure) {
                        Text(
                            text = ensData.exception.localizedMessage.orEmpty(),
                            color = Color.Red,
                            style = MaterialTheme.typography.caption,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.weight(1f)) {
                    EmptyInputContent(
                        contacts = contacts,
                        recent = recent,
                        onItemSelect = onItemSelect,
                    )
                }
                PrimaryButton(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canConfirm,
                ) {
                    Text(text = stringResource(R.string.common_controls_next))
                }
            }
        }
    }
}

@Composable
private fun SearchInput(
    value: String,
    ensData: EnsData?,
    readOnly: Boolean,
    onValueChanged: (String) -> Unit,
    onScanQrCode: () -> Unit,
    onClear: () -> Unit,
    onSearch: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.surface, shape = MaterialTheme.shapes.small)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        MaskInputField(
            value = value,
            onValueChange = onValueChanged,
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = {
                Text(
                    text = stringResource(R.string.scene_wallet_send_address_search_address),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            readOnly = readOnly,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch()
                }
            ),
        )
        if (ensData != null) {
            Spacer(Modifier.width(8.dp))
            ProgressButton(ensData)
        }
        Spacer(Modifier.width(8.dp))
        if (value.isEmpty()) {
            MaskIconButton(onClick = onScanQrCode) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_scan),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            }
        } else {
            MaskIconButton(onClick = onClear) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_clear),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun ProgressButton(ensData: EnsData) {
    Box(Modifier.size(24.dp), Alignment.Center) {
        when (ensData) {
            EnsData.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                )
            }
            is EnsData.Success -> {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    tint = Color(0xFF1FB885),
                    modifier = Modifier.size(20.dp)
                )
            }
            is EnsData.Failure -> {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun EnsCard(
    ensData: EnsData.Success,
    onClick: () -> Unit,
    onCopy: () -> Unit,
) {
    MaskButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(12.dp),
    ) {
        Column {
            Text(
                text = ensData.name,
                style = MaterialTheme.typography.h5,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = ensData.address,
                style = MaterialTheme.typography.body2,
            )
        }
        Spacer(Modifier.width(20.dp))
        MaskIconButton(onClick = onCopy) {
            Icon(
                painter = painterResource(R.drawable.ic_copy_ens),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun EmptyInputContent(
    contacts: List<SearchAddressData>,
    recent: List<SearchAddressData>,
    onItemSelect: (SearchAddressData) -> Unit
) {
    LazyColumn {
        item {
            ItemHeader(
                icon = R.drawable.ic_profile,
                title = stringResource(R.string.scene_backup_restored_contacts),
            )
        }
        items(contacts.size) { index ->
            SearchAddressItem(
                item = contacts[index],
                onClick = { onItemSelect.invoke(it) }
            )
        }
        item {
            ItemHeader(
                icon = R.drawable.ic_time_circle,
                title = stringResource(R.string.scene_wallet_send_address_recent),
            )
        }
        items(recent.size) { index ->
            SearchAddressItem(
                recent[index],
                onClick = { onItemSelect.invoke(it) }
            )
        }
    }
}

@Composable
private fun ItemHeader(
    @DrawableRes icon: Int,
    title: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.h4,
        )
    }
}

@Composable
private fun SearchAddressItem(
    item: SearchAddressData,
    onClick: (SearchAddressData) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick.invoke(item) }
            .padding(vertical = 8.dp)
    ) {
        if (!item.name.isNullOrEmpty() || !item.ens.isNullOrEmpty()) {
            Text(
                text = item.name ?: item.ens ?: "",
                style = MaterialTheme.typography.h5,
            )
            Spacer(modifier = Modifier.height(2.dp))
        }
        Text(
            text = if (!item.name.isNullOrEmpty()) {
                item.ens ?: item.address
            } else item.address,
            style = MaterialTheme.typography.body2,
        )
        Spacer(Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ColumnScope.LoadingContent(
    visible: Boolean
) {
    AnimatedVisibility(visible = visible) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun NoResultContent() {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(100.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_search_big),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "No Results",
            style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "The address is not found")
    }
}

@Composable
private fun ErrorContent(
    error: String
) {
    Text(text = error, color = MaterialTheme.colors.error)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun EmptyTokenWarning(
    tokenName: String,
    onBuy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(255, 185, 21, 38), shape = MaterialTheme.shapes.small)
            .padding(vertical = 10.dp, horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_property_1_note),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = stringResource(R.string.scene_wallet_send_address_empty_token_warn, tokenName, tokenName),
                color = Color(255, 185, 21, 255)
            )
            Spacer(modifier = Modifier.height(2.dp))
            TextButton(onClick = onBuy) {
                Text(
                    text = stringResource(R.string.scene_wallet_send_address_by_token, tokenName),
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
