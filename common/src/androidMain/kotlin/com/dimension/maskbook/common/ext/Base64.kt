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
package com.dimension.maskbook.common.ext

import android.util.Base64

fun ByteArray.encodeBase64(flag: Int = Base64.DEFAULT): ByteArray {
    return Base64.encode(this, flag)
}

fun ByteArray.encodeBase64String(flag: Int = Base64.DEFAULT): String {
    return String(encodeBase64(flag = flag))
}

fun ByteArray.decodeBase64(flag: Int = Base64.DEFAULT): ByteArray {
    return Base64.decode(this, flag)
}

fun ByteArray.decodeBase64String(flag: Int = Base64.DEFAULT): String {
    return String(decodeBase64(flag = flag))
}

fun String.encodeBase64Bytes(flag: Int = Base64.DEFAULT): ByteArray {
    return toByteArray().encodeBase64(flag = flag)
}

fun String.encodeBase64(flag: Int = Base64.DEFAULT): String {
    return String(encodeBase64Bytes(flag = flag))
}

fun String.decodeBase64Bytes(flag: Int = Base64.DEFAULT): ByteArray {
    return toByteArray().decodeBase64(flag = flag)
}

fun String.decodeBase64(flag: Int = Base64.DEFAULT): String {
    return String(decodeBase64Bytes(flag = flag))
}
