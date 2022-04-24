/*
 *  Mask-Android
 *
 *  Copyright (C) 2022  DimensionDev and Contributors
 *
 *  This file is part of Mask-Android.
 *
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Mask-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.labs.ui.scenes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.route.navigationComposeAnimComposable
import com.dimension.maskbook.common.route.navigationComposeAnimComposablePackage
import com.dimension.maskbook.common.routeProcessor.annotations.Back
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.ui.widget.IosSwitch
import com.dimension.maskbook.common.ui.widget.MaskListItem
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskScene
import com.dimension.maskbook.common.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.common.ui.widget.ScaffoldPadding
import com.dimension.maskbook.common.ui.widget.TipMessageDialog
import com.dimension.maskbook.common.ui.widget.button.MaskBackButton
import com.dimension.maskbook.common.ui.widget.button.MaskButton
import com.dimension.maskbook.labs.R
import com.dimension.maskbook.labs.route.LabsRoute
import com.dimension.maskbook.labs.viewmodel.PluginDisplayData
import com.dimension.maskbook.labs.viewmodel.PluginSettingsViewModel
import moe.tlaster.koin.compose.getViewModel

@NavGraphDestination(
    route = LabsRoute.PluginSettings,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun PluginSettingsScene(
    @Back onBack: () -> Unit,
) {
    val viewModel = getViewModel<PluginSettingsViewModel>()
    val apps by viewModel.apps.collectAsState()
    val shouldShowPluginSettingsTipDialog by viewModel.shouldShowPluginSettingsTipDialog.collectAsState()
    MaskScene {
        MaskScaffold(
            topBar = {
                MaskSingleLineTopAppBar(
                    title = {
                        Text(text = stringResource(R.string.scene_plugin_settings_title))
                    },
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier.padding(padding)
            ) {
                LazyColumn(
                    contentPadding = ScaffoldPadding,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(apps) { item ->
                        PluginSettingsItem(
                            item = item,
                            onClick = {
                                viewModel.setEnabled(item.key, !item.enabled)
                            },
                        )
                    }
                }
                if (shouldShowPluginSettingsTipDialog) {
                    TipMessageDialog(
                        modifier = Modifier
                            .padding(horizontal = 22.5f.dp, vertical = 44.dp)
                            .align(Alignment.BottomCenter),
                        onClose = {
                            viewModel.setShowPluginSettingsTipDialog(false)
                        },
                        text = {
                            Text(
                                text = stringResource(R.string.scene_plugin_settings_message_tips),
                                color = Color.White,
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PluginSettingsItem(
    item: PluginDisplayData,
    onClick: () -> Unit,
) {
    MaskButton(onClick = onClick) {
        MaskListItem(
            icon = {
                Image(
                    painter = painterResource(id = item.onIcon),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
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
                    onCheckedChange = null,
                )
            }
        )
    }
}
