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
package com.dimension.maskbook.labs.export.model

data class TransakConfig(
    val isStaging: Boolean,
    val walletAddress: String,
    val defaultCryptoCurrency: String = "ETH",
    val hideMenu: Boolean = true
) {
    val url: String
        get() = "https://$host${queryString()}"

    val host: String
        get() = if (isStaging) "staging-global.transak.com" else "global.transak.com"

    fun queryString(): String {
        // TODO add apiKey in order to integrate all other parameters
        return "?apiKey=${if (isStaging) "4fcd6904-706b-4aff-bd9d-77422813bbb7" else "253be1f0-c6d8-46e7-9d80-38f33bf973e2" }" +
            "&environment=${if (isStaging) "STAGING" else "PRODUCTION" }" +
            "&defaultCryptoCurrency=$defaultCryptoCurrency" +
            "&hideMenu=$hideMenu" +
            "&disablePaymentMethods=apple_pay,googlepay" +
            if (walletAddress.isEmpty()) "" else "&walletAddress=$walletAddress"
    }

    companion object {
        fun host(isStaging: Boolean) = if (isStaging) "staging-global.transak.com" else "global.transak.com"
    }
}
