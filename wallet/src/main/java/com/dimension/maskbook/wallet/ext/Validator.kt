package com.dimension.maskbook.wallet.ext

import com.dimension.maskbook.wallet.BuildConfig
import org.web3j.crypto.WalletUtils

object Validator {
    fun isPhone(value: String): Boolean {
        return "^\\+(?:[0-9]?){6,14}[0-9]\$".toRegex().matches(value)
    }

    fun isEmail(value: String): Boolean {
        return "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$".toRegex().matches(value)
    }

    fun isValidPasswordFormat(value: String): Boolean {
        return if (BuildConfig.DEBUG) {
            true
        } else  {
            "^(?=.{8,20}$)(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^\\dA-Za-z]).*".toRegex()
                .matches(value)
        }

    }

    fun isMnemonic(value: String): Boolean {
        return value.split(" ").size.let {
            it == 12 || it == 18 || it == 24
        }
    }

    fun isEnsName(value: String): Boolean {
        return value.endsWith(".eth")
    }

    fun isWalletAddress(address: String): Boolean {
        return WalletUtils.isValidAddress(address)
    }
}