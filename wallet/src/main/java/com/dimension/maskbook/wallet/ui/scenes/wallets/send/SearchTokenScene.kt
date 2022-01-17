package com.dimension.maskbook.wallet.ui.scenes.wallets.send

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ContentAlpha
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.MaskBackButton
import com.dimension.maskbook.wallet.ui.widget.MaskInputField
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding

@Composable
fun SearchTokenScene(
    onBack: () -> Unit,
    query: String,
    onQueryChanged: (String) -> Unit,
    /*TODO LOGIC:
    * results: List<?>
    *  onAdded:(item) -> Unit
    *  onSelected: (item) -> Unit
    * */
) {
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskSingleLineTopAppBar(
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    },
                    title = { Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_sendTransaction_tokenList_title)) }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ScaffoldPadding),
            ) {
                MaskInputField(
                    value = query,
                    onValueChange = onQueryChanged,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    placeholder = {
                        Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_sendTransaction_tokenList_placeholder))
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn {
                    /*TODO replace this item to items*/
                    item {
                        SearchResultItem(
                            logo = "https://downloads.coindesk.com/arc-hosted-images/eth.png",
                            text = "ETH/Ethereum",
                            isAdded = false,
                            count = 1.001f,
                            onAdded = { /*TODO*/ },
                            onSelect = {/*TODO*/ }
                        )
                    }
                }
            }
        }
    }
}

/*TODO Logic:after confirm the result model, use this layout in lazy column*/
@Composable
private fun SearchResultItem(
    logo: String,
    text: String,
    secondaryText: String? = null,
    isAdded: Boolean,
    count: Float,
    onAdded: () -> Unit,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = isAdded,
                onClick = {
                    onSelect.invoke()
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = rememberImagePainter(logo),
            contentDescription = null,
            modifier = Modifier.size(38.dp),
            alpha = if (!isAdded) ContentAlpha.disabled else 1f
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = text, color = MaterialTheme.colors.onSurface.copy(
                    if (!isAdded) ContentAlpha.disabled else 1f
                )
            )
            if (!secondaryText.isNullOrEmpty()) {
                Text(
                    text = secondaryText, color = MaterialTheme.colors.onSurface.copy(
                        if (!isAdded) ContentAlpha.disabled else 1f
                    ),
                    style = MaterialTheme.typography.body2
                )
            }
        }
        if (isAdded) {
            Text(text = count.toString())
        } else {
            IconButton(onClick = onAdded) {
                Image(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
