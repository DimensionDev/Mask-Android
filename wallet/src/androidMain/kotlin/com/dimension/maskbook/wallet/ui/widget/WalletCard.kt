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
package com.dimension.maskbook.wallet.ui.widget

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.dimension.maskbook.common.bigDecimal.BigDecimal
import com.dimension.maskbook.common.ext.humanizeDollar
import com.dimension.maskbook.common.ext.onDrawableRes
import com.dimension.maskbook.common.ui.widget.MiddleEllipsisText
import com.dimension.maskbook.common.ui.widget.button.MaskIconButton
import com.dimension.maskbook.common.ui.widget.button.MaskTextButton
import com.dimension.maskbook.common.ui.widget.button.MaskTransparentButton
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.export.model.DbWalletBalanceType
import com.dimension.maskbook.wallet.export.model.WalletData
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import kotlin.math.absoluteValue

@ExperimentalPagerApi
@Composable
fun WalletCard(
    wallets: List<WalletData>,
    walletChainType: ChainType,
    displayChainType: ChainType?,
    onWalletAddressClick: () -> Unit,
    onDisplayChainTypeClick: (ChainType?) -> Unit,
    onMoreClick: () -> Unit,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    HorizontalPager(
        count = wallets.size,
        state = pagerState,
    ) { page ->
        Box(
            modifier = modifier
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
                },
        ) {
            val wallet = wallets[page]
            val amount = remember(displayChainType, wallet) {
                when (displayChainType) {
                    null -> wallet.balance[DbWalletBalanceType.all]
                    ChainType.eth -> wallet.balance[DbWalletBalanceType.eth]
                    ChainType.bsc -> wallet.balance[DbWalletBalanceType.bsc]
                    ChainType.polygon -> wallet.balance[DbWalletBalanceType.polygon]
                    ChainType.arbitrum -> wallet.balance[DbWalletBalanceType.arbitrum]
                    ChainType.xdai -> wallet.balance[DbWalletBalanceType.xdai]
                    else -> null
                } ?: BigDecimal.ZERO
            }

            WalletCardItem(
                wallet = wallet,
                amount = amount,
                walletChainType = walletChainType,
                displayChainType = displayChainType,
                onWalletAddressClick = onWalletAddressClick,
                onDisplayChainTypeClick = onDisplayChainTypeClick,
                onMoreClick = onMoreClick,
            )
        }
    }
}

@Composable
fun WalletCardItem(
    wallet: WalletData,
    amount: BigDecimal,
    walletChainType: ChainType,
    displayChainType: ChainType?,
    onWalletAddressClick: () -> Unit,
    onDisplayChainTypeClick: (ChainType?) -> Unit,
    onMoreClick: () -> Unit,
) {
    Box {
        Column(
            modifier = Modifier
                .background(
                    brush = WalletCardDefaults.walletCardBackground,
                    shape = WalletCardDefaults.walletCardShape,
                )
                .clip(
                    shape = WalletCardDefaults.walletCardShape
                )
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = WalletCardDefaults.contentPadding),
            ) {
                Spacer(Modifier.height(WalletCardDefaults.contentPadding))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = wallet.name,
                        color = Color.White,
                        style = MaterialTheme.typography.h3,
                    )
                    Spacer(Modifier.width(4.dp))
                    Row(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colors.surface.copy(0.11f),
                                shape = RoundedCornerShape(4.dp),
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(walletChainType.onDrawableRes),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = walletChainType.name,
                            color = Color.White,
                            style = MaterialTheme.typography.caption,
                        )
                    }
                }
                MaskTextButton(
                    onClick = onWalletAddressClick,
                    contentPadding = PaddingValues(0.dp),
                ) {
                    MiddleEllipsisText(
                        text = wallet.address,
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.fillMaxWidth(0.6f),
                    )
                    Spacer(Modifier.width(2.dp))
                    Icon(
                        painter = painterResource(R.drawable.ic_copy),
                        contentDescription = null,
                        tint = Color.White.copy(0.6f),
                    )
                }
                // Spacer(Modifier.height(10.dp))
                Text(
                    text = amount.humanizeDollar(),
                    color = Color.White,
                    style = MaterialTheme.typography.h1,
                )
            }
            Spacer(Modifier.height(10.dp))
            WalletDisplayAmount(
                chainType = displayChainType,
                onDisplayChainTypeClick = onDisplayChainTypeClick,
            )
        }
        MaskIconButton(
            onClick = onMoreClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 20.dp, end = WalletCardDefaults.contentPadding),
        ) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = null,
                tint = Color.White,
            )
        }
    }
}

@Composable
private fun WalletDisplayAmount(
    chainType: ChainType?,
    onDisplayChainTypeClick: (ChainType?) -> Unit,
) {
    Row(
        modifier = Modifier
            .background(
                brush = WalletCardDefaults.displayAmountTypeBackground,
            )
            .padding(
                horizontal = WalletCardDefaults.contentPadding,
            )
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(WalletCardDefaults.contentPadding),
    ) {
        amountTypeList.forEach { item ->
            val isSelected = if (chainType == null) {
                DisplayAmountType.All === item
            } else {
                chainType === item.chainType
            }
            MaskTransparentButton(
                enabled = !isSelected,
                onClick = { onDisplayChainTypeClick(item.chainType) },
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.alpha(if (isSelected) 1f else 0.6f),
                ) {
                    Spacer(Modifier.height(13.dp))
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color.White,
                    )
                    if (isSelected) {
                        Spacer(Modifier.height(4.dp))
                        Spacer(Modifier.size(4.dp).background(Color.White, shape = CircleShape))
                        Spacer(Modifier.height(11.dp))
                    } else {
                        Spacer(Modifier.height(19.dp))
                    }
                }
            }
        }
    }
}

private val amountTypeList = DisplayAmountType.values()

private enum class DisplayAmountType(
    @DrawableRes val icon: Int,
    val chainType: ChainType?,
) {
    All(R.drawable.wallet_7, null),
    ETH(R.drawable.wallet_6, ChainType.eth),
    Binance(R.drawable.wallet_2, ChainType.bsc),
    Matic(R.drawable.wallet_3, ChainType.polygon),
    Arbitrum(R.drawable.wallet_4, ChainType.arbitrum),
    Xdai(R.drawable.wallet_5, ChainType.xdai),
}

private object WalletCardDefaults {
    val contentPadding = 16.dp

    val walletCardShape = RoundedCornerShape(26.dp)
    val walletCardBackground = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0049CE),
            Color(0xFF1C68F3),
        )
    )
    val displayAmountTypeBackground = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(0.08f),
            Color.Transparent
        )
    )
}
