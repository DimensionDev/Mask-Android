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
package com.dimension.maskbook.persona.ui.scenes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.common.route.navigationComposeBottomSheet
import com.dimension.maskbook.common.route.navigationComposeBottomSheetPackage
import com.dimension.maskbook.common.route.navigationComposeDialog
import com.dimension.maskbook.common.route.navigationComposeDialogPackage
import com.dimension.maskbook.common.routeProcessor.annotations.Back
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.MaskPasswordInputField
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.common.ui.widget.button.SecondaryButton
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.persona.route.PersonaRoute
import com.dimension.maskbook.persona.viewmodel.PersonaLogoutViewModel
import moe.tlaster.precompose.navigation.NavController
import org.koin.androidx.compose.getViewModel

@NavGraphDestination(
    route = PersonaRoute.Logout,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
)
@Composable
fun LogoutDialog(
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    val viewModel = getViewModel<PersonaLogoutViewModel>()
    val done by viewModel.done.observeAsState()
    val loading by viewModel.loadingState.observeAsState()
    if (done) {
        navController.popBackStack(CommonRoute.Main.Home.path, inclusive = false)
    }
    MaskDialog(
        onDismissRequest = { /*TODO*/ },
        icon = {
            Image(
                painterResource(id = R.drawable.ic_note),
                contentDescription = null
            )
        },
        title = {
            Text(text = stringResource(R.string.common_alert_persona_logout_title))
        },
        text = {
            Text(text = stringResource(R.string.common_alert_persona_logout_description))
        },
        buttons = {
            Row {
                SecondaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = onBack,
                    enabled = !loading && !done
                ) {
                    Text(text = stringResource(R.string.common_controls_cancel))
                }
                Spacer(modifier = Modifier.width(20.dp))
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.logout()
                    },
                    enabled = !loading && !done
                ) {
                    Text(text = stringResource(R.string.common_controls_confirm))
                }
            }
        }
    )
}

@NavGraphDestination(
    route = PersonaRoute.LogoutBeforeCheck,
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun LogoutBeforeCheckModal(
    navController: NavController,
) {
    val viewModel = getViewModel<PersonaLogoutViewModel>()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()

    MaskModal(
        title = {
            Text(stringResource(R.string.scene_backup_password_verify_title))
        }
    ) {
        Column {
            MaskPasswordInputField(
                value = password,
                onValueChange = { viewModel.setPassword(it) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(20.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = confirmPassword,
                onClick = {
                    navController.navigate(PersonaRoute.Logout)
                },
            ) {
                Text(text = stringResource(com.dimension.maskbook.localization.R.string.common_controls_next))
            }
        }
    }
}
