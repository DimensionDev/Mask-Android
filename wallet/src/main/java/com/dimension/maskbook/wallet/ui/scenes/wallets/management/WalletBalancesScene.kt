package com.dimension.maskbook.wallet.ui.scenes.wallets.management

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.CollectibleCard
import com.dimension.maskbook.wallet.ui.widget.MaskCard
import com.dimension.maskbook.wallet.ui.widget.MaskIconCardButton
import com.dimension.maskbook.wallet.ui.widget.MaskListCardItem
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.wallet.ui.widget.WalletCard
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState

enum class BalancesSceneType {
    Token,
    Collectible
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalCoilApi::class, ExperimentalFoundationApi::class,
    ExperimentalPagerApi::class
)
@Composable
fun WalletBalancesScene(
    wallets: List<WalletData>,
    currentWallet: WalletData,
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
            LazyColumn {
                item {
                    WalletCard(
                        wallets = wallets,
                        walletChainType = walletChainType,
                        displayChainType = displayChainType,
                        onWalletAddressClick = onWalletAddressClicked,
                        onDisplayChainTypeClick = onDisplayChainTypeClicked,
                        onMoreClick = onWalletMenuClicked,
                        pagerState = pagerState,
                        modifier = Modifier.fillMaxWidth(0.9f),
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    Row(
                        modifier = Modifier.padding(horizontal = 22.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        MaskCard(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSendClicked.invoke() }
                                .padding(12.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.transaction_1),
                                    contentDescription = null,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.scene_wallet_balance_btn_Send),
                                    style = MaterialTheme.typography.subtitle1
                                )
                            }
                        }

                        MaskCard(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onReceiveClicked.invoke() }
                                .padding(12.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.transaction_2),
                                    contentDescription = null,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.scene_wallet_balance_btn_receive),
                                    style = MaterialTheme.typography.subtitle1
                                )
                            }
                        }
                    }
                }
                stickyHeader {
                    Box {
                        CompositionLocalProvider(
                            LocalTextStyle provides LocalTextStyle.current.copy(color = Color.Unspecified)
                        ) {
                            ScrollableTabRow(
                                modifier = Modifier.fillMaxWidth(),
                                selectedTabIndex = BalancesSceneType.values().indexOf(sceneType),
                                backgroundColor = MaterialTheme.colors.background,
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
                                                .fillMaxWidth(0.4f)
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
                                        selectedContentColor = MaterialTheme.colors.primary,
                                        unselectedContentColor = MaterialTheme.colors.onBackground.copy(
                                            alpha = ContentAlpha.medium
                                        ),
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.align(Alignment.CenterEnd),
                        ) {
                            TextButton(onClick = { /*TODO*/ }) {
                                Text(text = stringResource(R.string.scene_wallet_derivation_path_operation_add))
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Add, contentDescription = null)
                            }
                            Spacer(modifier = Modifier.width(22.dp))
                        }
                    }
                }
                when (sceneType) {
                    BalancesSceneType.Token -> {
                        items(
                            if (displayChainType == null) {
                                currentWallet.tokens
                            } else {
                                currentWallet.tokens.filter {
                                    it.tokenData.chainType === displayChainType
                                }
                            }.sortedByDescending {
                                it.tokenData.price * it.count
                            }
                        ) {
                            val tokenData = it.tokenData
                            MaskListCardItem(
                                modifier = Modifier
                                    .clickable {
                                        onTokenDetailClicked.invoke(tokenData)
                                    },
                                text = {
                                    Text(text = tokenData.name)
                                },
                                secondaryText = {
                                    Text(text = it.count.humanizeToken() + " ${tokenData.symbol}")
                                },
                                trailing = {
                                    Text(text = (it.count * tokenData.price).humanizeDollar())
                                },
                                icon = {
                                    Box {
                                        Image(
                                            painter = rememberImagePainter(data = tokenData.logoURI),
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
                    BalancesSceneType.Collectible -> {
                        items(collectible) {
                            if (it != null) {
                                CollectibleCard(
                                    data = it,
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
