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
package com.dimension.maskbook.wallet.ui.scenes.wallets.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.common.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.repository.WalletCreateOrImportResult

@Composable
fun WalletCreateOrImportResult.Dialog(onDismissRequest: () -> Unit) {
    MaskDialog(
        onDismissRequest = {
            onDismissRequest.invoke()
        },
        title = { Title() },
        icon = { Icon() },
        buttons = {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onDismissRequest.invoke()
                }
            ) {
                when (type) {
                    WalletCreateOrImportResult.Type.SUCCESS -> Text(text = stringResource(R.string.common_controls_done))
                    WalletCreateOrImportResult.Type.ERROR -> Text(text = stringResource(R.string.common_controls_ok))
                    WalletCreateOrImportResult.Type.WARNING -> Text(text = stringResource(R.string.common_controls_i_understand))
                }
            }
        },
        text = {
            if (!message.isNullOrEmpty()) Text(text = message)
        }
    )
}

@Composable
private fun WalletCreateOrImportResult.Title() = when (type) {
    WalletCreateOrImportResult.Type.SUCCESS -> Text(text = title ?: "Success")
    WalletCreateOrImportResult.Type.ERROR -> Text(text = title ?: androidx.compose.ui.res.stringResource(R.string.common_alert_wallet_import_alert_title_fail))
    WalletCreateOrImportResult.Type.WARNING -> Text(text = title ?: androidx.compose.ui.res.stringResource(R.string.common_alert_recovery_key_warning_title))
}

@Composable
private fun WalletCreateOrImportResult.Icon() = when (type) {
    WalletCreateOrImportResult.Type.SUCCESS -> Image(
        painter = painterResource(id = R.drawable.ic_property_1_snccess),
        contentDescription = null
    )
    WalletCreateOrImportResult.Type.ERROR -> Image(
        painter = painterResource(id = R.drawable.ic_property_1_failed),
        contentDescription = null
    )
    WalletCreateOrImportResult.Type.WARNING -> Image(
        painter = painterResource(id = R.drawable.ic_property_1_note),
        contentDescription = null
    )
}
