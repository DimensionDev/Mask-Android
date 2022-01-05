package com.dimension.maskbook.wallet.ui.scenes.persona.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Pages
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.viewmodel.persona.post.PostViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostScene() {
    val viewModel = getViewModel<PostViewModel>()
    val items by viewModel.items.observeAsState(initial = emptyList())
    if (!items.any()) {
        EmptyPostScene()
    } else {
        LazyColumn {
            items(items) {
                ListItem(
                    icon = {
                        Icon(Icons.Default.Pages, contentDescription = null)
                    },
                    text = {
                        Text(text = it.title)
                    },
                    trailing = {
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyPostScene() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painterResource(id = com.dimension.maskbook.wallet.R.drawable.ic_group_130),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "You haven’t created any post yet.")
        Spacer(modifier = Modifier.height(24.dp))
        PrimaryButton(onClick = {

        }) {
            Text(text = "Create Post")
        }
    }
}