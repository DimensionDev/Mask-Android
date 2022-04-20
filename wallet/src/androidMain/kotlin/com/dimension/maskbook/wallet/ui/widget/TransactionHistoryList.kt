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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
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
import com.dimension.maskbook.common.ext.humanizeDollar
import com.dimension.maskbook.common.ext.humanizeToken
import com.dimension.maskbook.common.model.DateType
import com.dimension.maskbook.common.ui.widget.HorizontalScenePadding
import com.dimension.maskbook.common.ui.widget.MaskListItem
import com.dimension.maskbook.common.ui.widget.button.MaskButton
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.repository.TransactionData
import com.dimension.maskbook.wallet.repository.TransactionStatus
import com.dimension.maskbook.wallet.repository.TransactionTokenData
import com.dimension.maskbook.wallet.repository.TransactionType
import org.joda.time.LocalDate
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionHistoryList(
    transactions: Map<DateType, List<TransactionData>>,
    onSpeedUp: (TransactionData) -> Unit,
    onCancel: (TransactionData) -> Unit,
    showPrice: Boolean = true,
) {
    if (transactions.isEmpty()) {
        TokenDetailEmptyLayout()
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(horizontal = HorizontalScenePadding),
    ) {
        transactions.forEach { entry ->
            stickyHeader {
                MaskListItem(
                    modifier = Modifier.background(MaterialTheme.colors.background),
                    contentPadding = PaddingValues(vertical = 24.dp),
                    text = {
                        val title = remember(entry.key) {
                            createTitle(entry.key, entry.value[0].createdAt)
                        }
                        Text(
                            text = title,
                            style = MaterialTheme.typography.h4,
                        )
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
                    },
                    showPrice = showPrice,
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

@Composable
private fun TransactionItem(
    item: TransactionData,
    tokenData: TransactionTokenData,
    onSpeedUp: () -> Unit,
    onCancel: () -> Unit,
    showPrice: Boolean = true,
) {
    MaskListItem(
        contentPadding = PaddingValues(vertical = 12.dp),
        text = {
            Row {
                Text(
                    text = item.title(),
                    style = MaterialTheme.typography.h5,
                )
                when (item.status) {
                    TransactionStatus.Pending -> {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.scene_transaction_history_status_pending),
                            color = Color(0xFFFFB915),
                            style = MaterialTheme.typography.h6,
                        )
                    }
                    TransactionStatus.Failure -> {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.scene_transaction_history_status_fail),
                            color = Color(0xFFFF5F5F),
                            style = MaterialTheme.typography.h6,
                        )
                    }
                    else -> Unit
                }
            }
        },
        secondaryText = {
            if (item.status == TransactionStatus.Pending) {
                Spacer(Modifier.height(6.dp))
                Row {
                    PrimaryButton(
                        onClick = onSpeedUp,
                        modifier = Modifier.size(62.dp, 28.dp),
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.scene_transaction_history_speed_up),
                            color = Color.White,
                            style = MaterialTheme.typography.caption.copy(
                                fontSize = 10.sp,
                                lineHeight = 15.sp,
                            )
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    MaskButton(
                        onClick = onCancel,
                        modifier = Modifier.size(62.dp, 28.dp),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFD7E6FF),
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.common_controls_cancel),
                            color = MaterialTheme.colors.primary,
                            style = MaterialTheme.typography.caption.copy(
                                fontSize = 10.sp,
                                lineHeight = 15.sp,
                            )
                        )
                    }
                }
            }
        },
        trailing = if (showPrice) {
            {
                Column(horizontalAlignment = Alignment.End) {
                    val count = item.count.humanizeToken()
                    val countPrefix = when {
                        count == "0" -> ""
                        item.type == TransactionType.Receive -> "+"
                        item.type == TransactionType.Send -> "-"
                        else -> ""
                    }

                    val dollar = item.price.humanizeDollar()
                    val dollarPrefix = when {
                        dollar == "$0" -> ""
                        item.type == TransactionType.Receive -> "+"
                        item.type == TransactionType.Send -> "-"
                        else -> ""
                    }
                    val dollarColor = when {
                        dollar == "$0" -> Color.Unspecified
                        item.type == TransactionType.Receive -> Color(0xFF1FB885)
                        item.type == TransactionType.Send -> Color.Unspecified
                        else -> Color.Unspecified
                    }

                    Text(
                        text = "$countPrefix$count ${tokenData.symbol}",
                        style = MaterialTheme.typography.caption,
                    )
                    Text(
                        text = "$dollarPrefix$dollar",
                        color = dollarColor,
                        style = MaterialTheme.typography.h5,
                    )
                }
            }
        } else null,
        icon = {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(item.iconTintBackground(), shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(item.icon()),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = item.iconTintColor(),
                )
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
        TransactionType.Unknown -> stringResource(
            R.string.scene_transaction_history_type_unknown,
            tokenData.symbol
        )
        TransactionType.Mint -> stringResource(R.string.scene_transaction_history_type_mint)
    }
}

private fun TransactionData.icon() = when {
    status == TransactionStatus.Failure -> R.drawable.ic_close_square
    type == TransactionType.Receive -> R.drawable.ic_transaction_history_receive
    type == TransactionType.Send -> R.drawable.ic_transaction_history_send
    type == TransactionType.Swap -> R.drawable.ic_transaction_history_swap
    type == TransactionType.Approve -> R.drawable.ic_transaction_history_approve
    else -> R.drawable.ic_transaction_history_swap
}

private fun TransactionData.iconTintColor() = when {
    status == TransactionStatus.Failure -> Color(0xFFFF5F5F)
    type == TransactionType.Receive -> Color(0xFF1C68F3)
    type == TransactionType.Send ||
        type == TransactionType.Swap -> Color(0xFFFFB915)
    type == TransactionType.Approve -> Color(0xFF1FB885)
    else -> Color(0xFFFFB915)
}

private fun TransactionData.iconTintBackground() = when {
    status == TransactionStatus.Failure -> Color(0x0AFF5F5F)
    type == TransactionType.Receive -> Color(0x1A1C68F3)
    type == TransactionType.Send ||
        type == TransactionType.Swap -> Color(0x1AFFB915)
    type == TransactionType.Approve -> Color(0x2677E0B5)
    else -> Color(0x1AFFB915)
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
