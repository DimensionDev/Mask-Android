package com.dimension.maskbook.wallet.ui.scenes.wallets.management

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.repository.WalletData
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WalletManagementModal(
    walletData: WalletData,
    onRename: () -> Unit,
    onBackup: () -> Unit,
    onTransactionHistory: () -> Unit,
    onDelete: () -> Unit,
    onDisconnect: () -> Unit,
) {
    MaskModal {
        Column(
            modifier = Modifier
                .padding(ScaffoldPadding),
        ) {
            Text(
                text = walletData.address,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(21.dp))
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
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                elevation = 0.dp,
                onClick = onTransactionHistory,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp),
                ) {
                    Image(
                        painterResource(id = R.drawable.time_circle),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_wallet_detail_wallet_items_history),
                        style = MaterialTheme.typography.subtitle1,
                    )
                }
            }
            if (!walletData.fromWalletConnect) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    elevation = 0.dp,
                    onClick = onBackup,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp),
                    ) {
                        Image(
                            painterResource(id = R.drawable.paper_plus),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_personas_action_backup),
                            style = MaterialTheme.typography.subtitle1,
                        )
                    }
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
                        modifier = Modifier.padding(12.dp),
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