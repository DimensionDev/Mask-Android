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
package com.dimension.maskbook.wallet.ui.scenes.wallets.intro.password

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ui.theme.MaskTheme
import com.dimension.maskbook.common.ui.widget.MaskBackButton
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskTopAppBar
import com.dimension.maskbook.common.ui.widget.PrimaryButton
import com.dimension.maskbook.common.ui.widget.ScaffoldPadding
import com.dimension.maskbook.common.ui.widget.SecondaryButton
import com.dimension.maskbook.common.viewmodel.BiometricEnableViewModel
import com.dimension.maskbook.wallet.R
import org.koin.androidx.compose.getViewModel

@Composable
fun BiometricsEnableScene(
    onBack: () -> Unit,
    onEnable: (enable: Boolean) -> Unit,
) {
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskTopAppBar(
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    },
                    title = {
                        Text(text = stringResource(FaceIdEnableSceneObjects.Title))
                    }
                )
            }
        ) {
            // TODO Biometrics Replace UI
            val context = LocalContext.current
            val viewModel: BiometricEnableViewModel = getViewModel()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ScaffoldPadding),
            ) {
                Text(
                    text = stringResource(FaceIdEnableSceneObjects.Subtitle),
                    style = MaterialTheme.typography.subtitle1,
                )
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painterResource(id = R.drawable.ic_face_id_enable),
                        contentDescription = null
                    )
                }
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.enable(
                            context = context,
                            onEnable = { onEnable.invoke(true) },
                            title = R.string.scene_biometry_recognition_face_id_title,
                            subTitle = R.string.scene_biometry_recognition_face_id_description,
                            negativeButton = R.string.common_controls_no_thanks
                        )
                    },
                ) {
                    Text(text = stringResource(FaceIdEnableSceneObjects.EnableButton))
                }
                Spacer(modifier = Modifier.height(16.dp))
                SecondaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onEnable.invoke(false)
                    }
                ) {
                    Text(text = stringResource(FaceIdEnableSceneObjects.CancelButton))
                }
            }
        }
    }
}

private object FaceIdEnableSceneObjects {
    val Title = R.string.scene_biometry_recognition_face_id_title
    val Subtitle = R.string.scene_biometry_recognition_face_id_description
    val EnableButton = R.string.common_controls_enable
    val CancelButton = R.string.common_controls_no_thanks
}
