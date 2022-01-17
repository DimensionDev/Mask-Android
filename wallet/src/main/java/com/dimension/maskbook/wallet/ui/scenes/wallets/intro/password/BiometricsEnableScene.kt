package com.dimension.maskbook.wallet.ui.scenes.wallets.intro.password

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.MaskBackButton
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.ui.widget.SecondaryButton
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
                Text(text = FaceIdEnableSceneObjects.Title, style = MaterialTheme.typography.h4)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = FaceIdEnableSceneObjects.Subtitle)
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
                            title = FaceIdEnableSceneObjects.Title,
                            subTitle = FaceIdEnableSceneObjects.Subtitle,
                            negativeButton = FaceIdEnableSceneObjects.CancelButton
                        )
                    },
                ) {
                    Text(text = FaceIdEnableSceneObjects.EnableButton)
                }
                Spacer(modifier = Modifier.height(16.dp))
                SecondaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onEnable.invoke(false)
                    }) {
                    Text(text = FaceIdEnableSceneObjects.CancelButton)
                }
            }
        }
    }
}

private object FaceIdEnableSceneObjects {
    const val Title = "Unlock with face ID?"
    const val Subtitle = "Unlock Mask Network faster by setting up facial recognition."
    const val EnableButton = "Enable"
    const val CancelButton = "No thanks"
}