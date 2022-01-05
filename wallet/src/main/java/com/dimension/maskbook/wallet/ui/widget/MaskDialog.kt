package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dimension.maskbook.wallet.R
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MaskSelection(
    selected: Boolean,
    onClicked: () -> Unit,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    Box(modifier = Modifier.padding(vertical = 8.dp)) {
        val backgroundColor = if (selected) {
            MaterialTheme.colors.surface
        } else {
            MaterialTheme.colors.background
        }
        Card(
            elevation = 0.dp,
            backgroundColor = backgroundColor,
            onClick = {
                onClicked.invoke()
            },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.button
                ) {
                    content.invoke(this)
                }
                if (selected) {
                    Image(
                        painterResource(id = R.drawable.ic_tick_square),
                        contentDescription = null
                    )
                }
            }
        }
    }
}