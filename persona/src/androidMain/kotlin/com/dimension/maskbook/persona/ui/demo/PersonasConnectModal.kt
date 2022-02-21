package com.dimension.maskbook.persona.ui.demo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimension.maskbook.common.ui.widget.MaskListItem
import com.dimension.maskbook.common.ui.widget.MaskModal
import com.dimension.maskbook.common.ui.widget.button.MaskButton
import com.dimension.maskbook.persona.export.model.Network
import com.dimension.maskbook.persona.model.icon

private val networks = Network.values()

@Composable
fun PersonasConnectModal(
    onConnect: (Network) -> Unit,
) {
    MaskModal(
        modifier = Modifier.background(MaterialTheme.colors.primary),
        title = {
            Text(
                text = "Connect",
                color = Color.White,
                fontSize = 24.sp,
            )
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            networks.forEach { item ->
                PersonasListItem(
                    icon = item.icon,
                    text = item.name,
                    onClick = { onConnect(item) }
                )
            }
        }
    }
}

@Composable
private fun PersonasListItem(
    icon: Int,
    text: String,
    onClick: () -> Unit,
) {
    MaskButton(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
        ),
    ) {
        MaskListItem(
            icon = {
                Image(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                )
            },
            text = {
                Text(
                    text = text,
                    color = Color.White
                )
            },
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 20.dp),
            modifier = Modifier
                .background(
                    Color.White.copy(alpha = 0.1f),
                    RoundedCornerShape(12.dp)
                )
        )
    }
}