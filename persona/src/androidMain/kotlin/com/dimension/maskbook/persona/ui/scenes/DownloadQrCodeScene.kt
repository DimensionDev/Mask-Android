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

import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.route.navigationComposeAnimComposable
import com.dimension.maskbook.common.route.navigationComposeAnimComposablePackage
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.ui.barcode.rememberBarcodeBitmap
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskScene
import com.dimension.maskbook.persona.export.model.PersonaQrCode
import com.dimension.maskbook.persona.route.PersonaRoute
import com.dimension.maskbook.persona.viewmodel.DownloadQrCodeViewModel
import org.koin.androidx.compose.getViewModel

@NavGraphDestination(
    route = PersonaRoute.DownloadQrCode,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun DownloadQrCodeScene(
    navController: NavController
) {
    val viewModel = getViewModel<DownloadQrCodeViewModel>()
    val personaQrCode by viewModel.personaQrCode.observeAsState()
    val state by viewModel.state.observeAsState()
    val context = LocalContext.current
    var pdfView = View(context)
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(),
        onResult = {
            if (it != null) {
                viewModel.save(uri = it, context = context, pdfContent = pdfView, height = pdfView.height, width = pdfView.width)
            } else {
                navController.popBackStack()
            }
        },
    )
    pdfView = remember(personaQrCode) {
        ComposeView(context).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            setContent {
                personaQrCode?.let {
                    QrCodePdfPreview(it)
                }
            }

            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // TODO Mimao some times it won't be invoke
                    if (state == DownloadQrCodeViewModel.DownloadState.Idle) {
                        personaQrCode?.let {
                            viewModel.pickFile()
                            filePickerLauncher.launch("mask-persona-${it.nickName}.pdf")
                        }
                    }
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }

    // TODO Mimao broken logic needs to be fix
    when (state) {
        DownloadQrCodeViewModel.DownloadState.Success -> {
            navController.popBackStack()
        }
        DownloadQrCodeViewModel.DownloadState.Failed -> {
            navController.popBackStack()
        }
        else -> {}
    }

    MaskScene {
        MaskScaffold {
            Box(modifier = Modifier.fillMaxSize()) {
                personaQrCode?.let {
                    AndroidView(modifier = Modifier.fillMaxSize(),
                        factory = { pdfView }
                    )
                }
                if (state == DownloadQrCodeViewModel.DownloadState.Pending) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

//Todo Mimao complete ui
@Composable
private fun QrCodePdfPreview(info: PersonaQrCode) {
    Log.d("Mimao", "preview called:$info")
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        Text("this is a PDF", color = MaterialTheme.colors.primary)
        Image(
            painter = rememberImagePainter(
                data = rememberBarcodeBitmap(
                    info.privateKeyBase64, // TODO Mimao mask://xxx
                )
            ),
            modifier = Modifier
                .padding(8.dp)
                .wrapContentSize(),
            contentScale = ContentScale.FillBounds,
            contentDescription = null
        )
        Text("this is a PDF", color = MaterialTheme.colors.primary)
    }
}
