package com.dimension.maskbook.wallet.ui.scenes.wallets.create.import

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.*

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
                        Text(text = "Import wallet")
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
                    title = "Mnemonic words",
                    subtitle = "The mnemonic is composed of words and separated by spaces",
                    onClick = {
                        onMnemonic.invoke()
                    }
                )
                ImportItem(
                    icon = R.drawable.ic_wallet_password,
                    title = "Private Key",
                    subtitle = "Plaintext private key ",
                    onClick = {
                        onPassword.invoke()
                    }
                )
                ImportItem(
                    icon = R.drawable.ic_wallet_keystore,
                    title = "Keystore",
                    subtitle = "Encrypted private key JSON content. ",
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