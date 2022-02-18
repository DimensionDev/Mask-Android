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

import com.dimension.maskbook.common.BuildConfig
import org.web3j.crypto.WalletUtils
import org.web3j.ens.EnsResolver

object Validator {
    fun isPhone(value: String): Boolean {
        return "^(\\+?\\d{2,3}-?)([1][3,4578][0-9]\\d{8})".toRegex().matches(value)
    }

    fun isEmail(value: String): Boolean {
        return "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$".toRegex().matches(value)
    }

    fun isValidPasswordFormat(value: String): Boolean {
        return if (BuildConfig.DEBUG) {
            true
        } else {
            "^(?=.{8,20}$)(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^\\dA-Za-z]).*".toRegex()
                .matches(value)
        }
    }

    fun isMnemonic(value: String): Boolean {
        return value.split(" ").let { list ->
            list.size.let { it == 12 || it == 18 || it == 24 } &&
                list.all { it.isNotEmpty() }
        }
    }

    fun isEnsName(value: String): Boolean {
        return value.isNotEmpty() && EnsResolver.isValidEnsName(value)
    }

    fun isWalletAddress(address: String): Boolean {
        return WalletUtils.isValidAddress(address)
    }
}
