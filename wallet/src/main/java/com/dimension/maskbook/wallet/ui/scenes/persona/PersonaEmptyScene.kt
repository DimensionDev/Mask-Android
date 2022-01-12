package com.dimension.maskbook.wallet.ui.scenes.persona

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.ui.widget.itemsGridIndexed

enum class ConnectData(
    val title: String,
    @DrawableRes val icon: Int,
    val enable: Boolean,
) {
    Twitter(
        title = "Twitter",
        icon = R.drawable.twitter,
        enable = true,
    ),
    Facebook(
        title = "Facebook",
        icon = R.drawable.facebook,
        enable = true,
    ),
    Instagram(
        title = "Instagram",
        icon = R.drawable.instagram,
        enable = false,
    ),
    Minds(
        title = "Minds",
        icon = R.drawable.ic_persona_empty_mind,
        enable = false,
    )
}

private val showList = ConnectData.values().sortedByDescending { it.enable }

@Composable
fun PersonaEmptyScene(
    onItemClick: (ConnectData) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(
            horizontal = PersonaEmptySceneDefaults.contentHorizontalPadding,
            vertical = PersonaEmptySceneDefaults.contentVerticalPadding,
        ),
        verticalArrangement = Arrangement.spacedBy(PersonaEmptySceneDefaults.itemPadding),
    ) {
        item {
            Text(
                text = PersonaEmptySceneDefaults.body,
                style = MaterialTheme.typography.h5,
            )
        }
        itemsGridIndexed(
            data = showList,
            spacing = PersonaEmptySceneDefaults.itemPadding,
            rowSize = 3,
        ) { _, item ->
            ConnectItem(
                item = item,
                onItemClick = { onItemClick(item) }
            )
        }
    }
}

@Composable
private fun ConnectItem(item: ConnectData, onItemClick: () -> Unit) {
    Button(
        enabled = item.enable,
        onClick = onItemClick,
        elevation = null,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            disabledBackgroundColor = Color.Transparent,
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(PersonaEmptySceneDefaults.itemHeight),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(item.icon),
                    contentDescription = null,
                    modifier = Modifier.size(PersonaEmptySceneDefaults.itemIconSize),
                    alpha = LocalContentAlpha.current,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.subtitle2,
                    color = LocalContentColor.current.copy(LocalContentAlpha.current),
                )
            }
        }
    }
}

private object PersonaEmptySceneDefaults {
    const val body = "Connect Social accounts"
    val contentHorizontalPadding = 22.5f.dp
    val contentVerticalPadding = 16.dp
    val itemPadding = 8.dp
    val itemHeight = 100.dp
    val itemIconSize = 48.dp
}
