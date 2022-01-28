package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable

@Composable
fun MaskBackButton(
    onBack: () -> Unit,
) {
    MaskIconCardButton(onClick = onBack) {
        Icon(
            imageVector = Icons.Rounded.ArrowBack,
            contentDescription = null,
        )
    }
}
