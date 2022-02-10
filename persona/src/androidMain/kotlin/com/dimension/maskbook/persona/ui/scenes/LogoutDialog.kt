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
package com.dimension.maskbook.wallet.ui.scenes.persona

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
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.common.ui.widget.PrimaryButton
import com.dimension.maskbook.common.ui.widget.SecondaryButton
import com.dimension.maskbook.persona.R

@Composable
fun LogoutDialog(
    onBack: () -> Unit,
    onDone: () -> Unit,
) {
    MaskDialog(
        onDismissRequest = { /*TODO*/ },
        icon = {
            Image(
                painterResource(id = R.drawable.ic_property_1_note),
                contentDescription = null
            )
        },
        title = {
            Text(text = stringResource(R.string.common_alert_persona_logout_title))
        },
        text = {
            Text(text = stringResource(R.string.common_alert_persona_logout_description))
        },
        buttons = {
            Row {
                SecondaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = onBack,
                ) {
                    Text(text = stringResource(R.string.common_controls_cancel))
                }
                Spacer(modifier = Modifier.width(20.dp))
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = onDone,
                ) {
                    Text(text = stringResource(R.string.common_controls_confirm))
                }
            }
        }
    )
}
