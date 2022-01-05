package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            MetaItem(title = "Account", value = meta.account)
            Spacer(modifier = Modifier.height(16.dp))
            MetaItem(title = "Personas", value = meta.personas.toString())
            Spacer(modifier = Modifier.height(16.dp))
            MetaItem(
                title = "Associated account",
                value = meta.associatedAccount.toString()
            )
            Spacer(modifier = Modifier.height(16.dp))
            MetaItem(
                title = "Encrypted post",
                value = meta.encryptedPost.toString()
            )
            Spacer(modifier = Modifier.height(16.dp))
            MetaItem(title = "Contacts", value = meta.contacts.toString())
            Spacer(modifier = Modifier.height(16.dp))
            MetaItem(title = "File", value = meta.file.toString())
        }
    }
}
