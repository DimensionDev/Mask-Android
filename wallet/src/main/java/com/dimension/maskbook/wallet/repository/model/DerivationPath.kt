package com.dimension.maskbook.wallet.repository.model

data class DerivationPath(
    val purpose: Int,
    val coin: Int,
    val account: Int = 0,
    val change: Int = 0,
    val address: Int = 0,
) {
    override fun toString(): String {
        return "m/${purpose}'/${coin}'/${account}'/${change}/${address}"
    }

    companion object {
        fun parse(value: String) = value
            .trimStart('m')
            .trimStart('/')
            .split("/")
            .map { it.trim('\'') }
            .map { it.toInt() }
            .let { DerivationPath(it[0], it[1], it[2], it[3], it[4]) }
    }
}