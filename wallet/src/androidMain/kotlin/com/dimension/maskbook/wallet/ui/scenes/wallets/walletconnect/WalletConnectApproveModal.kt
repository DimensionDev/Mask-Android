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
package com.dimension.maskbook.wallet.ui.scenes.wallets.walletconnect

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.dimension.maskbook.common.ext.ifNullOrEmpty
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.MiddleEllipsisText
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.common.ui.widget.button.SecondaryButton
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.export.model.ChainData
import com.dimension.maskbook.wallet.export.model.WalletData
import com.dimension.maskbook.wallet.walletconnect.WCClientMeta

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WalletConnectApproveModal(
    client: WCClientMeta?,
    wallet: WalletData?,
    currentChain: ChainData?,
    onSwitchWallet: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    MaskModal {
        if (client == null) {
            Box(modifier = Modifier.height(412.dp)) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            return@MaskModal
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(24.dp))
            Image(
                rememberImagePainter(client.icons.firstOrNull()),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "${client.name} wants to connect to your wallet",
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(horizontal = 72.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            client.url.takeIf { it.isNotEmpty() }?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.h6,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().clickable {
                    onSwitchWallet()
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("${wallet?.name ?: "no wallet"} (${currentChain?.fullName.ifNullOrEmpty { currentChain?.name }})")
                    Spacer(modifier = Modifier.height(8.dp))
                    MiddleEllipsisText(
                        text = wallet?.address ?: "",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(154.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row {
                SecondaryButton(onClick = onReject, modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.common_controls_cancel),
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                PrimaryButton(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f),
                    enabled = wallet != null
                ) {
                    Text(
                        text = stringResource(R.string.scene_wallet_connect_server_connect),
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
