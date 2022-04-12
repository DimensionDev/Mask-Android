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
package com.dimension.maskbook.setting.ui.scenes.backup

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ui.widget.BackMetaDisplay
import com.dimension.maskbook.common.ui.widget.MaskPasswordInputField
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskScene
import com.dimension.maskbook.common.ui.widget.MaskTopAppBar
import com.dimension.maskbook.common.ui.widget.MetaItem
import com.dimension.maskbook.common.ui.widget.ScaffoldPadding
import com.dimension.maskbook.common.ui.widget.button.MaskBackButton
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.common.ui.widget.button.clickable
import com.dimension.maskbook.localization.R
import com.dimension.maskbook.setting.viewmodel.BackupCloudViewModel
import moe.tlaster.koin.compose.getViewModel

@Composable
fun BackupCloudScene(
    onBack: () -> Unit,
    onConfirm: (withWallet: Boolean) -> Unit,
) {
    val viewModel = getViewModel<BackupCloudViewModel>()
    val meta by viewModel.meta.collectAsState(initial = null)
    val withWallet by viewModel.withLocalWallet.collectAsState(initial = false)
    val backupPassword by viewModel.backupPassword.collectAsState(initial = "")
    val backupPasswordValid by viewModel.backupPasswordValid.collectAsState(initial = false)
    val paymentPassword by viewModel.paymentPassword.collectAsState(initial = "")
    val paymentPasswordValid by viewModel.paymentPasswordValid.collectAsState(initial = false)
    MaskScene {
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
