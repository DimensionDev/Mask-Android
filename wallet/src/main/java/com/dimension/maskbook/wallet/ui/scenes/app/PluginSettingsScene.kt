package com.dimension.maskbook.wallet.ui.scenes.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimension.maskbook.wallet.ui.widget.*
import com.dimension.maskbook.wallet.viewmodel.app.PluginDisplayData
import com.dimension.maskbook.wallet.viewmodel.app.PluginSettingsViewModel
import org.koin.androidx.compose.viewModel

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
                items(apps) { item ->
                    PluginSettingsItem(
                        item = item,
                        onItemCheckedChange = { enabled ->
                            viewModel.setEnabled(item.key, enabled)
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
                text = stringResource(item.name),
            )
        },
        trailing = {
            IosSwitch(
                checked = item.enabled,
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
            lineHeight = 21.sp,
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
