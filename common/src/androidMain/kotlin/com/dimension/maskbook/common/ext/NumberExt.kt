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

import com.dimension.maskbook.common.bigDecimal.BigDecimal
import kotlin.math.log10
import kotlin.math.pow
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

fun Float.humanizeToken(): String {
    return this.toBigDecimal().humanizeToken()
}

fun Float.humanizeDollar(): String {
    return this.toBigDecimal().humanizeDollar()
}

fun Double.humanizeToken(): String {
    return this.toBigDecimal().humanizeToken()
}

fun Double.humanizeDollar(): String {
    return this.toBigDecimal().humanizeDollar()
}

fun BigDecimal.humanizeToken(): String {
    return when {
        this > BigDecimal.ONE -> {
            this.format(6)
        }
        this > BigDecimal.valueOf(0.000001) -> {
            this.format(6)
        }
        this == BigDecimal.ZERO -> "0"
        else -> {
            return "<0.000001"
        }
    }
}

fun BigDecimal.humanizeDollar(): String {
    return "$" + when {
        this > BigDecimal.valueOf(0.01) -> {
            this.format(2, trimTrailingZero = false)
        }
        else -> "0"
    }
}

fun Double.format(digits: Int, trimTrailingZero: Boolean = true) =
    "%.${digits}f".format(this).let { if (trimTrailingZero) it.trimTrailingZero() else it }

fun Float.format(digits: Int, trimTrailingZero: Boolean = true) =
    "%.${digits}f".format(this).let { if (trimTrailingZero) it.trimTrailingZero() else it }

fun BigDecimal.format(digits: Int, trimTrailingZero: Boolean = true) =
    "%.${digits}f".format(this).let { if (trimTrailingZero) it.trimTrailingZero() else it }

fun String.fromHexString(): BigDecimal {
    return substringAfter("0x").toLong(16).toBigDecimal()
}

fun String.trimTrailingZero(): String {
    return if (indexOf(".") < 0) {
        this
    } else {
        replace("0*$".toRegex(), "") // remove continuous from end of the string
            .replace("\\.$".toRegex(), "") // remove . if it is the last char
    }
}

@OptIn(ExperimentalTime::class)
fun Number.humanizeMinutes(): String = toDouble().minutes.toString()

fun Long.humanizeTimestamp(): String {
    val date = java.util.Date(this)
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
    return sdf.format(date)
}

fun Long.humanizeFileSize(): String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(toDouble()) / log10(1024.0)).toInt()
    return "${toDouble() / 1024.0.pow(digitGroups)} ${units[digitGroups]}"
}
