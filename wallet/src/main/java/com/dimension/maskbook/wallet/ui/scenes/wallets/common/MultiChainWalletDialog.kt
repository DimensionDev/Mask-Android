package com.dimension.maskbook.wallet.ui.scenes.wallets.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.LocalRootNavController
import com.dimension.maskbook.wallet.ui.widget.MaskDialog
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton

@Composable
fun MultiChainWalletDialog() {
    val rootNavController = LocalRootNavController.current
    MaskDialog(
        onDismissRequest = { rootNavController.popBackStack() },
        title = {
            Text(text = "Multi-chain wallet")
        },
        text = {
            Column {
                Text(text = "Our EVM compatible wallet supports the following blockchain networks:")
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoItem(icon = R.drawable.ic_chain_eth, name = "ETH")
                    InfoItem(icon = R.drawable.ic_chain_bsc, name = "BSC")
                    InfoItem(icon = R.drawable.ic_chain_arbitrum, name = "Arbitrum")
                    InfoItem(icon = R.drawable.ic_chain_optimism, name = "Optimism")
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoItem(icon = R.drawable.ic_chain_matic, name = "Matic")
                    InfoItem(icon = R.drawable.ic_chain_xdai, name = "Xdai")
                    InfoItem(icon = R.drawable.ic_chain_ropsten, name = "Ropsten")
                    InfoItem(icon = R.drawable.ic_chain_rinkeby, name = "Rinkeby")
                }
            }
        },
        buttons = {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { rootNavController.popBackStack() },
            ) {
                Text(text = "Ok")
            }
        }
    )
}

@Composable
private fun InfoItem(@DrawableRes icon: Int, name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            modifier = Modifier.size(48.dp),
            painter = painterResource(id = icon),
            contentDescription = null
        )
        Text(text = name, modifier = Modifier.padding(top = 10.dp))
    }
}