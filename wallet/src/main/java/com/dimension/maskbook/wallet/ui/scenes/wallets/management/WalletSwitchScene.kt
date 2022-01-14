package com.dimension.maskbook.wallet.ui.scenes.wallets.management

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.repository.ChainType
import com.dimension.maskbook.wallet.repository.WalletData
import com.dimension.maskbook.wallet.ui.widget.*

val ChainType.onDrawableRes: Int
    get() = when (this) {
        ChainType.eth -> R.drawable.ethereum_o1_2
        ChainType.bsc -> R.drawable.binance_2
        ChainType.polygon -> R.drawable.polygon_2
        ChainType.optimism -> R.drawable.logos_and_symbols
        ChainType.xdai -> R.drawable._4
        else -> -1
    }

val ChainType.offDrawableRes: Int
    get() = when (this) {
        ChainType.eth -> R.drawable.ethereum_o1_1
        ChainType.bsc -> R.drawable.binance_1
        ChainType.polygon -> R.drawable.polygon1
        ChainType.optimism -> R.drawable.logos_and_symbols_1
        ChainType.xdai -> R.drawable._5
        else -> -1
    }

@OptIn(ExperimentalStdlibApi::class)
val supportedChainType = buildList {
    add(ChainType.eth)
    add(ChainType.bsc)
    add(ChainType.polygon)
    add(ChainType.optimism)
    add(ChainType.xdai)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WalletSwitchScene(
    onBack: () -> Unit,
    selectedWallet: WalletData,
    wallets: List<WalletData>,
    onWalletSelected: (WalletData) -> Unit,
    selectedChainType: ChainType,
    onChainTypeSelected: (ChainType) -> Unit,
    onAddWalletClicked: () -> Unit,
    onWalletConnectClicked: () -> Unit,
    onEditMenuClicked: (data: WalletData) -> Unit,
) {
    var editMode by remember {
        mutableStateOf(false)
    }
    MaskModal {
        MaskScaffold(
            topBar = {
                Column {
                    MaskSingleLineTopAppBar(
                        title = {
                            Text(text = stringResource(R.string.scene_wallet_list_title))
                        },
                        actions = {
                            TextButton(
                                onClick = {
                                    editMode = !editMode
                                },
                            ) {
                                if (editMode) {
                                    Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_done))
                                } else {
                                    Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_sendTransaction_sendConfirmPop_edit))
                                }
                            }
                        }
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(36.dp)
                    ) {
                        items(supportedChainType) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val res = if (selectedChainType == it) {
                                    it.onDrawableRes
                                } else {
                                    it.offDrawableRes
                                }
                                Image(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clickable {
                                            if (selectedChainType != it) {
                                                onChainTypeSelected(it)
                                            }
                                        },
                                    painter = painterResource(id = res),
                                    contentDescription = null
                                )
                                if (selectedChainType == it) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(
                                                MaterialTheme.colors.primary,
                                                shape = CircleShape
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                Box(
                    modifier = Modifier.background(MaterialTheme.colors.surface)
                ) {
                    MaskListItem(
                        text = {
                            Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_wallet_list_wallets_items_add))
                        },
                        trailing = {
                            IconButton(
                                onClick = {
                                    onAddWalletClicked.invoke()
                                },
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_plus),
                                    contentDescription = null,
                                )
                            }
                        }
                    )
                }
            }
        ) {
            LazyColumn(
                contentPadding = it,
            ) {
                items(wallets) {
                    MaskListCardItem(
                        modifier = Modifier
                            .clickable {
                                if (selectedWallet != it) {
                                    onWalletSelected.invoke(it)
                                }
                            },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(text = it.name)
                                if (it.imported) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_wallet_list_wallets_list_import),
                                        modifier = Modifier
                                            .background(
                                                color = Color(0XFFEBF0F8),
                                                shape = RoundedCornerShape(4.dp),
                                            )
                                            .padding(horizontal = 8.dp),
                                        color = Color(0XFFAFC3E1),
                                    )
                                }
                            }
                        },
                        secondaryText = {
                            Text(text = it.address)
                        },
                        icon = {
                            WalletAvatar(
                                walletData = it,
                                modifier = Modifier.size(32.dp),
                            )
                        },
                        trailing = when {
                            editMode -> {
                                {
                                    IconButton(
                                        onClick = {
                                            onEditMenuClicked.invoke(it)
                                        },
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_more_square),
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                            selectedWallet == it -> {
                                {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_tick_square),
                                        contentDescription = null
                                    )
                                }
                            }
                            else -> {
                                null
                            }
                        }
                    )
                }
                item {
                    MaskListCardItem(
                        modifier = Modifier.clickable(
                            onClick = {
                                onWalletConnectClicked.invoke()
                            },
                            enabled = !editMode,
                        ),
                        text = {
                            Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_wallet_connect_wallet_connect))
                        },
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.mask1),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                            )
                        },
                        trailing = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_right_4),
                                contentDescription = null,
                            )
                        }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WalletSwitchAddModal(
    onCreate: () -> Unit,
    onImport: () -> Unit,
) {
    MaskModal {
        Column(
            modifier = Modifier.padding(ScaffoldPadding)
        ) {
            Card(
                elevation = 0.dp,
                onClick = onCreate,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp),
                ) {
                    Image(
                        painterResource(id = R.drawable.ic_plus),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_wallets_list_add_wallets_items_add),
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                elevation = 0.dp,
                onClick = onImport,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp),
                ) {
                    Image(
                        painterResource(id = R.drawable.ic_import3),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.common_controls_import_wallet),
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WalletSwitchModal(
    walletData: WalletData,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onDisconnect: () -> Unit,
) {
    MaskModal {
        Column(
            modifier = Modifier.padding(ScaffoldPadding)
        ) {
            Card(
                elevation = 0.dp,
                onClick = onRename,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp),
                ) {
                    Image(
                        painterResource(id = R.drawable.edit),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_wallet_edit_item_rename),
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = walletData.name)
                }
            }
            if (!walletData.fromWalletConnect) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    elevation = 0.dp,
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp),
                    ) {
                        Image(
                            painterResource(id = R.drawable.delete),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_wallet_edit_item_delete),
                            style = MaterialTheme.typography.subtitle1,
                            color = Color.Red,
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    elevation = 0.dp,
                    onClick = onDisconnect,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painterResource(id = R.drawable.disconnect),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_wallet_connect_disconnect),
                            style = MaterialTheme.typography.subtitle1,
                            color = Color.Red,
                        )
                    }
                }
            }
        }
    }
}