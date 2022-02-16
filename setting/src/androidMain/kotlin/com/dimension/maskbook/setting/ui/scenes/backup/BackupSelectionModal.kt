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

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.setting.R

@Composable
fun BackupSelectionModal(
    onLocal: () -> Unit,
    onRemote: () -> Unit,
) {
    MaskModal(
        title = {
            Text(text = stringResource(com.dimension.maskbook.localization.R.string.scene_setting_backup_data_title))
        }
    ) {
        Column {
            IconButton(
                icon = R.drawable.ic_icloud,
                text = stringResource(com.dimension.maskbook.localization.R.string.common_controls_back_up_to_cloud),
                onClick = onRemote,
            )
            Spacer(modifier = Modifier.height(16.dp))
            IconButton(
                icon = R.drawable.ic_iphone,
                text = stringResource(com.dimension.maskbook.localization.R.string.common_controls_back_up_locally),
                onClick = onLocal,
            )
        }
    }
}

@Composable
private fun IconButton(
    @DrawableRes icon: Int,
    text: String,
    onClick: () -> Unit,
) {
    PrimaryButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Icon(
            painterResource(id = icon),
            contentDescription = null,
            tint = LocalContentColor.current
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}
