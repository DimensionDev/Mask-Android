package com.dimension.maskbook.wallet.ui.scenes.persona.social

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding

@Composable
fun ConnectSocialModal(
    onDone: () -> Unit
) {
    MaskModal {
        Column(
            modifier = Modifier
                .padding(ScaffoldPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_social_login_in_to_continue), style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_social_login_in_notify))
            Spacer(modifier = Modifier.height(8.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onDone.invoke() },
            ) {
                Text(text = androidx.compose.ui.res.stringResource(com.dimension.maskbook.wallet.R.string.scene_social_i_understand))
            }
        }
    }
}