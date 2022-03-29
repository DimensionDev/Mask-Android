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
package com.dimension.maskbook.common.manager

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.dimension.maskbook.common.ext.encodeBase64String
import com.dimension.maskbook.common.ext.toByteArray

class ImageLoaderManager(
    private val context: Context,
) {
    val imageLoader = ImageLoader.Builder(context).componentRegistry {
        add(SvgDecoder(context))
    }.build()

    suspend fun convertUrlToBase64(imageUrl: String): String? {
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .build()
        val drawable = imageLoader.execute(request).drawable as? BitmapDrawable ?: return null
        return try {
            drawable.bitmap.toByteArray().encodeBase64String()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
