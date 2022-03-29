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
package com.dimension.maskbook.wallet.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.dimension.maskbook.wallet.R
import com.dimension.maskbook.wallet.export.model.WalletCollectibleData

@Composable
fun CollectibleCard(
    modifier: Modifier = Modifier,
    data: WalletCollectibleData,
) {
    Box(modifier = modifier) {
        if (!data.videoUrl.isNullOrEmpty()) {
            // TODO: video
        } else if (!data.imageUrl.isNullOrEmpty()) {
            Image(
                painter = rememberImagePainter(data.imageUrl),
                modifier = Modifier
                    .fillMaxWidth(),
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )
        } else {
            Image(
                painter = painterResource(R.drawable.mask),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )
        }
    }
}
