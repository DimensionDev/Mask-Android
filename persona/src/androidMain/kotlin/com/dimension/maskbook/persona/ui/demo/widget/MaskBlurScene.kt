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
package com.dimension.maskbook.persona.ui.demo.widget

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.BlurTransformation
import com.dimension.maskbook.common.ui.widget.MaskScene
import com.dimension.maskbook.persona.R

@Composable
fun MaskBlurScene(
    @DrawableRes image: Int = R.drawable.ic_demo_avatar,
    imageHeight: Dp = 250.dp,
    contentBackgroundRadius: Dp = 25.dp,
    content: @Composable () -> Unit
) {
    MaskScene(
        extendViewIntoStatusBar = true,
    ) {
        val context = LocalContext.current
        Image(
            painter = rememberImagePainter(image) {
                transformations(BlurTransformation(context, 25f))
            },
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .height(imageHeight + contentBackgroundRadius)
                .fillMaxWidth(),
        )
        content()
    }
}
