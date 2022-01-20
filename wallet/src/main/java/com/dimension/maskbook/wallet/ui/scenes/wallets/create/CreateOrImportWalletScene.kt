package com.dimension.maskbook.wallet.ui.scenes.wallets.create

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.LocalRootNavController
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.MaskBackButton
import com.dimension.maskbook.wallet.ui.widget.MaskDialog
import com.dimension.maskbook.wallet.ui.widget.MaskIconButton
import com.dimension.maskbook.wallet.ui.widget.MaskInputField
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding

@Composable
fun CreateOrImportWalletScene(
    onBack: () -> Unit,
    type: CreateType
) {
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskSingleLineTopAppBar(
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    },
                )
            }
        ) {
            val rootNavController = LocalRootNavController.current
            var input by remember {
                mutableStateOf("")
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ScaffoldPadding),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_create_wallet_logo),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(31.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_multi_chain_logo),
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.scene_create_wallet_multichain_wallet_title),
                        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1F)
                    )
                    MaskIconButton(
                        onClick = {
                            rootNavController.navigate("MultiChainWalletDialog")
                        }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_doubt),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.primary) {
                    Text(text = stringResource(R.string.scene_create_wallet_wallet_name))
                }

                Spacer(modifier = Modifier.height(8.dp))
                MaskInputField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = stringResource(R.string.scene_create_wallet_wallet_name_placeholder))
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        when (type) {
                            CreateType.CREATE -> rootNavController.navigate("CreateWallet/${input}")
                            CreateType.IMPORT -> rootNavController.navigate("ImportWallet/${input}")
                        }
                    },
                    enabled = input.isNotEmpty()
                ) {
                    Text(text = stringResource(R.string.common_controls_accept))
                }
            }
        }
    }
}

@Composable
private fun CreateSuccessDialog(onDismissRequest: () -> Unit) {
    MaskDialog(
        onDismissRequest = { /*TODO*/ },
        title = { Text(text = "Wallet successfully created!") },
        icon = {
            Image(
                painter = painterResource(id = R.drawable.ic_property_1_snccess),
                contentDescription = null
            )
        },
        buttons = {
            PrimaryButton(
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.common_controls_done))
            }
        }
    )
}

enum class CreateType {
    CREATE,
    IMPORT
}