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
package com.dimension.maskbook.labs.ui.scenes.redpacket

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.dimension.maskbook.common.bigDecimal.BigDecimal
import com.dimension.maskbook.common.ext.encodeUrl
import com.dimension.maskbook.common.ext.eventFlow
import com.dimension.maskbook.common.ext.navigateUri
import com.dimension.maskbook.common.ext.navigateWithPopSelf
import com.dimension.maskbook.common.model.ResultEvent
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.common.route.navigationComposeBottomSheet
import com.dimension.maskbook.common.route.navigationComposeBottomSheetPackage
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.routeProcessor.annotations.Path
import com.dimension.maskbook.common.routeProcessor.annotations.Query
import com.dimension.maskbook.common.ui.theme.moreTypography
import com.dimension.maskbook.common.ui.widget.MaskListItem
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.MiddleEllipsisText
import com.dimension.maskbook.common.ui.widget.WalletTokenImage
import com.dimension.maskbook.common.ui.widget.button.MaskButton
import com.dimension.maskbook.labs.R
import com.dimension.maskbook.labs.model.ui.UiLuckyDropData
import com.dimension.maskbook.labs.route.LabsRoute
import com.dimension.maskbook.labs.ui.widget.ClaimLoadingIndicator
import com.dimension.maskbook.labs.ui.widget.RedPacketClaimButton
import com.dimension.maskbook.labs.viewmodel.LuckDropViewModel
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.pow

@NavGraphDestination(
    route = LabsRoute.RedPacket.LuckyDrop.path,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun LuckDropModal(
    navController: NavController,
    @Path("dataRaw") dataRaw: String,
    @Query("requestRaw") requestRaw: String?,
) {
    val viewModel = getViewModel<LuckDropViewModel> {
        parametersOf(dataRaw, requestRaw)
    }
    val stateData by viewModel.stateData.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(Unit) {
        navController.eventFlow<ResultEvent.TokenConfirm>().collect { event ->
            val transactionHash = event.transactionHash
            if (transactionHash == null) {
                navController.navigateWithPopSelf(
                    LabsRoute.RedPacket.LuckyDropResult(false, null, null)
                )
                return@collect
            }

            val text = (
                "Hi friends, I just found a lucky drop sent by @${stateData.redPacket.shares} on ${stateData.redPacket.tokenName} network. " +
                    "Follow @realMaskNetwork (mask.io) to claim lucky drops.\n" +
                    "#mask_io #LuckyDrop\n" +
                    "${stateData.redPacket.postLink}"
                )
            val tweetUrl = "https://mobile.twitter.com/compose/tweet?text=${text.encodeUrl()}"

            val redPacketState = viewModel.getRedPacketAvailabilityState(stateData, transactionHash)
            navController.navigateWithPopSelf(
                LabsRoute.RedPacket.LuckyDropResult(
                    success = redPacketState != null,
                    amount = redPacketState?.let {
                        "%s %s".format(
                            it.claimedAmount.divide(BigDecimal(10.0.pow(stateData.wallet.decimals))),
                            stateData.wallet.symbol
                        )
                    },
                    tweetUrl = tweetUrl,
                )
            )
        }
    }

    MaskModal(
        title = {
            Text(stringResource(R.string.scene_open_red_package_title))
        }
    ) {
        Column {
            GoodLuckCard(
                redPacket = stateData.redPacket,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.scene_open_red_package_wallet),
                style = MaterialTheme.typography.h3,
            )
            Spacer(Modifier.height(8.dp))
            WalletTokenCard(
                wallet = stateData.wallet,
                onClick = {
                    navController.navigateUri(Uri.parse(Deeplinks.Wallet.SwitchWallet))
                }
            )
            Spacer(Modifier.height(24.dp))
            RedPacketClaimButton(
                enabled = stateData.buttonEnabled && !loading,
                onClick = {
                    viewModel.getSendTransactionData(stateData)?.let { data ->
                        navController.navigateUri(
                            Uri.parse(Deeplinks.Wallet.SendTokenConfirm(data, ignorePaymentPassword = true))
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (loading) {
                    Text(stringResource(R.string.scene_open_red_package_claiming))
                    Spacer(Modifier.width(8.dp))
                    ClaimLoadingIndicator()
                } else {
                    Text(
                        text = if (stateData.buttonStringRes != 0) {
                            stringResource(stateData.buttonStringRes)
                        } else "",
                    )
                }
            }
        }
    }
}

@Composable
private fun GoodLuckCard(
    redPacket: UiLuckyDropData.RedPacket,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(color = Color(0xFfC2130F), shape = MaterialTheme.shapes.medium)
            .padding(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = redPacket.message,
                style = MaterialTheme.typography.h6,
                color = Color.White,
            )
            Text(
                text = if (redPacket.stateStringRes != 0) {
                    stringResource(redPacket.stateStringRes)
                } else "",
                style = MaterialTheme.moreTypography.h10,
                color = Color.White,
                modifier = Modifier
                    .background(color = Color(0x66000000), shape = CircleShape)
                    .padding(horizontal = 8.dp, vertical = 5.dp),
            )
        }
        Text(
            text = "%d %s/ %s %s".format(
                redPacket.shares,
                stringResource(R.string.scene_open_red_package_shares),
                redPacket.amount,
                stringResource(R.string.scene_open_red_package_total),
            ),
            style = MaterialTheme.moreTypography.h7,
            color = Color(0xFFFFB915)
        )
        Image(
            painter = painterResource(R.drawable.ic_redpack_good_luck),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .height(120.dp)
                .align(Alignment.CenterHorizontally),
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.scene_open_red_package_ends, redPacket.endTime),
                style = MaterialTheme.moreTypography.h10,
                color = Color(0xFFFFB915),
            )
            Text(
                text = stringResource(R.string.scene_open_red_package_from, redPacket.senderName),
                style = MaterialTheme.moreTypography.h10,
                color = Color.White,
            )
        }
    }
}

@Composable
private fun WalletTokenCard(
    wallet: UiLuckyDropData.Wallet,
    onClick: () -> Unit,
) {
    MaskButton(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
        ),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.border(1.dp, Color(0xFFEFF3F4), shape = MaterialTheme.shapes.small),
    ) {
        MaskListItem(
            icon = {
                WalletTokenImage(
                    painter = rememberImagePainter(R.drawable.mask),
                    chainPainter = rememberImagePainter(wallet.chainTypeIcon),
                )
            },
            text = {
                Row {
                    Text(
                        text = wallet.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    MiddleEllipsisText(
                        text = "(${wallet.address})",
                        modifier = Modifier.weight(1f, fill = false),
                    )
                }
            },
            secondaryText = {
                Text(wallet.chainBalance)
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
