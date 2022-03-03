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
package com.dimension.maskbook.wallet.ui.scenes.register.recovery

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
fun PersonaAlreadyExitsDialog(
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    restoreFrom: String,
) {
    MaskDialog(
        onDismissRequest = onBack,
        title = {
            Text(stringResource(R.string.scene_setting_backup_recovery_persona_already_exits_title))
        },
        icon = {
            Image(painter = painterResource(R.drawable.ic_warn), contentDescription = "")
        },
        text = {
            Text(stringResource(R.string.scene_setting_backup_recovery_persona_already_exits_desc, restoreFrom))
        },
        buttons = {
            PrimaryButton(onClick = {
                onConfirm.invoke()
            }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.common_controls_ok))
            }
        }
    )
}
