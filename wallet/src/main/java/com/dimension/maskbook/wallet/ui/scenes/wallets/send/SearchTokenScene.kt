package com.dimension.maskbook.wallet.ui.scenes.wallets.send

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.humanizeDollar
import com.dimension.maskbook.wallet.ext.humanizeToken
import com.dimension.maskbook.wallet.repository.TokenData
import com.dimension.maskbook.wallet.repository.WalletTokenData
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.*
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

                LazyColumn {
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
    MaskListCardItem(
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
