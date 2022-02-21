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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimension.maskbook.common.model.DateType
import com.dimension.maskbook.common.ui.widget.MaskListItem
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.export.model.TokenData
import com.dimension.maskbook.wallet.ext.humanizeDollar
import com.dimension.maskbook.wallet.ext.humanizeToken
import com.dimension.maskbook.wallet.repository.TransactionData
import com.dimension.maskbook.wallet.repository.TransactionStatus
import com.dimension.maskbook.wallet.repository.TransactionType
import org.joda.time.LocalDate
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun TransactionHistoryList(
    transactions: Map<DateType, List<TransactionData>>,
    onSpeedUp: (TransactionData) -> Unit,
    onCancel: (TransactionData) -> Unit,
) {
    if (transactions.isEmpty()) {
        TokenDetailEmptyLayout()
        return
    }
    LazyColumn {
        transactions.forEach { entry ->
            stickyHeader {
                MaskListItem(
                    modifier = Modifier.background(MaterialTheme.colors.background),
                    text = {
                        val title = remember {
                            createTitle(entry.key, entry.value[0].createdAt)
                        }
                        Text(text = title)
                    }
                )
            }
            items(entry.value) { item ->
                TransactionItem(
                    item,
                    item.tokenData,
                    onSpeedUp = {
                        onSpeedUp.invoke(item)
                    },
                    onCancel = {
                        onCancel.invoke(item)
                    }
                )
            }
        }
    }
}

@Composable
private fun TokenDetailEmptyLayout() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_token_detail_empty),
            contentDescription = null,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.scene_transaction_history_no_transaction)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TransactionItem(
    item: TransactionData,
    tokenData: TokenData,
    onSpeedUp: () -> Unit,
    onCancel: () -> Unit,
) {
    MaskListItem(
        modifier = Modifier.padding(vertical = 10.dp),
        text = {
            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                Row {
                    Text(text = item.title(), style = MaterialTheme.typography.subtitle1)
                    when (item.status) {
                        TransactionStatus.Pending -> {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.scene_transaction_history_status_pending),
                                color = Color(0xFFFFB915)
                            )
                        }
                        TransactionStatus.Failure -> {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.scene_transaction_history_status_fail),
                                color = Color(0xFFFF5F5F)
                            )
                        }
                        else -> Unit
                    }
                }

                if (item.status == TransactionStatus.Pending) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row {
                        PrimaryButton(
                            onClick = onSpeedUp,
                            modifier = Modifier.height(height = 28.dp),
                            contentPadding = PaddingValues(vertical = 6.dp, horizontal = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.scene_transaction_history_speed_up),
                                style = MaterialTheme.typography.button.copy(fontSize = 10.sp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onCancel,
                            modifier = Modifier.height(height = 28.dp),
                            contentPadding = PaddingValues(vertical = 0.dp, horizontal = 0.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFFD7E6FF),
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.common_controls_cancel),
                                style = MaterialTheme.typography.button.copy(
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colors.primary
                                )
                            )
                        }
                    }
                }
            }
        },
        trailing = {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${item.count.humanizeToken()} ${tokenData.symbol}",
                )
                Text(
                    text = (item.count * tokenData.price).humanizeDollar(),
                    style = if (item.type == TransactionType.Receive) MaterialTheme.typography.subtitle1.copy(
                        color = Color(0xFF1FB885)
                    ) else MaterialTheme.typography.subtitle1,
                )
            }
        },
        icon = {
            when {
                item.status == TransactionStatus.Failure -> R.drawable.ic_close_square
                item.type == TransactionType.Receive -> R.drawable.download
                item.type == TransactionType.Send -> R.drawable.upload
                item.type == TransactionType.Swap -> R.drawable.filter2
                item.type == TransactionType.Approve -> R.drawable.filter2
                else -> null
            }?.let { icon ->
                val color = when {
                    item.status == TransactionStatus.Failure -> Color(0x0AFF5F5F)
                    item.type == TransactionType.Receive -> Color(0x1A1C68F3)
                    else -> Color(0x1AFFB915)
                }
                val iconColor = when {
                    item.status == TransactionStatus.Failure -> Color(0xFFFF5F5F)
                    item.type == TransactionType.Receive -> Color(0xFF1C68F3)
                    else -> Color(0xFFFFB915)
                }
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(color, shape = CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(painterResource(id = icon), contentDescription = null, tint = iconColor)
                }
            }
        }
    )
}

@Composable
private fun TransactionData.title() = message.ifEmpty {
    when (type) {
        TransactionType.Swap -> stringResource(
            R.string.scene_transaction_history_type_swap,
        )
        TransactionType.Receive -> stringResource(
            R.string.scene_transaction_history_type_receive,
            tokenData.symbol
        )
        TransactionType.Send -> stringResource(
            R.string.scene_transaction_history_type_send,
            tokenData.symbol
        )
        TransactionType.Approve -> stringResource(
            R.string.scene_transaction_history_type_approve,
            tokenData.symbol
        )
        TransactionType.Cancel -> stringResource(
            R.string.scene_transaction_history_type_cancel,
            tokenData.symbol
        )
    }
}

private fun createTitle(dateType: DateType, dateTime: Long): String {
    val date = LocalDate(dateTime)
    return when (dateType) {
        DateType.Today -> "Today"
        DateType.Yesterday -> "Yesterday"
        is DateType.ThisMonth -> "This Month"
        is DateType.ThisYear -> date.monthOfYear().getAsText(Locale.US)
        is DateType.OlderDate -> date.monthOfYear().getAsText(Locale.US) + ", " +
            date.year().getAsText(Locale.US)
        else -> ""
    }
}
