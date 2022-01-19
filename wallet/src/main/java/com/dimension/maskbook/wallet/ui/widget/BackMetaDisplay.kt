package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.repository.BackupMeta
import com.dimension.maskbook.wallet.ui.scenes.settings.MetaItem


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
