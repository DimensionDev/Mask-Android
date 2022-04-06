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

import org.web3j.crypto.Sign

val Sign.SignatureData.signature: ByteArray
    get() {
        val magic1 = byteArrayOf(0, 27, 31, 35)
        val magic2 = byteArrayOf(1, 28, 32, 36)
        val v = v.firstOrNull() ?: throw Error("v is empty")
        return r + s + when {
            magic1.contains(v) -> {
                0x1b
            }
            magic2.contains(v) -> {
                0x1c
            }
            else -> {
                throw Error("invalid v")
            }
        }
    }
