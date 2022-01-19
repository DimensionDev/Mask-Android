package com.dimension.maskbook.wallet.ui.scenes.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.AppKey
import com.dimension.maskbook.wallet.ui.widget.MaskButton
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.wallet.viewmodel.app.AppDisplayData
import com.dimension.maskbook.wallet.viewmodel.app.LabsViewModel
import org.koin.androidx.compose.getViewModel


@Composable
fun LabsScene(
    onSettingClick: () -> Unit,
    onItemClick: (AppKey) -> Unit,
) {
    val viewModel: LabsViewModel = getViewModel()
    val apps by viewModel.apps.observeAsState(initial = emptyList())
    MaskScaffold(
        topBar = {
            MaskSingleLineTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.tab_labs),
                        style = MaterialTheme.typography.h6,
                    )
                },
                actions = {
                    IconButton(onClick = { onSettingClick() }) {
                        Icon(
                            painterResource(id = R.drawable.ic_setting),
                            contentDescription = null,
                        )
                    }
                }
            )
        }
    ) {
        LazyColumn {
            items(apps) { item ->
                AppItem(
                    item = item,
                    onItemClick = { onItemClick(item.key) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AppItem(
    item: AppDisplayData,
    onItemClick: () -> Unit
) {
    MaskButton(
        onClick = { onItemClick() },
        enabled = item.enabled,
    ) {
        ListItem(
            icon = {
                Image(
                    painter = painterResource(id = item.onIcon),
                    contentDescription = null,
                    modifier = Modifier.padding(vertical = 12.dp),
                    alpha = if (item.enabled) 1f else LocalContentAlpha.current,
                )
            },
            text = {
                Text(
                    text = stringResource(item.name),
                    color = LocalTextStyle.current.color.copy(LocalContentAlpha.current),
                )
            },
            secondaryText = {
                Text(
                    text = item.description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = LocalTextStyle.current.color.copy(LocalContentAlpha.current),
                )
            },
            singleLineSecondaryText = false,
            trailing = {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_right),
                    contentDescription = null,
                    modifier = Modifier.padding(vertical = 22.dp),
                )
            }
        )
    }
}
