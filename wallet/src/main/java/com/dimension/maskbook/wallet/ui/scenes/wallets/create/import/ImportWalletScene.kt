package com.dimension.maskbook.wallet.ui.scenes.wallets.create.import

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.MaskBackButton
import com.dimension.maskbook.wallet.ui.widget.MaskCard
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskTopAppBar
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding

@Composable
fun ImportWalletScene(
    onBack: () -> Unit,
    onMnemonic: () -> Unit,
    onPassword: () -> Unit,
    onKeystore: () -> Unit,
) {
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskTopAppBar(
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    },
                    title = {
                        Text(text = stringResource(R.string.scene_wallet_list_wallets_items_import))
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ScaffoldPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ImportItem(
                    icon = R.drawable.ic_wallet_mnemonic,
                    title = stringResource(R.string.scene_wallet_import_item_mnemonic_words),
                    subtitle = stringResource(R.string.scene_wallet_import_item_mnemonic_words_desc),
                    onClick = {
                        onMnemonic.invoke()
                    }
                )
                ImportItem(
                    icon = R.drawable.ic_wallet_password,
                    title = stringResource(R.string.scene_identity_privatekey_import_title),
                    subtitle = stringResource(R.string.scene_wallet_import_item_private_key_desc),
                    onClick = {
                        onPassword.invoke()
                    }
                )
                ImportItem(
                    icon = R.drawable.ic_wallet_keystore,
                    title = stringResource(R.string.scene_wallet_import_item_keystore),
                    subtitle = stringResource(R.string.scene_wallet_import_item_keystore_desc),
                    onClick = {
                        onKeystore.invoke()
                    }
                )
            }
        }
    }
}

@Composable
private fun ImportItem(
    @DrawableRes icon: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    MaskCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick.invoke() }
            .padding(vertical = 8.dp, horizontal = 12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(painter = painterResource(id = icon), contentDescription = null)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                )
                Text(text = subtitle)
            }
            Icon(imageVector = Icons.Default.ArrowRight, contentDescription = null)
        }
    }
}