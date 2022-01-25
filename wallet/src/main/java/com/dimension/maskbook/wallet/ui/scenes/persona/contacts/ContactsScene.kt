package com.dimension.maskbook.wallet.ui.scenes.persona.contacts

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.ui.widget.MaskButton
import com.dimension.maskbook.wallet.ui.widget.MaskIconButton
import com.dimension.maskbook.wallet.ui.widget.MaskListItem
import com.dimension.maskbook.wallet.ui.widget.NameImage
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.viewmodel.persona.contacts.ContactsViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContactsScene() {
    val viewModel: ContactsViewModel = getViewModel()
    val items by viewModel.items.observeAsState(initial = emptyList())
    if (!items.any()) {
        EmptyContactsScene()
    } else {
        LazyColumn(
            modifier = Modifier.padding(ScaffoldPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(items) { item ->
                MaskButton(onClick = {}) {
                    MaskListItem(
                        icon = {
                            NameImage(
                                name = item.name,
                                modifier = Modifier.size(38.dp),
                            )
                        },
                        text = {
                            Text(text = item.name)
                        },
                        secondaryText = {
                            Text(text = '@' + item.id.substringAfter('/'))
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyContactsScene() {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painterResource(id = R.drawable.ic_group_129),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = stringResource(R.string.scene_persona_contacts_empty_contacts_tips))
        Spacer(modifier = Modifier.height(24.dp))
        PrimaryButton(onClick = {
            context.startActivity(
                Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "${context.resources.getText(R.string.scene_share_shareLink)}\nhttps://mask.io/download-links/")
                    type = "text/plain"
                }
            )
        }) {
            Text(text = stringResource(R.string.common_controls_invite))
        }
    }
}