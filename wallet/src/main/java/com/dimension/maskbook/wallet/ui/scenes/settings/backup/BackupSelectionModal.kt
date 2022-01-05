package com.dimension.maskbook.wallet.ui.scenes.settings.backup

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.widget.MaskModal
import com.dimension.maskbook.wallet.ui.widget.PrimaryButton
import com.dimension.maskbook.wallet.ui.widget.ScaffoldPadding

@Composable
fun BackupSelectionModal(
    onLocal: () -> Unit,
    onRemote: () -> Unit,
) {
    MaskModal {
        Column(
            modifier = Modifier.padding(ScaffoldPadding)
        ) {
            Text(text = "Back up your data", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(21.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onRemote.invoke()
                },
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_icloud),
                    contentDescription = null,
                    tint = LocalContentColor.current
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Back up to cloud")
            }
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onLocal.invoke()
                },
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_iphone),
                    contentDescription = null,
                    tint = LocalContentColor.current
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Back up locally")
            }
        }
    }
}