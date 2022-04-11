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
package com.dimension.maskbook.common.util

import com.dimension.maskbook.common.ext.isHexStrict
import com.dimension.maskbook.common.ext.signature
import org.web3j.crypto.Credentials
import org.web3j.crypto.Sign
import org.web3j.utils.Numeric

object SignUtils {
    fun signMessage(message: String, privateKey: String): String {
        val messageBytes = if (message.isHexStrict) {
            Numeric.hexStringToByteArray(message)
        } else {
            message.toByteArray()
        }

        val credentials = Credentials.create(privateKey)
        val data = Sign.signPrefixedMessage(messageBytes, credentials.ecKeyPair,)
        return Numeric.toHexString(data.signature)
    }
}
