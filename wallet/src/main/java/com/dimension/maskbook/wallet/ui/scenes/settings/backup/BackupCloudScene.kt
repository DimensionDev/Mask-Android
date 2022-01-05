package com.dimension.maskbook.wallet.ui.scenes.settings.backup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.scenes.settings.MetaItem
import com.dimension.maskbook.wallet.ui.widget.*
import com.dimension.maskbook.wallet.viewmodel.settings.BackupCloudViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun BackupCloudScene(
    onBack: () -> Unit,
    onConfirm: (withWallet: Boolean) -> Unit,
) {
    val viewModel = getViewModel<BackupCloudViewModel>()
    val meta by viewModel.meta.observeAsState(initial = null)
    val withWallet by viewModel.withLocalWallet.observeAsState(initial = false)
    val backupPassword by viewModel.backupPassword.observeAsState(initial = "")
    val backupPasswordValid by viewModel.backupPasswordValid.observeAsState(initial = false)
    val paymentPassword by viewModel.paymentPassword.observeAsState(initial = "")
    val paymentPasswordValid by viewModel.paymentPasswordValid.observeAsState(initial = false)
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskTopAppBar(
                    title = {
                        Text(text = "Back up to cloud")
                    },
                    navigationIcon = {
                        MaskBackButton(
                            onBack = onBack
                        )
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(ScaffoldPadding),
            ) {
                meta?.let { meta ->
                    BackMetaDisplay(meta)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.clickable {
                            viewModel.setWithLocalWallet(!withWallet)
                        },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(checked = withWallet, onCheckedChange = {
                            viewModel.setWithLocalWallet(it)
                        })
                        Spacer(modifier = Modifier.width(10.dp))
                        MetaItem(
                            title = "Local Wallet",
                            value = meta.wallet.toString()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Backup Password")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = backupPassword,
                    onValueChange = {
                        viewModel.setBackupPassword(it)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                )
                if (withWallet) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Payment Password")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = paymentPassword,
                        onValueChange = {
                            viewModel.setPaymentPassword(it)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.weight(1f))
                PrimaryButton(
                    onClick = {
                        onConfirm.invoke(withWallet)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = backupPasswordValid && (withWallet && paymentPasswordValid)
                ) {
                    Text(text = "Back Up")
                }
            }
        }
    }
}