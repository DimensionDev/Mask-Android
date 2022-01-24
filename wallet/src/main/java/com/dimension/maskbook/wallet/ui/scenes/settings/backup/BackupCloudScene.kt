package com.dimension.maskbook.wallet.ui.scenes.settings.backup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
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
                        Text(text = stringResource(R.string.common_controls_back_up_to_cloud))
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
                            title = stringResource(R.string.scene_setting_local_backup_local_wallet),
                            value = meta.wallet.toString()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = stringResource(R.string.scene_setting_backup_recovery_back_up_password))
                Spacer(modifier = Modifier.height(8.dp))
                MaskPasswordInputField(
                    value = backupPassword,
                    onValueChange = {
                        viewModel.setBackupPassword(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    imeAction = if (withWallet) ImeAction.Next else ImeAction.Done,
                )
                if (withWallet) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = stringResource(R.string.scene_setting_general_setup_payment_password))
                    Spacer(modifier = Modifier.height(8.dp))
                    MaskPasswordInputField(
                        value = paymentPassword,
                        onValueChange = {
                            viewModel.setPaymentPassword(it)
                        },
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
                    enabled = backupPasswordValid && (if (withWallet) paymentPasswordValid else true)
                ) {
                    Text(text = stringResource(R.string.scene_personas_action_backup))
                }
            }
        }
    }
}