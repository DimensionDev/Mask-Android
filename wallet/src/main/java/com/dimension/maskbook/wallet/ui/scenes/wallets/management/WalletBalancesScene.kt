package com.dimension.maskbook.wallet.ui.scenes.wallets.management

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.db.model.DbWalletBalanceType
import com.dimension.maskbook.wallet.ext.humanizeDollar
import com.dimension.maskbook.wallet.ext.humanizeToken
import com.dimension.maskbook.wallet.repository.*
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.MaskCard
import com.dimension.maskbook.wallet.ui.widget.MaskIconButton
import com.dimension.maskbook.wallet.ui.widget.MaskIconCardButton
import com.dimension.maskbook.wallet.ui.widget.MaskListCardItem
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.wallet.ui.widget.TokenAddressText
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import java.math.BigDecimal
import kotlin.math.absoluteValue

enum class BalancesSceneType {
    Token,
    Collectible
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalCoilApi::class, ExperimentalFoundationApi::class)
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
    chainType: ChainType,
    onCollectibleDetailClicked: (WalletCollectibleItemData) -> Unit,
    onBack: () -> Unit,
    displayAmountType: DisplayAmountType,
    onDisplayAmountTypeChanged: (DisplayAmountType) -> Unit,
    collectible: LazyPagingItems<WalletCollectibleData>,
) {
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
                        currentWallet = currentWallet,
                        onWalletChanged = onWalletChanged,
                        onMoreClicked = {
                            onWalletMenuClicked.invoke()
                        },
                        chainType = chainType,
                        displayAmountType = displayAmountType,
                        onDisplayAmountTypeChanged = onDisplayAmountTypeChanged,
                    )
                }
                item {
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
                                Text(text = stringResource(R.string.scene_wallet_balance_btn_Send), style = MaterialTheme.typography.subtitle1)
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
                                Text(text = stringResource(R.string.scene_wallet_balance_btn_receive), style = MaterialTheme.typography.subtitle1)
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
                            if (displayAmountType.chainType == null) {
                                currentWallet.tokens
                            } else {
                                currentWallet.tokens.filter {
                                    it.tokenData.chainType == displayAmountType.chainType
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

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun WalletCard(
    chainType: ChainType,
    wallets: List<WalletData>,
    currentWallet: WalletData,
    onWalletChanged: (WalletData) -> Unit,
    onMoreClicked: () -> Unit,
    displayAmountType: DisplayAmountType,
    onDisplayAmountTypeChanged: (DisplayAmountType) -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
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
    HorizontalPager(
        count = wallets.size,
        state = pagerState,
    ) { page ->
        Box(
            modifier = Modifier
                .graphicsLayer {
                    val pageOffset =
                        calculateCurrentOffsetForPage(page).absoluteValue
                    lerp(
                        start = 0.85f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    ).also { scale ->
                        scaleX = scale
                        scaleY = scale
                    }
                    alpha = lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                }
                .fillMaxWidth(0.9f),
        ) {
            val wallet = wallets[page]
            val amount = remember(displayAmountType, wallet) {
                when (displayAmountType) {
                    DisplayAmountType.All -> wallet.balance[DbWalletBalanceType.all]
                    DisplayAmountType.ETH -> wallet.balance[DbWalletBalanceType.eth]
                    DisplayAmountType.Binance -> wallet.balance[DbWalletBalanceType.bsc]
                    DisplayAmountType.Matic -> wallet.balance[DbWalletBalanceType.polygon]
                    DisplayAmountType.Arbitrum -> wallet.balance[DbWalletBalanceType.arbitrum]
                } ?: BigDecimal.ZERO
            }
            WalletCardItem(
                walletData = wallets[page],
                amount = amount,
                selectedDisplayAmountType = displayAmountType,
                onDisplayAmountTypeChanged = onDisplayAmountTypeChanged,
                onCopyClicked = {
                    clipboardManager.setText(buildAnnotatedString { append(wallet.address) })
                },
                onMoreClicked = onMoreClicked,
                chainType = chainType,
            )
        }
    }
}


@Composable
fun WalletCardItem(
    walletData: WalletData,
    amount: BigDecimal,
    chainType: ChainType,
    selectedDisplayAmountType: DisplayAmountType,
    onDisplayAmountTypeChanged: (DisplayAmountType) -> Unit,
    onCopyClicked: () -> Unit,
    onMoreClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(26.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0XFF0049CE),
                        Color(0XFF1C68F3)
                    )
                ),
                shape = RoundedCornerShape(26.dp),
            )
            .fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = walletData.name,
                        style = MaterialTheme.typography.h6.copy(color = Color.White)
                    )
                    Row(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colors.surface.copy(alpha = 0.11f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(id = chainType.onDrawableRes),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                        )
                        Text(
                            text = chainType.name,
                            style = MaterialTheme.typography.caption.copy(color = Color.White),
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        onCopyClicked.invoke()
                    }
                ) {
                    TokenAddressText(
                        text = walletData.address,
                        style = MaterialTheme.typography.caption.copy(Color.White.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth(0.6f)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_copy),
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = amount.humanizeDollar(),
                    style = MaterialTheme.typography.h4.copy(color = Color.White)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DisplayAmountType.values().forEach {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Image(
                                painter = painterResource(id = it.icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .alpha(if (selectedDisplayAmountType == it) 1f else 0.6f)
                                    .size(28.dp)
                                    .clickable {
                                        if (selectedDisplayAmountType != it) {
                                            onDisplayAmountTypeChanged.invoke(it)
                                        }
                                    }
                            )
                            if (selectedDisplayAmountType == it) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .background(color = Color.White, shape = CircleShape)
                                )
                            }
                        }
                    }
                }
            }
            MaskIconButton(
                onClick = {
                    onMoreClicked.invoke()
                },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_more_circle),
                    contentDescription = null
                )
            }
        }
    }
}

enum class DisplayAmountType(
    @DrawableRes val icon: Int,
    val chainType: ChainType?,
) {
    All(R.drawable.wallet_7, null),
    ETH(R.drawable.wallet_6, ChainType.eth),
    Binance(R.drawable.wallet_2, ChainType.bsc),
    Matic(R.drawable.wallet_3, ChainType.polygon),
    Arbitrum(R.drawable.wallet_4, ChainType.arbitrum),
}