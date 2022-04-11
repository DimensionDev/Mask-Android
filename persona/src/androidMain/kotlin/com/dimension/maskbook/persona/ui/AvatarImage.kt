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
package com.dimension.maskbook.persona.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import coil.compose.rememberImagePainter
import com.dimension.maskbook.common.ui.widget.NameImage
import com.dimension.maskbook.persona.export.model.Network

@Composable
fun AvatarImage(
    modifier: Modifier = Modifier,
    avatar: String,
    name: String,
    network: Network,
) {
    if (avatar.isNotEmpty()) {
        val source = when (network) {
            Network.Twitter -> "https://unavatar.io/$name"
            else -> avatar
        }
        Image(
            painter = rememberImagePainter(source),
            contentDescription = null,
            modifier = modifier
                .clip(CircleShape),
            alpha = LocalContentAlpha.current,
        )
    } else {
        NameImage(
            name = name,
            style = MaterialTheme.typography.h4,
            modifier = modifier,
        )
    }
}
