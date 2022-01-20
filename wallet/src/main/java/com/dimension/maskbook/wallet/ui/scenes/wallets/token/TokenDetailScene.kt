package com.dimension.maskbook.wallet.ui.scenes.wallets.token

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.humanizeDollar
import com.dimension.maskbook.wallet.ext.humanizeToken
import com.dimension.maskbook.wallet.repository.TokenData
import com.dimension.maskbook.wallet.repository.TransactionData
import com.dimension.maskbook.wallet.repository.TransactionStatus
import com.dimension.maskbook.wallet.repository.TransactionType
import com.dimension.maskbook.wallet.repository.WalletTokenData
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.MaskBackButton
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskTopAppBar
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import org.joda.time.DateTime

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun TokenDetailScene(
    onBack: () -> Unit,
    tokenData: TokenData,
    walletTokenData: WalletTokenData,
    transactions: List<TransactionData>,
    onSpeedUp: (TransactionData) -> Unit,
    onCancel: (TransactionData) -> Unit,
    onSend: () -> Unit,
    onReceive: () -> Unit
) {
    MaskTheme {
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
                Surface(
                    contentColor = contentColorFor(backgroundColor = Color(0XFF212E59)),
                    color = Color(0XFF212E59),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(horizontal = 23.dp)
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
                            .padding(horizontal = 23.dp),
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun TransactionHistoryList(
    transactions: List<TransactionData>,
    onSpeedUp: (TransactionData) -> Unit,
    onCancel: (TransactionData) -> Unit,
) {
    if (transactions.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_token_detail_empty),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = stringResource(R.string.scene_transaction_history_no_transaction))
        }
    } else {
        val items = remember(transactions) {
            transactions.sortedByDescending { it.createdAt }
                .groupBy { DateTime(it.createdAt).toLocalDate() }.toMap()
        }
        LazyColumn {
            items.forEach { entry ->
                stickyHeader {
                    ListItem {
                        Text(
                            text = entry.key.toString(),
                            style = MaterialTheme.typography.subtitle1.copy(fontSize = 18.sp)
                        )
                    }
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
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TransactionItem(
    it: TransactionData,
    tokenData: TokenData,
    onSpeedUp: () -> Unit,
    onCancel: () -> Unit,
) {
    ListItem(
        modifier = Modifier.padding(vertical = 10.dp),
        text = {
            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                Row {
                    Text(text = it.title(), style = MaterialTheme.typography.subtitle1)
                    when (it.status) {
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

                if (it.status == TransactionStatus.Pending) {
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
            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = "${it.count.humanizeToken()} ${tokenData.symbol}",
                )
                Text(
                    text = (it.count * tokenData.price).humanizeDollar(),
                    style = if (it.type == TransactionType.Receive) MaterialTheme.typography.subtitle1.copy(
                        color = Color(0xFF1FB885)
                    ) else MaterialTheme.typography.subtitle1,
                )
            }
        },
        icon = {
            val icon = when {
                it.status == TransactionStatus.Failure -> R.drawable.ic_close_square
                it.type == TransactionType.Receive -> R.drawable.download
                it.type == TransactionType.Send -> R.drawable.upload
                it.type == TransactionType.Swap -> R.drawable.filter2
                it.type == TransactionType.Approve -> R.drawable.filter2
                else -> null
            }
            if (icon != null) {
                val color = if (it.status == TransactionStatus.Failure) {
                    Color(0x0AFF5F5F)
                } else if (it.type == TransactionType.Receive) {
                    Color(0x1A1C68F3)
                } else {
                    Color(0x1AFFB915)
                }
                val iconColor = if (it.status == TransactionStatus.Failure) {
                    Color(0xFFFF5F5F)
                } else if (it.type == TransactionType.Receive) {
                    Color(0xFF1C68F3)
                } else {
                    Color(0xFFFFB915)
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
        TransactionType.Swap -> stringResource(R.string.scene_transaction_history_type_swap)
        TransactionType.Receive -> stringResource(R.string.scene_transaction_history_type_receive, tokenData.symbol)
        TransactionType.Send -> stringResource(R.string.scene_transaction_history_type_send, tokenData.symbol)
        TransactionType.Approve -> stringResource(R.string.scene_transaction_history_type_approve, tokenData.symbol)
        TransactionType.Cancel -> stringResource(R.string.scene_transaction_history_type_cancel, tokenData.symbol)
    }
}
