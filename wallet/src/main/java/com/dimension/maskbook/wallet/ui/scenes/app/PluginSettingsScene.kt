package com.dimension.maskbook.wallet.ui.scenes.app

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
    var isShowTipDialog by remember { mutableStateOf(true) }
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
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
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
            if (isShowTipDialog) {
                TipDialog(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    onClose = {
                        isShowTipDialog = false
                    }
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

@Composable
private fun TipDialog(
    modifier: Modifier,
    onClose: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = 22.dp, vertical = 44.dp)
            .shadow(12.dp, RoundedCornerShape(12.dp))
            .background(
                brush = PluginSettingsItemDefault.tipDialogBackGround,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(start = 16.dp, end = 0.dp, top = 10.dp, bottom = 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = PluginSettingsItemDefault.tipMessage,
            color = Color.White,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                tint = Color.White,
            )
        }
    }
}

private object PluginSettingsItemDefault {
    const val title = "Plugin Settings"
    const val tipMessage =
        "If you turn off a plugin, the plugin function can no longer be rendered on timeline when browsing social media."
    val tipDialogBackGround = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1C68F3),
            Color(0xFF499DFF)
        ),
    )
}
