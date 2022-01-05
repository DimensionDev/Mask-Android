package com.dimension.maskbook.wallet.ext

import org.web3j.utils.Convert
import java.math.BigDecimal

data class EtherNumber(
    val wei: BigDecimal,
): Comparable<EtherNumber> {
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