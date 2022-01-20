package com.dimension.maskbook.wallet.ext

import java.math.BigDecimal
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
        this > BigDecimal.ONE -> {
            this.format(2, trimTrailingZero = false)
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

fun Double.format(digits: Int, trimTrailingZero: Boolean = true) =
    "%.${digits}f".format(this).let { if (trimTrailingZero) it.trimTrailingZero() else it }

fun Float.format(digits: Int, trimTrailingZero: Boolean = true) =
    "%.${digits}f".format(this).let { if (trimTrailingZero) it.trimTrailingZero() else it }

fun BigDecimal.format(digits: Int, trimTrailingZero: Boolean = true) =
    "%.${digits}f".format(this).let { if (trimTrailingZero) it.trimTrailingZero() else it }

fun String.fromHexString(): BigDecimal {
    return substringAfter("0x").toLong(16).toBigDecimal()
}

private fun String.trimTrailingZero(): String {
    return if (indexOf(".") < 0) {
        this
    } else {
        replace("0*$".toRegex(), "")// remove continuous from end of the string
            .replace("\\.$".toRegex(), "")// remove . if it is the last char
    }
}

@OptIn(ExperimentalTime::class)
fun Number.humanizeMinutes(): String = toDouble().minutes.toString()