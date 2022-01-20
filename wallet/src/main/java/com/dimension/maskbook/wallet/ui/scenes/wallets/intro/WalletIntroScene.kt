package com.dimension.maskbook.wallet.ui.scenes.wallets.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.ui.widget.SecondaryButton

@Composable
fun WalletIntroScene(
    onCreate: () -> Unit,
    onImport: () -> Unit,
    onConnect: () -> Unit,
) {
    MaskTheme {
        MaskScaffold {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ScaffoldPadding),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painterResource(id = R.drawable.ic_wallet_intro),
                        contentDescription = null
                    )
                }
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onCreate.invoke()
                    },
                ) {
                    Text(text = stringResource(R.string.common_controls_create_wallet))
                }
                ButtonSpacing()
                SecondaryButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        onImport.invoke()
                    },
                ) {
                    Text(text = stringResource(R.string.common_controls_import_wallet))
                }
                ButtonSpacing()
                SecondaryButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        onConnect.invoke()
                    },
                ) {
                    Text(text = stringResource(R.string.scene_wallet_connect_wallet_connect))
                }
                ButtonSpacing()
            }
        }
    }
}

@Composable
private fun ButtonSpacing() {
    Spacer(modifier = Modifier.height(16.dp))
}