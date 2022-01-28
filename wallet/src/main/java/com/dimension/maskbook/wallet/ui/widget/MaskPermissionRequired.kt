/*
 *  Mask-Android
 *
 *  Copyright (C) DimensionDev and Contributors
 * 
 *  This file is part of Mask-Android.
 * 
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Mask-Android. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.wallet.ui.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MaskPermissionsRequired(
    permissions: List<String>,
    feature: String,
    onPermissionDenied: () -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val multiplePermissionsState = rememberMultiplePermissionsState(permissions)
    val permissionNames = permissions.names(context)
    PermissionsRequired(
        multiplePermissionsState = multiplePermissionsState,
        permissionsNotGrantedContent = {
            RequestDialog(
                onCancel = onPermissionDenied,
                onRequestPermission = { multiplePermissionsState.launchMultiplePermissionRequest() },
                permissions = permissionNames,
                feature = feature
            )
        },
        permissionsNotAvailableContent = {
            PermissionDeniedDialog(
                navigateToSettingsScreen = {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                    )
                },
                onCancel = onPermissionDenied,
                permissions = permissionNames
            )
        }
    ) {
        content()
    }
}

@Composable
private fun List<String>.names(context: Context): String {
    val pm = context.packageManager
    return this.map {
        pm.getPermissionInfo(it, 0).let { info ->
            info.group?.let { group ->
                pm.getPermissionGroupInfo(group, 0).loadLabel(pm)
            }.takeIf { it != "android.permission-group.UNDEFINED" } ?: info.loadLabel(pm)
        }
    }.joinToString { it }
}

@Composable
private fun RequestDialog(
    onCancel: () -> Unit,
    onRequestPermission: () -> Unit,
    permissions: String,
    feature: String,
) {
    MaskDialog(
        title = { Text(stringResource(R.string.common_alert_permissions_title, permissions)) },
        text = { Text(stringResource(R.string.common_alert_permissions_description, feature)) },
        buttons = {
            Row {
                SecondaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = onCancel
                ) {
                    Text(text = stringResource(R.string.common_alert_permissions_btn_cancel))
                }
                Spacer(modifier = Modifier.width(20.dp))
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = onRequestPermission,
                ) {
                    Text(text = stringResource(R.string.common_alert_permissions_btn_ok))
                }
            }
        },
        onDismissRequest = onCancel
    )
}

@Composable
private fun PermissionDeniedDialog(
    navigateToSettingsScreen: () -> Unit,
    onCancel: () -> Unit,
    permissions: String,
) {
    MaskDialog(
        title = { Text(stringResource(R.string.common_alert_permissions_title, permissions)) },
        text = { Text(stringResource(R.string.common_alert_permissions_open_settings)) },
        buttons = {
            Row {
                SecondaryButton(
                    onClick = onCancel
                ) {
                    Text(text = stringResource(R.string.common_alert_permissions_btn_cancel))
                }
                Spacer(modifier = Modifier.width(20.dp))
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = navigateToSettingsScreen,
                ) {
                    Text(text = stringResource(R.string.common_alert_permissions_btn_settings))
                }
            }
        },
        onDismissRequest = onCancel
    )
}
