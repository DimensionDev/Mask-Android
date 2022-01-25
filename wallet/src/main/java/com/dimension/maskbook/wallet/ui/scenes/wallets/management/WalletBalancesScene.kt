package com.dimension.maskbook.wallet.ui.scenes.wallets.management

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.humanizeDollar
import com.dimension.maskbook.wallet.ext.humanizeToken
import com.dimension.maskbook.wallet.repository.ChainType
import com.dimension.maskbook.wallet.repository.TokenData
import com.dimension.maskbook.wallet.repository.WalletCollectibleData
import com.dimension.maskbook.wallet.repository.WalletCollectibleItemData
import com.dimension.maskbook.wallet.repository.WalletData
import com.dimension.maskbook.wallet.repository.WalletTokenData
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.CollectibleCard
import com.dimension.maskbook.wallet.ui.widget.MaskButton
import com.dimension.maskbook.wallet.ui.widget.MaskIconCardButton
import com.dimension.maskbook.wallet.ui.widget.MaskListItem
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.wallet.ui.widget.WalletCard
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState

enum class BalancesSceneType {
    Token,
    Collectible
}

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalCoilApi::class, ExperimentalFoundationApi::class,
    ExperimentalPagerApi::class
)
@Composable
fun WalletBalancesScene(
    wallets: List<WalletData>,
    currentWallet: WalletData,
    showTokens: List<WalletTokenData>,
    onWalletChanged: (WalletData) -> Unit,
    onWalletMenuClicked: () -> Unit,
    onWalletSwitchClicked: () -> Unit,
    onTokenDetailClicked: (TokenData) -> Unit,
    onReceiveClicked: () -> Unit,
    onSendClicked: () -> Unit,
    sceneType: BalancesSceneType,
    onSceneTypeChanged: (BalancesSceneType) -> Unit,
    walletChainType: ChainType,
    onCollectibleDetailClicked: (WalletCollectibleItemData) -> Unit,
    onBack: () -> Unit,
    displayChainType: ChainType?,
    onDisplayChainTypeClicked: (ChainType?) -> Unit,
    onWalletAddressClicked: () -> Unit,
    collectible: LazyPagingItems<WalletCollectibleData>,
) {
    val pagerState = rememberPagerState(initialPage = maxOf(wallets.indexOf(currentWallet), 0))

    LaunchedEffect(wallets, currentWallet, pagerState.pageCount) {
        if (pagerState.pageCount > 0) {
            pagerState.scrollToPage(maxOf(wallets.indexOf(currentWallet), 0))
        }
    }
    LaunchedEffect(pagerState.currentPage) {
        if (wallets.isNotEmpty()) {
            onWalletChanged.invoke(wallets[pagerState.currentPage])
        }
    }

    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskSingleLineTopAppBar(
                    navigationIcon = {
                        MaskIconCardButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.scan),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    },
                    actions = {
                        MaskIconCardButton(onClick = onWalletSwitchClicked) {
                            Icon(
                                painter = painterResource(id = R.drawable.wallet),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    }
                )
            }
        ) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    WalletCard(
                        wallets = wallets,
                        walletChainType = walletChainType,
                        displayChainType = displayChainType,
                        onWalletAddressClick = onWalletAddressClicked,
                        onDisplayChainTypeClick = onDisplayChainTypeClicked,
                        onMoreClick = onWalletMenuClicked,
                        pagerState = pagerState,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        WalletButton(
                            text = stringResource(R.string.scene_wallet_balance_btn_Send),
                            icon = R.drawable.transaction_1,
                            onClick = onSendClicked,
                        )
                        Spacer(Modifier.width(12.dp))
                        WalletButton(
                            text = stringResource(R.string.scene_wallet_balance_btn_receive),
                            icon = R.drawable.transaction_2,
                            onClick = onReceiveClicked,
                        )
                    }
                }
                stickyHeader {
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colors.background)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CompositionLocalProvider(
                            LocalTextStyle provides LocalTextStyle.current.copy(color = Color.Unspecified)
                        ) {
                            ScrollableTabRow(
                                modifier = Modifier.weight(1f),
                                selectedTabIndex = BalancesSceneType.values().indexOf(sceneType),
                                backgroundColor = Color.Transparent,
                                divider = { },
                                edgePadding = 0.dp,
                                indicator = { tabPositions ->
                                    Box(
                                        Modifier
                                            .tabIndicatorOffset(
                                                tabPositions[BalancesSceneType
                                                    .values()
                                                    .indexOf(sceneType)]
                                            )
                                            .height(3.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .fillMaxWidth(0.2f)
                                                .fillMaxHeight()
                                                .background(
                                                    color = MaterialTheme.colors.primary,
                                                    shape = CircleShape,
                                                )
                                        )
                                    }
                                },
                            ) {
                                BalancesSceneType.values().forEachIndexed { _, type ->
                                    Tab(
                                        text = { Text(type.name) },
                                        selected = sceneType == type,
                                        onClick = {
                                            onSceneTypeChanged(type)
                                        },
                                        selectedContentColor = MaterialTheme.colors.onBackground,
                                        unselectedContentColor = MaterialTheme.colors.onBackground.copy(
                                            alpha = ContentAlpha.medium
                                        ),
                                    )
                                }
                            }
                        }
                        TextButton(onClick = { /*TODO*/ }) {
                            Text(text = stringResource(R.string.scene_wallet_derivation_path_operation_add))
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = LocalTextStyle.current.color,
                            )
                        }
                    }
                }
                when (sceneType) {
                    BalancesSceneType.Token -> {
                        items(showTokens) {
                            val tokenData = it.tokenData
                            MaskButton(onClick = { onTokenDetailClicked(it.tokenData) }) {
                                MaskListItem(
                                    text = {
                                        Text(text = tokenData.name)
                                    },
                                    secondaryText = {
                                        Text(text = it.count.humanizeToken() + " ${tokenData.symbol}")
                                    },
                                    trailing = {
                                        Column {
                                            Text(text = (it.count * tokenData.price).humanizeDollar())
                                        }
                                    },
                                    icon = {
                                        Box {
                                            Image(
                                                painter = rememberImagePainter(data = tokenData.logoURI) {
                                                    placeholder(R.drawable.mask)
                                                    error(R.drawable.mask)
                                                    fallback(R.drawable.mask)
                                                },
                                                contentDescription = null,
                                                modifier = Modifier.size(38.dp)
                                            )
                                            Image(
                                                painter = rememberImagePainter(data = tokenData.chainType.onDrawableRes),
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp).align(Alignment.BottomEnd)
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                    BalancesSceneType.Collectible -> {
                        items(collectible) { item ->
                            if (item != null) {
                                CollectibleCard(
                                    data = item,
                                    onItemClicked = {
                                        onCollectibleDetailClicked.invoke(it)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WalletButton(
    text: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
) {
    MaskButton(
        onClick = onClick,
        modifier = Modifier.size(140.dp, 48.dp),
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.button
        )
    }
}
