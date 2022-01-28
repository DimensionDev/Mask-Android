package com.dimension.maskbook.wallet.ui.scenes.wallets.management

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.applyTextStyle
import com.dimension.maskbook.wallet.repository.WalletData
import com.dimension.maskbook.wallet.ui.widget.MaskButton
import com.dimension.maskbook.wallet.ui.widget.MaskListItem
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WalletSwitchEditModal(
    walletData: WalletData,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onDisconnect: () -> Unit,
) {
    MaskModal {
        Column(
            modifier = Modifier.padding(ScaffoldPadding)
        ) {
            WalletSwitchItem(
                onClick = onRename,
                icon = R.drawable.ic_rename_wallet,
                text = {
                    Text(text = stringResource(R.string.scene_wallet_edit_item_rename))
                },
                trailing = {
                    Text(text = walletData.name)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (walletData.fromWalletConnect) {
                WalletSwitchItem(
                    onClick = onDisconnect,
                    icon = R.drawable.ic_disconnect,
                    text = {
                        Text(
                            text = stringResource(R.string.scene_wallet_connect_disconnect),
                            color = Color.Red,
                        )
                    }
                )
            } else {
                WalletSwitchItem(
                    onClick = onDelete,
                    icon = R.drawable.ic_delete_wallet,
                    text = {
                        Text(
                            text = stringResource(R.string.scene_wallet_edit_item_delete),
                            color = Color.Red,
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun WalletSwitchItem(
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    text: @Composable (() -> Unit),
    trailing: @Composable (() -> Unit)? = null,
) {
    MaskButton(onClick = onClick) {
        MaskListItem(
            icon = {
                Image(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                )
            },
            text = text,
            trailing = applyTextStyle(MaterialTheme.typography.h5, trailing),
        )
    }
}
