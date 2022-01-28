/*
 *  Mask-Android
 *
 *  Copyright (C) DimensionDev and Contributors
 * 
 *  This file is part of Mask-Android.
 * 
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Mask-Android. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.wallet.ui.scenes.persona.social

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.widget.MaskDialog
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.SecondaryButton

@Composable
fun DisconnectSocialDialog(
    socialName: String,
    personaName: String,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
) {
    MaskDialog(
        onDismissRequest = { onBack.invoke() },
        icon = {
            Image(
                painterResource(id = R.drawable.ic_property_1_note),
                contentDescription = null
            )
        },
        text = {
            Text(text = stringResource(R.string.common_alert_disconnect_profile_title, socialName, personaName))
        },
        buttons = {
            Row {
                SecondaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onBack.invoke()
                    }
                ) {
                    Text(text = stringResource(R.string.common_controls_cancel))
                }
                Spacer(modifier = Modifier.width(20.dp))
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onConfirm.invoke()
                    },
                ) {
                    Text(text = stringResource(R.string.common_controls_confirm))
                }
            }
        }
    )
}
