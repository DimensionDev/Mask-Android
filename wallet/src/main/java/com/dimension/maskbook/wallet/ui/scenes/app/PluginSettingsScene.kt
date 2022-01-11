package com.dimension.maskbook.wallet.ui.scenes.app

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.widget.MaskBackButton
import com.dimension.maskbook.wallet.ui.widget.MaskListCardItem
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.wallet.viewmodel.app.PluginSettingsViewModel
import org.koin.androidx.compose.viewModel

data class PluginDisplayData(
    val id: String,
    @DrawableRes val onIcon: Int,
    val name: String,
    val canExplore: Boolean = false,
    val settingRoute: String? = null,
)

private val items = listOf(
    PluginDisplayData(
        id = "com.maskbook.fileservice",
        name = "File Service",
        onIcon = R.drawable.ic_labs_encrypted_file,
        canExplore = true,
    ),
    PluginDisplayData(
        id = "com.maskbook.ito",
        name = "ITO",
        onIcon = R.drawable.ic_labs_ito,
        canExplore = true,
    ),
    PluginDisplayData(
        id = "com.maskbook.red_packet",
        name = "Lucky Drop",
        onIcon = R.drawable.ic_labs_packet,
        canExplore = true,
    ),
    PluginDisplayData(
        id = "com.maskbook.transak",
        name = "Transak",
        onIcon = R.drawable.ic_labs_transak,
        canExplore = true,
    ),
    PluginDisplayData(
        id = "org.snapshot",
        name = "Snapshot",
        onIcon = R.drawable.ic_labs_snapshot,
    ),
    PluginDisplayData(
        id = "co.dhedge",
        name = "DHEDGE",
        onIcon = R.drawable.ic_labs_dhedge,
    ),
    PluginDisplayData(
        id = "co.gitcoin",
        name = "Gitcoin",
        onIcon = R.drawable.ic_labs_gitcoin,
    ),
    PluginDisplayData(
        id = "com.maskbook.trader",
        name = "Market Trend",
        onIcon = R.drawable.ic_labs_market_trend,
        settingRoute = "MarketTrendSettings"
    ),
    PluginDisplayData(
        id = "com.maskbook.collectibles",
        name = "Collectibles",
        onIcon = R.drawable.ic_labs_collectibles,
    ),
    PluginDisplayData(
        id = "com.maskbook.tweet",
        name = "Valuables",
        onIcon = R.drawable.ic_labs_union,
    ),
)

@Composable
fun PluginSettingsScene(
    onBack: () -> Unit
) {
    val viewModel by viewModel<PluginSettingsViewModel>()
    val apps by viewModel.apps.collectAsState()
    MaskScaffold(
        topBar = {
            MaskSingleLineTopAppBar(
                title = {
                    Text(
                        text = PluginSettingsItemDefault.title,
                        style = MaterialTheme.typography.h6,
                    )
                },
                navigationIcon = {
                    MaskBackButton(onBack = onBack)
                }
            )
        }
    ) {
        LazyColumn {
            items(items) { item ->
                PluginSettingsItem(
                    item = item,
                    checked = apps.find { it.id == item.id }?.enabled ?: false,
                    onItemCheckedChange = { enabled ->
                        viewModel.setEnabled(item.id, enabled)
                    },
                )
            }
        }
    }
}

@Composable
private fun PluginSettingsItem(
    item: PluginDisplayData,
    checked: Boolean,
    onItemCheckedChange: (Boolean) -> Unit,
) {
    MaskListCardItem(
        icon = {
            Image(
                painter = painterResource(id = item.onIcon),
                contentDescription = null,
            )
        },
        text = {
            Text(
                text = item.name,
            )
        },
        trailing = {
            Switch(
                checked = checked,
                onCheckedChange = onItemCheckedChange
            )
        }
    )
}

private object PluginSettingsItemDefault {
    const val title = "Plugin Settings"
}
