/*
 *  Mask-Android
 *
 *  Copyright (C) DimensionDev and Contributors
 * 
 *  This file is part of Mask-Android.
 * 
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Mask-Android. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.wallet.repository.model

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
        return "?defaultCryptoCurrency=$defaultCryptoCurrency" +
            "&hideMenu=$hideMenu" +
            if (walletAddress.isEmpty()) "" else "&walletAddress=$walletAddress" +
                if (isStaging) "apiKey=4fcd6904-706b-4aff-bd9d-77422813bbb7&environment=STAGING" else ""
    }

    companion object {
        fun host(isStaging: Boolean) = if (isStaging) "staging-global.transak.com" else "global.transak.com"
    }
}
