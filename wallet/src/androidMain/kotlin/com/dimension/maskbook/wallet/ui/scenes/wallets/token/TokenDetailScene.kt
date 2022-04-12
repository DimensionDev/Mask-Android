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
package com.dimension.maskbook.wallet.ui.scenes.wallets.token

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.dimension.maskbook.common.ext.humanizeDollar
import com.dimension.maskbook.common.ext.humanizeToken
import com.dimension.maskbook.common.model.DateType
import com.dimension.maskbook.common.ui.widget.HorizontalScenePadding
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskScene
import com.dimension.maskbook.common.ui.widget.MaskTopAppBar
import com.dimension.maskbook.common.ui.widget.button.MaskBackButton
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.export.model.TokenData
import com.dimension.maskbook.wallet.export.model.WalletTokenData
import com.dimension.maskbook.wallet.repository.TransactionData
import com.dimension.maskbook.wallet.ui.widget.TransactionHistoryList

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun TokenDetailScene(
    onBack: () -> Unit,
    tokenData: TokenData?,
    walletTokenData: WalletTokenData?,
    transactions: Map<DateType, List<TransactionData>>,
    onSpeedUp: (TransactionData) -> Unit,
    onCancel: (TransactionData) -> Unit,
    onSend: () -> Unit,
    onReceive: () -> Unit
) {
    MaskScene {
        MaskScaffold(
            topBar = {
                MaskTopAppBar(
                    navigationIcon = {
                        MaskBackButton(
                            onBack = onBack
                        )
                    }
                )
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (walletTokenData != null && tokenData != null) {
                    Surface(
                        contentColor = contentColorFor(backgroundColor = Color(0XFF212E59)),
                        color = Color(0XFF212E59),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.padding(horizontal = HorizontalScenePadding)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (tokenData.logoURI != null) {
                                Image(
                                    painter = rememberImagePainter(data = tokenData.logoURI),
                                    contentDescription = null,
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = tokenData.name,
                                style = MaterialTheme.typography.subtitle2,
                                modifier = Modifier.weight(1f),
                                color = Color.White,
                            )
                            Column(
                                horizontalAlignment = Alignment.End,
                            ) {
                                Text(
                                    text = "${walletTokenData.count.humanizeToken()} ${tokenData.symbol}",
                                    color = Color.White,
                                )
                                Text(
                                    text = (walletTokenData.count * tokenData.price).humanizeDollar(),
                                    style = MaterialTheme.typography.h6,
                                    color = Color.White,
                                )
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier.weight(1f),
                ) {
                    TransactionHistoryList(transactions, onSpeedUp, onCancel)
                }
                Column(
                    modifier = Modifier.background(MaterialTheme.colors.surface)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = HorizontalScenePadding),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PrimaryButton(
                            modifier = Modifier.weight(1f),
                            onClick = { onSend.invoke() },
                            elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFFFFB915))
                        ) {
                            Icon(painterResource(id = R.drawable.upload), contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.scene_wallet_balance_btn_Send),
                                maxLines = 1
                            )
                        }
                        PrimaryButton(
                            modifier = Modifier.weight(1f),
                            onClick = onReceive,
                            elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                        ) {
                            Icon(
                                painterResource(id = R.drawable.download),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.scene_wallet_balance_btn_receive),
                                maxLines = 1
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(41.dp))
                }
            }
        }
    }
}
