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
package com.dimension.maskbook.common.ui.barcode

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.dimension.maskbook.common.BuildConfig
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

@Composable
fun rememberBarcodeBitmap(info: String, width: Int = 400, height: Int = 400): Bitmap? {
    return remember(info, width, height) {
        barcodeBitmap(info, width, height)
    }
}

fun barcodeBitmap(info: String, width: Int = 400, height: Int = 400): Bitmap? {
    if (info.isEmpty()) return null
    return try {
        BarcodeEncoder().encodeBitmap(
            info,
            BarcodeFormat.QR_CODE,
            width,
            height
        )
    } catch (e: Throwable) {
        if (BuildConfig.DEBUG) e.printStackTrace()
        null
    }
}
