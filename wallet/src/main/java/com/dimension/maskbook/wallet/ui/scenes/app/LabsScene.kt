package com.dimension.maskbook.wallet.ui.scenes.app

import androidx.annotation.DrawableRes
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.ui.widget.MaskButton
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.wallet.viewmodel.app.LabsViewModel
import org.koin.androidx.compose.getViewModel

private data class AppDisplayData(
    val id: String,
    @DrawableRes val onIcon: Int,
    val name: String,
    val description: String,
    val canExplore: Boolean = false,
    val settingRoute: String? = null
)

private val items = listOf(
    AppDisplayData(
        id = "com.maskbook.transak",
        name = "Transak",
        description = "Buy crypto in 60+ countries with Transak support.",
        onIcon = R.drawable.ic_labs_transak,
        canExplore = true,
    ),
    AppDisplayData(
        id = "com.maskbook.ito",
        name = "ITO",
        description = "Enable users to buy tokens directly on Twitter.",
        onIcon = R.drawable.ic_labs_ito,
        canExplore = true,
    ),
    AppDisplayData(
        id = "com.maskbook.red_packet",
        name = "Lucky Drop",
        description = "Surprise your encrypted friends with Token/NFT Lucky Drops.",
        onIcon = R.drawable.ic_labs_packet,
        canExplore = true,
    ),
    // TODO swap ?
    AppDisplayData(
        id = "com.maskbook.fileservice",
        name = "File Service",
        description = "Decentralized file storage for users.",
        onIcon = R.drawable.ic_labs_encrypted_file,
        canExplore = true,
    ),
)

@Composable
fun LabsScene(
    onSettingClick: () -> Unit,
) {
    val viewModel: LabsViewModel = getViewModel()
    val apps by viewModel.apps.observeAsState(initial = emptyList())
    MaskScaffold(
        topBar = {
            MaskSingleLineTopAppBar(
                title = {
                    Text(
                        text = LabsSceneDefault.title,
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
            items(items) { item ->
                AppItem(
                    item = item,
                    enabled = apps.find { it.id == item.id }?.enabled ?: false
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AppItem(
    item: AppDisplayData,
    enabled: Boolean,
) {
    MaskButton(
        onClick = {},
        enabled = enabled,
    ) {
        ListItem(
            icon = {
                Image(
                    painter = painterResource(id = item.onIcon),
                    contentDescription = null,
                    modifier = Modifier.padding(vertical = 12.dp),
                    alpha = if (enabled) 1f else LocalContentAlpha.current,
                )
            },
            text = {
                Text(
                    text = item.name,
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

private object LabsSceneDefault {
    const val title = "Labs"
}
