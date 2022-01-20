package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

@Composable
fun MaskListCardItem(
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    secondaryText: @Composable (() -> Unit)? = null,
    overlineText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    text: @Composable () -> Unit
) {
    MaskListItem(
        modifier = modifier
            .background(MaterialTheme.colors.surface, shape = MaterialTheme.shapes.medium)
            .clip(shape = MaterialTheme.shapes.medium),
        icon = icon,
        secondaryText = secondaryText,
        overlineText = overlineText,
        trailing = trailing,
        text = text,
    )
}
