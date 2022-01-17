package com.dimension.maskbook.wallet.ui.scenes.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.MaskTheme
import com.dimension.maskbook.wallet.ui.widget.MaskScaffold
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding
import com.dimension.maskbook.wallet.ui.widget.SecondaryButton

@Composable
fun RegisterScene(
    onCreateIdentity: () -> Unit,
    onRecoveryAndSignIn: () -> Unit,
    onSynchronization: () -> Unit,
) {
    MaskTheme {
        MaskScaffold {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ScaffoldPadding),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painterResource(id = R.drawable.ic_group_132),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painterResource(id = R.drawable.ic_mask_logo),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.height(9.dp))
                    Text(
                        text = stringResource(R.string.scene_identity_empty_description),
                        style = LocalTextStyle.current.copy(color = MaterialTheme.colors.primary)
                    )
                }
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onCreateIdentity.invoke() },
                ) {
                    Text(text = stringResource(R.string.scene_identity_empty_create_an_identity))
                }
                Spacer(modifier = Modifier.height(16.dp))
                SecondaryButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = { onRecoveryAndSignIn.invoke() },
                ) {
                    Text(text = stringResource(R.string.scene_identity_empty_recovery_sign_in))
                }
//                Spacer(modifier = Modifier.height(16.dp))
//                SecondaryButton(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    onClick = { onSynchronization.invoke() },
//                ) {
//                    Text(text = stringResource(R.string.scene_identity_empty_synchronization))
//                }
            }
        }
    }
}
