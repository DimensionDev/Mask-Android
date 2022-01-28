package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun MaskIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(MaskIconButtonDefaults.defaultSize),
        enabled = enabled,
        interactionSource = interactionSource,
        content = {
            CompositionLocalProvider(
                LocalContentAlpha provides 1f,
                content = content
            )
        }
    )
}

@Composable
fun MaskIconCardButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colors.surface, shape = MaterialTheme.shapes.medium)
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(
                onClick = onClick,
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = rememberRipple()
            )
            .size(MaskIconButtonDefaults.defaultSize),
        contentAlignment = Alignment.Center
    ) {
        val contentAlpha = if (enabled) 1f else ContentAlpha.disabled
        CompositionLocalProvider(
            LocalContentAlpha provides contentAlpha, 
            content = content
        )
    }
}

private object MaskIconButtonDefaults {
    val defaultSize = 36.dp
}