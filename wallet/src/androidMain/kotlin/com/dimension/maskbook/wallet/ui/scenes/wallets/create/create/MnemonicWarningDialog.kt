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
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.wallet.R

@Composable
fun MnemonicWarningDialog(onDismissRequest: () -> Unit) {
    MaskDialog(
        onDismissRequest = { onDismissRequest.invoke() },
        title = {
            Text(text = "Store Mnemonic Phrase safely")
        },
        text = {
            Text(text = "Your mnemonic phrase is composed of randomly selected words. Please carefully write down each word in the order it appears.")
        },
        icon = {
            Image(
                painter = painterResource(id = R.drawable.ic_property_1_note),
                contentDescription = null
            )
        },
        buttons = {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onDismissRequest.invoke() },
            ) {
                Text(text = stringResource(R.string.common_controls_ok))
            }
        }
    )
}
