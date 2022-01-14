package com.dimension.maskbook.wallet.ui.scenes.wallets.intro.password

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.*
import com.dimension.maskbook.wallet.viewmodel.wallets.BiometricEnableViewModel
import org.koin.androidx.compose.get

@Composable
fun BiometricsEnableScene(
    onBack: () -> Unit,
    onEnable: (enable: Boolean) -> Unit,
) {
    MaskTheme {
        MaskScaffold(
            topBar = {
                MaskSingleLineTopAppBar(
                    navigationIcon = {
                        MaskBackButton(onBack = onBack)
                    },
                )
            }
        ) {
            // TODO Biometrics Replace UI
            val context = LocalContext.current
            val viewModel: BiometricEnableViewModel = get()
            if (!viewModel.isSupported(context)) onEnable(false)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ScaffoldPadding),
            ) {
                Text(text = stringResource(FaceIdEnableSceneObjects.Title), style = MaterialTheme.typography.h4)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = stringResource(FaceIdEnableSceneObjects.Subtitle))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
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
                    }) {
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