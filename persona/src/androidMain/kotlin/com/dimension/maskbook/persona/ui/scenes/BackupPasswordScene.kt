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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.common.route.navigationComposeBottomSheet
import com.dimension.maskbook.common.route.navigationComposeBottomSheetPackage
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.routeProcessor.annotations.Path
import com.dimension.maskbook.persona.route.PersonaRoute
import com.dimension.maskbook.persona.ui.scenes.register.BackUpPasswordModal
import com.dimension.maskbook.persona.viewmodel.BackUpPasswordViewModel
import moe.tlaster.koin.compose.getViewModel
import moe.tlaster.precompose.navigation.NavController

@NavGraphDestination(
    route = PersonaRoute.BackUpPassword.path,
    deeplink = [
        Deeplinks.Persona.BackUpPassword.path,
    ],
    packageName = navigationComposeBottomSheetPackage,
    functionName = navigationComposeBottomSheet,
)
@Composable
fun BackUpPassword(
    navController: NavController,
    @Path("target") target: String,
) {
    val viewModel = getViewModel<BackUpPasswordViewModel>()
    val password by viewModel.password.collectAsState(initial = "")
    val passwordValid by viewModel.passwordValid.collectAsState(initial = false)
    BackUpPasswordModal(
        password = password,
        onPasswordChanged = { viewModel.setPassword(it) },
        passwordValid = passwordValid,
        onConfirm = {
            navController.navigate(
                target,
                navOptions {
                    popUpTo(PersonaRoute.BackUpPassword.path) {
                        inclusive = true
                    }
                }
            )
        }
    )
}
