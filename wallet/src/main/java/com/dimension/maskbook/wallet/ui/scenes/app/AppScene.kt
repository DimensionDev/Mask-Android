package com.dimension.maskbook.wallet.ui.scenes.app

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.AppData
import com.dimension.maskbook.wallet.ui.LocalRootNavController
import com.dimension.maskbook.wallet.ui.widget.*
import com.dimension.maskbook.wallet.viewmodel.app.AppViewModel
import org.koin.androidx.compose.getViewModel

private data class AppDisplayData(
    val id: String,
    @DrawableRes val onIcon: Int,
    @DrawableRes val offIcon: Int,
    val name: String,
    val canExplore: Boolean = false,
    val settingRoute: String? = null
)

private val items = listOf(
    AppDisplayData(
        id = "com.maskbook.fileservice",
        name = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_app_plugins_file_service),
        onIcon = R.drawable.encrypted_file,
        offIcon = R.drawable.encrypted_file_1,
        canExplore = true,
    ),
    AppDisplayData(
        id = "co.gitcoin",
        name = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_app_plugins_gitcoin),
        onIcon = R.drawable.gitcoin,
        offIcon = R.drawable.gitcoin_1
    ),
    AppDisplayData(
        id = "co.dhedge",
        name = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_app_plugins_dhedge),
        onIcon = R.drawable.dhedge,
        offIcon = R.drawable.dhedge_1
    ),
    AppDisplayData(
        id = "com.maskbook.red_packet",
        name = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_app_plugins_lucy_drop),
        onIcon = R.drawable.packet,
        offIcon = R.drawable.packet_1,
        canExplore = true,
    ),
    AppDisplayData(
        id = "com.maskbook.transak",
        name = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_app_plugins_transaction),
        onIcon = R.drawable.transak,
        offIcon = R.drawable.transak_1,
        canExplore = true,
    ),
    AppDisplayData(
        id = "com.maskbook.collectibles",
        name = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_app_plugins_collectibles),
        onIcon = R.drawable.collectibles,
        offIcon = R.drawable.collectibles1
    ),
    AppDisplayData(
        id = "org.snapshot",
        name = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_app_plugins_snapshot),
        onIcon = R.drawable.snapshot,
        offIcon = R.drawable.snapshot_1
    ),
    AppDisplayData(
        id = "com.maskbook.ito",
        name = "Markets",
        onIcon = R.drawable.markets,
        offIcon = R.drawable.markets_1,
        canExplore = true,
    ),
    AppDisplayData(
        id = "com.maskbook.tweet",
        name = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_app_plugins_valuables),
        onIcon = R.drawable.union,
        offIcon = R.drawable.union_1
    ),
    AppDisplayData(
        id = "com.maskbook.trader",
        name = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_app_plugins_market_trend),
        onIcon = R.drawable.market_trend,
        offIcon = R.drawable.market_trend_1,
        settingRoute = "MarketTrendSettings"
    ),
).sortedByDescending { it.canExplore }

@Composable
fun AppScene(
    onBack: () -> Unit,
) {
    val viewModel: AppViewModel = getViewModel()
    val apps by viewModel.apps.observeAsState(initial = emptyList())
    MaskScaffold(
        topBar = {
            MaskSingleLineTopAppBar(
                navigationIcon = {
                    MaskBackButton(onBack = onBack)
                },
            )
        }
    ) {
        LazyColumn(
            contentPadding = ScaffoldPadding
        ) {
            itemsGridIndexed(items, spacing = 15.dp, rowSize = 2) { _, item ->
                val data = apps.firstOrNull { it.id == item.id }
                if (data != null) {
                    AppItem(
                        displayData = item,
                        data = data,
                        onEnabledChanged = { viewModel.setEnabled(item.id, it) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AppItem(
    displayData: AppDisplayData,
    data: AppData,
    onEnabledChanged: (Boolean) -> Unit,
) {
    Card(
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 16.dp,
            )
        ) {
            Row {
                if (data.enabled) {
                    Image(painterResource(id = displayData.onIcon), contentDescription = null)
                } else {
                    Image(painterResource(id = displayData.offIcon), contentDescription = null)
                }
                Spacer(modifier = Modifier.weight(1f))
                if (displayData.settingRoute != null) {
                    val rootNavController = LocalRootNavController.current
                    Icon(
                        painterResource(id = R.drawable.ic_setting),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            rootNavController.navigate(displayData.settingRoute)
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = displayData.name, style = MaterialTheme.typography.subtitle1)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
//                if (displayData.canExplore) {
//                    PrimaryCompatButton(
//                        onClick = { /*TODO*/ },
//                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
//                    ) {
//                        Text(text = "Explore")
//                    }
//                }
//                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = data.enabled,
                    onCheckedChange = onEnabledChanged,
                )
            }
        }
    }
}