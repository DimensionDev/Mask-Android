package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dimension.maskbook.wallet.ui.MaskTheme

@Composable
fun MaskDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    buttons: @Composable () -> Unit = {},
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    properties: DialogProperties = DialogProperties()
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        MaskTheme {
            MaskDialogContent(
                modifier,
                MaterialTheme.shapes.medium,
                MaterialTheme.colors.background,
                contentColorFor(MaterialTheme.colors.surface),
                icon,
                title,
                text,
                buttons
            )
        }
    }
}

@Composable
private fun MaskDialogContent(
    modifier: Modifier,
    shape: Shape,
    backgroundColor: Color,
    contentColor: Color,
    icon: @Composable (() -> Unit)?,
    title: @Composable (() -> Unit)?,
    text: @Composable (() -> Unit)?,
    buttons: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 24.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            icon?.invoke()
            Spacer(modifier = Modifier.height(24.dp))
            if (title != null) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                    val textStyle =
                        MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
                    ProvideTextStyle(textStyle, title)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            if (text != null) {
                CompositionLocalProvider(
                    LocalContentAlpha provides ContentAlpha.medium
                ) {
                    val textStyle = MaterialTheme.typography.body2
                    ProvideTextStyle(textStyle, text)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            buttons()
        }
    }
}
