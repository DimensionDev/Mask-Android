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
package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.localization.R

@Composable
fun BackMetaDisplay(
    meta: BackupMeta,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            MetaItem(title = stringResource(R.string.scene_backup_restored_account), value = meta.account)
            Spacer(modifier = Modifier.height(16.dp))
            MetaItem(title = stringResource(R.string.tab_personas), value = meta.personas.toString())
            Spacer(modifier = Modifier.height(16.dp))
            MetaItem(
                title = stringResource(R.string.scene_backup_restored_account),
                value = meta.associatedAccount.toString()
            )
            Spacer(modifier = Modifier.height(16.dp))
            MetaItem(
                title = stringResource(R.string.scene_backup_restored_post),
                value = meta.encryptedPost.toString()
            )
            Spacer(modifier = Modifier.height(16.dp))
            MetaItem(title = stringResource(R.string.scene_backup_restored_contacts), value = meta.contacts.toString())
            Spacer(modifier = Modifier.height(16.dp))
            MetaItem(title = stringResource(R.string.scene_backup_restored_files), value = meta.file.toString())
        }
    }
}

data class BackupMeta(
    val account: String,
    val personas: Int,
    val associatedAccount: Int,
    val encryptedPost: Int,
    val contacts: Int,
    val file: Int,
    val wallet: Int,
    val json: String,
)

@Composable
fun MetaItem(
    title: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title)
        Spacer(modifier = Modifier.weight(1f))
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.button
        ) {
            Text(text = value)
        }
    }
}
