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
package com.dimension.maskbook.persona.ui.common

import android.util.Base64
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.dimension.maskbook.common.ext.encodeBase64
import com.dimension.maskbook.common.ui.barcode.rememberBarcodeBitmap
import com.dimension.maskbook.common.ui.widget.button.SecondaryButton
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.persona.export.model.PersonaQrCode

@Composable
fun PersonaQrCodePdfView(
    personaQrCode: PersonaQrCode,
    onUiLoading: () -> Unit = {},
    onUiReady: (view: View) -> Unit = {},
    onUiError: () -> Unit = {}
) {
    val context = LocalContext.current
    val pdfView = remember(personaQrCode) {
        ComposeView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setContent {
                QrCodePdfDetail(
                    personaQrCode,
                    personaQrCode.qrCodeStr(),
                    onUiLoading = onUiLoading,
                    onUiReady = {
                        onUiReady.invoke(this)
                    },
                    onUiError = onUiError
                )
            }
        }
    }
    AndroidView(
        factory = { pdfView }
    )
}

@Composable
@OptIn(coil.annotation.ExperimentalCoilApi::class)
private fun QrCodePdfDetail(
    info: PersonaQrCode,
    qrStr: String,
    onUiLoading: () -> Unit,
    onUiReady: () -> Unit,
    onUiError: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxSize()
            .scale(0.8f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(R.drawable.mask),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(5.dp))
            Column {
                Text(text = info.nickName, style = MaterialTheme.typography.h6, color = Color.Black)
                Text(
                    text = info.identifier,
                    style = MaterialTheme.typography.body2,
                    color = Color.Black
                )
            }
        }
        val qrCodePainter = rememberImagePainter(
            data = rememberBarcodeBitmap(
                // currently ios can't handle ?nickname=xxx
                qrStr,
                600,
                600
            ),
        )
        when (qrCodePainter.state) {
            is ImagePainter.State.Success -> onUiReady.invoke()
            is ImagePainter.State.Error -> onUiError.invoke()
            else -> onUiLoading.invoke()
        }
        Image(
            painter = qrCodePainter,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(10.dp),
            contentScale = ContentScale.FillBounds,
            contentDescription = null
        )
        info.identityWords.takeIf { it.isNotEmpty() }?.let {
            Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray)
            Spacer(Modifier.height(20.dp))
            Text(
                stringResource(R.string.scene_persona_download_qr_code_identity_code),
                style = MaterialTheme.typography.h6,
                color = Color.Black
            )
            Spacer(Modifier.height(10.dp))
            it.split(" ").withIndex()
                .groupBy { it.index / 3 }
                .map { it.value.map { it.value } }
                .forEachIndexed { row, words ->
                    Row {
                        words.forEachIndexed { index, word ->
                            SecondaryButton(
                                onClick = {},
                                modifier = Modifier.weight(1f).padding(5.dp)
                            ) {
                                Text(
                                    "${(index + row * 3) + 1}.$word",
                                    style = MaterialTheme.typography.body1,
                                    color = MaterialTheme.colors.primary,
                                )
                            }
                        }
                    }
                }
        }
        Text(
            stringResource(R.string.scene_persona_download_qr_code_tips),
            style = MaterialTheme.typography.caption, color = MaterialTheme.colors.primary
        )
    }
}

private fun PersonaQrCode.qrCodeStr(): String {
    var str = if (identityWords.isNotEmpty()) {
        "mask://persona/identity/${identityWords.encodeBase64(Base64.NO_WRAP)}"
    } else {
        "mask://persona/privatekey/$privateKeyBase64"
    }
    if (nickName.isNotEmpty()) {
        str += "?nickname=$nickName"
    }
    return str
}
