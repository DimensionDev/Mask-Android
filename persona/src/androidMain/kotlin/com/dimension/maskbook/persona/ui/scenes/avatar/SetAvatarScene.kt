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
package com.dimension.maskbook.persona.ui.scenes.avatar

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.dimension.maskbook.common.route.navigationComposeAnimComposable
import com.dimension.maskbook.common.route.navigationComposeAnimComposablePackage
import com.dimension.maskbook.common.routeProcessor.annotations.Back
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskScene
import com.dimension.maskbook.common.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.common.ui.widget.ScaffoldPadding
import com.dimension.maskbook.common.ui.widget.button.MaskBackButton
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.persona.route.PersonaRoute
import com.dimension.maskbook.persona.utils.ImagePicker
import com.dimension.maskbook.persona.viewmodel.avatar.SetAvatarViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalPermissionsApi::class)
@NavGraphDestination(
    route = PersonaRoute.SetAvatar,
    packageName = navigationComposeAnimComposablePackage,
    functionName = navigationComposeAnimComposable,
)
@Composable
fun SetAvatarScene(
    @Back onBack: () -> Unit,
) {
    val viewModel = getViewModel<SetAvatarViewModel>()
    val currentAvatar by viewModel.currentAvatar.collectAsState(null)
    MaskScene {
        MaskScaffold(
            topBar = {
                MaskSingleLineTopAppBar(
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    },
                    title = {
                        Text(text = "Set Persona Avatar")
                    }
                )
            }
        ) {
            val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    it.data?.data?.let {
                        viewModel.setAvatar(it)
                    }
                }
            }
            Column(
                modifier = Modifier.padding(ScaffoldPadding),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (currentAvatar != null) {
                    Image(
                        rememberImagePainter(currentAvatar),
                        contentDescription = null,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .fillMaxWidth()
                            .weight(1f)
                    )
                } else {
                    Image(
                        Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
                val context = LocalContext.current
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val intent = ImagePicker.with(context)
                            .cropSquare()
                            .galleryOnly()
                            .compress(1024)
                            .maxResultSize(1080, 1080)
                            .createIntent()
                        launcher.launch(intent)
                    },
                    content = {
                        Text(text = "Select from photos")
                    }
                )
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val intent = ImagePicker.with(context)
                            .cropSquare()
                            .cameraOnly()
                            .compress(1024)
                            .maxResultSize(1080, 1080)
                            .createIntent()
                        launcher.launch(intent)
                    },
                    content = {
                        Text(text = "Take a photo")
                    }
                )
            }
        }
    }
}
