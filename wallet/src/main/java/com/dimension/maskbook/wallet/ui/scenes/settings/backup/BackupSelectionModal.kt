package com.dimension.maskbook.wallet.ui.scenes.settings.backup

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
    MaskModal(
        title = {
            Text(text = stringResource(R.string.scene_setting_backup_data_title))
        }
    ) {
        Column(Modifier.padding(ScaffoldPadding)) {
            IconButton(
                icon = R.drawable.ic_icloud,
                text = stringResource(R.string.common_controls_back_up_to_cloud),
                onClick = onRemote,
            )
            Spacer(modifier = Modifier.height(16.dp))
            IconButton(
                icon = R.drawable.ic_iphone,
                text = stringResource(R.string.common_controls_back_up_locally),
                onClick = onLocal,
            )
        }
    }
}

@Composable
private fun IconButton(
    @DrawableRes icon: Int,
    text: String,
    onClick: () -> Unit,
) {
    PrimaryButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Icon(
            painterResource(id = icon),
            contentDescription = null,
            tint = LocalContentColor.current
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}