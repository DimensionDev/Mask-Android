package com.dimension.maskbook.wallet.ui.scenes.wallets.send

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.repository.SearchAddressData
import com.dimension.maskbook.wallet.repository.TokenData
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.MaskBackButton
import com.dimension.maskbook.wallet.ui.widget.MaskIconButton
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding

@Composable
fun SearchAddressScene(
    onBack: () -> Unit,
    tokenData: TokenData,
    query: String,
    onQueryChanged: (String) -> Unit,
//    searchResult: SearchAddressResult?,
    contacts: List<SearchAddressData>,
    recent: List<SearchAddressData>,
//    showLoading: Boolean,
    noTokenFound: Boolean,
    onBuyToken: () -> Unit,
    onScanQrCode: () -> Unit,
    onNext: (String) -> Unit,
) {
    MaskTheme {
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ScaffoldPadding),
            ) {
                SearchInput(
                    value = query,
                    onValueChanged = onQueryChanged,
//                    searchResult = searchResult,
                    onScan = onScanQrCode
                )
                Spacer(modifier = Modifier.height(20.dp))
                EmptyTokenWarning(
                    visible = noTokenFound,
                    tokenName = tokenData.symbol,
                    onBuy = onBuyToken
                )
                Box(modifier = Modifier.weight(1f)) {
                    EmptyInputContent(
                        contacts = contacts,
                        recent = recent,
                        selectItemEnable = !noTokenFound,
                        onItemSelect = { onNext.invoke(it.address) }
                    )
                }
                NextButton(
                    onClick = { onNext.invoke(query) },
                    enable = query.isNotEmpty(),
                )
//                SearchResultContent(
//                    visible = query.isNotEmpty(),
//                    searchResult = searchResult,
//                    loading = showLoading,
//                    selectItemEnable = !noTokenFound,
//                    onItemSelect = { onNext.invoke(it) }
//                )
            }
        }
    }
}

//@OptIn(ExperimentalAnimationApi::class)
//@Composable
//fun SearchResultContent(
//    visible: Boolean,
////    searchResult: SearchAddressResult?,
////    loading: Boolean,
//    selectItemEnable: Boolean,
//    onItemSelect: (SearchAddressData) -> Unit,
//) {
//    AnimatedVisibility(visible = visible) {
//        Column(modifier = Modifier.fillMaxWidth()) {
//            LoadingContent(visible = loading)
//            searchResult?.let { result ->
//                when {
//                    result.success && result.data is MultipleAddressResultData -> {
//                        SearchResultListContent(
//                            contacts = result.data.contacts,
//                            suggestions = result.data.suggestions,
//                            onItemSelect = { if (selectItemEnable) onItemSelect.invoke(it) }
//                        )
//                    }
//                    result.success && result.data is SingleAddressResultData -> {
//                        NextButton(
//                            enable = selectItemEnable,
//                            onClick = { onItemSelect.invoke(result.data.address) }
//                        )
//                    }
//                    else -> {
//                        result.errorMsg?.takeIf { it.isNotEmpty() }?.let {
//                            ErrorContent(
//                                error = it
//                            )
//                        } ?: NoResultContent()
//                    }
//                }
//
//            }
//
//        }
//    }
//}

@Composable
private fun SearchResultListContent(
    contacts: List<SearchAddressData>,
    suggestions: List<SearchAddressData>,
    onItemSelect: (SearchAddressData) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemHeader(icon = R.drawable.ic_profile, title = R.string.scene_backup_restored_contacts)
        items(contacts.size) { index ->
            SearchAddressItem(
                item = contacts[index],
                onClick = onItemSelect
            )
        }
        itemHeader(icon = R.drawable.ic_time_circle, title = "Suggestions")
        items(suggestions.size) { index ->
            SearchAddressItem(
                suggestions[index],
                onClick = onItemSelect
            )
        }
    }
}

@Composable
private fun SearchInput(
    value: String,
    onValueChanged: (String) -> Unit,
    onScan: () -> Unit,
//    searchResult: SearchAddressResult?,
) {
    Text(text = stringResource(R.string.scene_sendTransaction_send_Label_To))
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.surface, shape = MaterialTheme.shapes.small)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        var hideResult by remember {
            mutableStateOf(true)
        }
        Image(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
//        if (searchResult?.data is SingleAddressResultData && !hideResult) {
//            Spacer(modifier = Modifier.width(8.dp))
//            Row(
//                modifier = Modifier
//                    .weight(1f)
//                    .clickable {
//                        hideResult = true
//                    },
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                searchResult.data.address.let {
//                    Text(
//                        text = "${it.name ?: it.ens}(${it.address})",
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                }
//                Image(
//                    painter = painterResource(id = R.drawable.ic_property_1_snccess),
//                    contentDescription = null,
//                    modifier = Modifier.size(16.dp)
//                )
//            }
//        } else {
        TextField(
            value = value,
            onValueChange = {
                onValueChanged.invoke(it)
                hideResult = false
            },
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = {
                Text(
                    text = "Name, Ens or Address(0x...)",
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            singleLine = true
        )
//        }
        Spacer(modifier = Modifier.width(8.dp))
        MaskIconButton(onClick = {
            if (value.isEmpty()) onScan.invoke() else onValueChanged.invoke("")
        }) {
            if (value.isEmpty()) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_scan),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_clear),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun EmptyInputContent(
    contacts: List<SearchAddressData>,
    recent: List<SearchAddressData>,
    selectItemEnable: Boolean,
    onItemSelect: (SearchAddressData) -> Unit
) {
    LazyColumn {
        itemHeader(icon = R.drawable.ic_profile, title = R.string.scene_backup_restored_contacts)
        items(contacts.size) { index ->
            SearchAddressItem(
                item = contacts[index],
                onClick = { if (selectItemEnable) onItemSelect.invoke(it) }
            )
        }
        itemHeader(icon = R.drawable.ic_time_circle, title = "Recent")
        items(recent.size) { index ->
            SearchAddressItem(
                recent[index],
                onClick = { if (selectItemEnable) onItemSelect.invoke(it) }
            )
        }
    }
}

private fun LazyListScope.itemHeader(
    @DrawableRes icon: Int,
    @StringRes title: Int,
) {
    item {
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
                text = stringResource(title),
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

private fun LazyListScope.itemHeader(
    @DrawableRes icon: Int,
    title: String,
) {
    item {
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
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun SearchAddressItem(
    item: SearchAddressData,
    onClick: (SearchAddressData) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick.invoke(item) }
        .padding(vertical = 8.dp)) {
        if (!item.name.isNullOrEmpty() || !item.ens.isNullOrEmpty()) {
            Text(
                text = item.name ?: item.ens ?: "",
                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(2.dp))
        }
        Text(text = if (!item.name.isNullOrEmpty()) item.ens ?: item.address else item.address)
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
    visible: Boolean,
    tokenName: String,
    onBuy: () -> Unit
) {
    AnimatedVisibility(visible = visible) {
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
                    text = "You have 0 $tokenName in your account to pay for transaction fees. Buy some $tokenName or deposit from another account. ",
                    color = Color(255, 185, 21, 255)
                )
                Spacer(modifier = Modifier.height(2.dp))
                TextButton(onClick = onBuy) {
                    Text(
                        text = "Buy $tokenName",
                        color = MaterialTheme.colors.primary,
                        style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.NextButton(
    enable: Boolean,
    onClick: () -> Unit
) {
    PrimaryButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = enable
    ) {
        Text(text = stringResource(R.string.common_controls_next))
    }
}