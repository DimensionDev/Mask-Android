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
package com.dimension.maskbook.wallet.ext

import com.dimension.maskbook.common.bigDecimal.BigDecimal
import org.web3j.utils.Convert

data class EtherNumber(
    val wei: BigDecimal,
) : Comparable<EtherNumber> {
    val kwei: BigDecimal get() = Convert.fromWei(wei, Convert.Unit.KWEI)
    val mwei: BigDecimal get() = Convert.fromWei(wei, Convert.Unit.MWEI)
    val gwei: BigDecimal get() = Convert.fromWei(wei, Convert.Unit.GWEI)
    val szabo: BigDecimal get() = Convert.fromWei(wei, Convert.Unit.SZABO)
    val finney: BigDecimal get() = Convert.fromWei(wei, Convert.Unit.FINNEY)
    val ether: BigDecimal get() = Convert.fromWei(wei, Convert.Unit.ETHER)
    val kether: BigDecimal get() = Convert.fromWei(wei, Convert.Unit.KETHER)
    val mether: BigDecimal get() = Convert.fromWei(wei, Convert.Unit.METHER)
    val gether: BigDecimal get() = Convert.fromWei(wei, Convert.Unit.GETHER)

    override fun compareTo(other: EtherNumber) = this.wei.compareTo(other.wei)
}

val Number.wei: EtherNumber get() = EtherNumber(BigDecimal("$this"))
val Number.kwei: EtherNumber get() = EtherNumber(Convert.toWei("$this", Convert.Unit.KWEI))
val Number.mwei: EtherNumber get() = EtherNumber(Convert.toWei("$this", Convert.Unit.MWEI))
val Number.gwei: EtherNumber get() = EtherNumber(Convert.toWei("$this", Convert.Unit.GWEI))
val Number.szabo: EtherNumber get() = EtherNumber(Convert.toWei("$this", Convert.Unit.SZABO))
val Number.finney: EtherNumber get() = EtherNumber(Convert.toWei("$this", Convert.Unit.FINNEY))
val Number.ether: EtherNumber get() = EtherNumber(Convert.toWei("$this", Convert.Unit.ETHER))
val Number.kether: EtherNumber get() = EtherNumber(Convert.toWei("$this", Convert.Unit.KETHER))
val Number.mether: EtherNumber get() = EtherNumber(Convert.toWei("$this", Convert.Unit.METHER))
val Number.gether: EtherNumber get() = EtherNumber(Convert.toWei("$this", Convert.Unit.GETHER))

val String.hexWei: EtherNumber get() = EtherNumber(BigDecimal(substringAfter("0x").toLong(16)))
