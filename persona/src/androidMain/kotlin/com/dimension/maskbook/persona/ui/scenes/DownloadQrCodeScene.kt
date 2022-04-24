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

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.ext.onFinished
import com.dimension.maskbook.common.route.navigationComposeAnimComposable
import com.dimension.maskbook.common.route.navigationComposeAnimComposablePackage
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.routeProcessor.annotations.Path
import com.dimension.maskbook.common.ui.notification.StringResNotificationEvent.Companion.show
import com.dimension.maskbook.common.ui.scene.LoadingScene
import com.dimension.maskbook.common.ui.widget.LocalInAppNotification
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.persona.route.PersonaRoute
import com.dimension.maskbook.persona.viewmodel.DownloadQrCodeViewModel
import kotlinx.coroutines.launch
import moe.tlaster.koin.compose.getViewModel
import moe.tlaster.precompose.navigation.NavController
import org.koin.core.parameter.parametersOf

@NavGraphDestination(
    route = PersonaRoute.DownloadQrCode.path,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun DownloadQrCodeScene(
    navController: NavController,
    @Path("idType") idType: String,
    @Path("idBase64") idBase64: String
) {
    val viewModel = getViewModel<DownloadQrCodeViewModel> {
        parametersOf(DownloadQrCodeViewModel.IdType.valueOf(idType), idBase64)
    }
    val personaQrCode by viewModel.personaQrCode.observeAsState()
    val filePickerLaunched by viewModel.filePickerLaunched.observeAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val inAppNotification = LocalInAppNotification.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(),
        onResult = {
            if (it != null) {
                scope.launch {
                    viewModel.save(
                        context = context,
                        uri = it,
                    ).onSuccess {
                        inAppNotification.show(R.string.scene_persona_download_qr_code_success)
                    }.onFailure {
                        inAppNotification.show(R.string.scene_persona_download_qr_code_failed)
                    }.onFinished {
                        navController.popBackStack()
                    }
                }
            } else {
                navController.popBackStack()
            }
        },
    )

    LaunchedEffect(personaQrCode) {
        personaQrCode?.let {
            if (!filePickerLaunched) {
                viewModel.launchFilePicker()
                filePickerLauncher.launch("mask-persona-${it.nickName}.pdf")
            }
        }
    }

    Box {
        LoadingScene()
    }
}
