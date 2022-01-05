package com.dimension.maskbook.wallet.ui.scenes.persona.contacts

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
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
        LazyColumn {
            items(items) {
                ListItem(
                    icon = {
                        Icon(Icons.Default.AccountCircle, contentDescription = null)
                    },
                    text = {
                        Text(text = it.name)
                    },
                    trailing = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Default.MoreHoriz, contentDescription = null)
                        }
                    }
                )
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
        Text(text = "You don’t have encrypted friends. \n" +
                "Invite friends to download Mask Network.")
        Spacer(modifier = Modifier.height(24.dp))
        PrimaryButton(onClick = {
            context.startActivity(
                Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Share Mask Network Link:\nhttps://mask.io/download-links/")
                    type = "text/plain"
                }
            )
        }) {
            Text(text = "Invite")
        }
    }
}