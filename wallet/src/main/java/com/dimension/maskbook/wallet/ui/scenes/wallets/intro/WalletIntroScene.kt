package com.dimension.maskbook.wallet.ui.scenes.wallets.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.widget.MaskIconCardButton
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding

@Composable
fun WalletIntroScene(
    onCreate: () -> Unit,
    onImport: () -> Unit,
    onConnect: () -> Unit,
) {
    MaskScaffold(
        topBar = {
            MaskSingleLineTopAppBar()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ScaffoldPadding),
        ) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painterResource(id = R.drawable.ic_wallet_intro),
                    contentDescription = null
                )
            }
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onCreate,
            ) {
                Text(text = stringResource(R.string.common_controls_create_wallet))
            }
            ButtonSpacing()
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onImport,
            ) {
                Text(text = stringResource(R.string.common_controls_import_wallet))
            }
            ButtonSpacing()
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onConnect,
            ) {
                Text(text = stringResource(R.string.scene_wallet_connect_wallet_connect))
            }
            ButtonSpacing()
        }
    }
}

@Composable
private fun ButtonSpacing() {
    Spacer(modifier = Modifier.height(16.dp))
}