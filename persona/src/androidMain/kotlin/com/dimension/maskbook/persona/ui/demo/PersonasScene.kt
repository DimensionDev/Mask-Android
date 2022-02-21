/*
 *  Mask-Android
 *
 *  Copyright (C) 2022  DimensionDev and Contributors
 *
 *  This file is part of Mask-Android.
 *
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Mask-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.persona.ui.demo

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.PersonAddAlt1
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimension.maskbook.common.ui.widget.MaskListItem
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.common.ui.widget.button.MaskButton
import com.dimension.maskbook.common.ui.widget.button.MaskIconCardButton
import com.dimension.maskbook.common.ui.widget.topInsetsPadding
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.persona.export.model.PersonaData
import com.dimension.maskbook.persona.ui.demo.widget.MaskBlurScene

@Composable
fun PersonasScene(
    persona: PersonaData?,
    onBack: () -> Unit,
    onCreatePersonas: () -> Unit,
    onConnectAccount: () -> Unit,
) {
    val image = R.drawable.ic_demo_avatar

    MaskBlurScene(
        image = image,
    ) {
        MaskScaffold(
            modifier = Modifier.topInsetsPadding(),
            backgroundColor = Color.Transparent,
            topBar = {
                MaskSingleLineTopAppBar(
                    backgroundColor = Color.Transparent,
                    title = {
                        Text("Personas")
                    },
                    navigationIcon = {
                        MaskIconCardButton(onClick = onBack) {
                            Icon(
                                Icons.Rounded.ArrowBackIos,
                                contentDescription = null,
                            )
                        }
                    },
                    actions = {
                        MaskIconCardButton(onClick = onCreatePersonas) {
                            Icon(
                                Icons.Outlined.PersonAddAlt1,
                                contentDescription = null,
                            )
                        }
                    },
                )
            }
        ) {
            Spacer(
                modifier = Modifier
                    .padding(
                        top = PersonasSceneDefaults.imageTopPadding +
                            PersonasSceneDefaults.imageSize / 2
                    )
                    .background(
                        color = MaterialTheme.colors.primary,
                        shape = RoundedCornerShape(
                            topStart = PersonasSceneDefaults.imageSize / 2,
                            topEnd = PersonasSceneDefaults.imageSize / 2,
                        )
                    )
                    .fillMaxSize()
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(PersonasSceneDefaults.imageTopPadding))
                Image(
                    painter = painterResource(image),
                    contentDescription = null,
                    modifier = Modifier
                        .background(Color.White, shape = CircleShape)
                        .clip(CircleShape)
                        .size(PersonasSceneDefaults.imageSize),
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = persona?.name.orEmpty(),
                    color = Color.White,
                    fontSize = 24.sp,
                )
                Spacer(Modifier.height(16.dp))
                MaskListItem(
                    icon = {
                        Image(
                            painter = painterResource(R.drawable.mask),
                            contentDescription = null,
                            modifier = Modifier
                                .border(1.dp, Color.White, shape = CircleShape)
                                .clip(CircleShape)
                                .size(36.dp),
                        )
                    },
                    text = {
                        Text(
                            text = "Mask Network Account",
                            color = Color.White,
                        )
                    },
                    secondaryText = {
                        Text(
                            text = persona?.id.orEmpty(),
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 50.dp)
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.weight(1f))
                ConnectButton(
                    onClick = onConnectAccount,
                    modifier = Modifier.padding(25.dp).fillMaxWidth()
                )
                Spacer(Modifier.height(25.dp))
            }
        }
    }
}

@Composable
private fun ConnectButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MaskButton(
        onClick = onClick,
        contentPadding = PaddingValues(16.dp),
        shape = CircleShape,
        modifier = modifier,
    ) {
        Text(
            text = "Connect",
            color = MaterialTheme.colors.primary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
        )
        Spacer(Modifier.width(5.dp))
        ConnectButtonImage(R.drawable.twitter)
        Spacer(Modifier.width(5.dp))
        ConnectButtonImage(R.drawable.facebook)
        Spacer(Modifier.width(5.dp))
        ConnectButtonImage(R.drawable.instagram)
        Spacer(Modifier.width(5.dp))
        ConnectButtonImage(R.drawable.ic_persona_empty_mind)
        Spacer(Modifier.width(5.dp))
        Icon(
            Icons.Default.MoreHoriz,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun ConnectButtonImage(@DrawableRes icon: Int) {
    Image(
        painter = painterResource(icon),
        contentDescription = null,
        modifier = Modifier.size(16.dp),
    )
}

private object PersonasSceneDefaults {
    val imageTopPadding = 40.dp
    val imageSize = 80.dp
}
