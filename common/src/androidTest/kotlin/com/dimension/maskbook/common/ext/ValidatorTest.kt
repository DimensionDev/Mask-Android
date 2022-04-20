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

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ValidatorTest {

    @Test
    fun isPhone() {
        assertTrue(Validator.isPhone("+86-13900000000"))
        assertTrue(Validator.isPhone("+886-13900000000"))
        assertTrue(Validator.isPhone("+45-13900000000"))

        assertTrue(Validator.isPhone("+8613900000000"))
        assertTrue(Validator.isPhone("+88613900000000"))
        assertTrue(Validator.isPhone("+4513900000000"))

        assertTrue(Validator.isPhone("86-13900000000"))
        assertTrue(Validator.isPhone("886-13000000000"))
        assertTrue(Validator.isPhone("32-13900000000"))

        assertTrue(Validator.isPhone("8613900000000"))
        assertTrue(Validator.isPhone("88613000000000"))
        assertTrue(Validator.isPhone("3213900000000"))

        assertTrue(Validator.isPhone("+8614900000000"))
        assertTrue(Validator.isPhone("+8615900000000"))
        assertTrue(Validator.isPhone("+8617900000000"))
        assertTrue(Validator.isPhone("+8618900000000"))

        assertFalse(Validator.isPhone("+8611900000000"))
        assertFalse(Validator.isPhone("+8616900000000"))
        assertFalse(Validator.isPhone("+8626900000000"))

        assertFalse(Validator.isPhone("+8666-13900000000"))
        assertFalse(Validator.isPhone("8666-13900000000"))

        assertFalse(Validator.isPhone("13900000000")) // need area code
        assertFalse(Validator.isPhone("+861690000"))
        assertFalse(Validator.isPhone("12312312312"))
    }

    @Test
    fun isValidBackupPasswordFormat() {
        assertTrue(Validator.isValidBackupPasswordFormat("Aa123456."))
        assertTrue(Validator.isValidBackupPasswordFormat("12AAAAaaaa."))
        assertTrue(Validator.isValidBackupPasswordFormat("""Aa123456\"""))
        assertTrue(Validator.isValidBackupPasswordFormat("Aa123456["))
        assertTrue(Validator.isValidBackupPasswordFormat("Aa123456]"))
        assertTrue(Validator.isValidBackupPasswordFormat("Aa123456$"))
        assertTrue(Validator.isValidBackupPasswordFormat("Aa$123456"))

        assertFalse(Validator.isValidBackupPasswordFormat("1111aaaa"))
        assertFalse(Validator.isValidBackupPasswordFormat("1111AAAA"))
        assertFalse(Validator.isValidBackupPasswordFormat("12AAAAAAA."))
        assertFalse(Validator.isValidBackupPasswordFormat("12aaaaaaa."))
        assertFalse(Validator.isValidBackupPasswordFormat("AAAABBBB."))
        assertFalse(Validator.isValidBackupPasswordFormat("Aa12345 6."))

        assertTrue(Validator.isValidBackupPasswordFormat("Ab123456$.1234567890"))
        assertFalse(Validator.isValidBackupPasswordFormat("Ab123456$.12345678901"))
    }

    @Test
    fun isValidPaymentPasswordFormat() {
        assertTrue(Validator.isValidPaymentPasswordFormat("11111111."))
        assertTrue(Validator.isValidPaymentPasswordFormat("1111aaaaa"))
        assertTrue(Validator.isValidPaymentPasswordFormat("1111AAAAA"))
        assertTrue(Validator.isValidPaymentPasswordFormat("1111AAaaa"))
        assertTrue(Validator.isValidPaymentPasswordFormat("1111111."))
        assertTrue(Validator.isValidPaymentPasswordFormat("AAAAAAA."))
        assertTrue(Validator.isValidPaymentPasswordFormat("aaaaaaa."))

        assertTrue(Validator.isValidPaymentPasswordFormat("Ab123456$"))
        assertTrue(Validator.isValidPaymentPasswordFormat("Ab123456["))
        assertTrue(Validator.isValidPaymentPasswordFormat("Ab123456]"))
        assertTrue(Validator.isValidPaymentPasswordFormat("""Ab123456\"""))
        assertTrue(Validator.isValidPaymentPasswordFormat("Ab123456/"))
        assertTrue(Validator.isValidPaymentPasswordFormat("Ab123456#"))
        assertTrue(Validator.isValidPaymentPasswordFormat("Ab123456@"))
        assertTrue(Validator.isValidPaymentPasswordFormat("Ab123456!"))
        assertTrue(Validator.isValidPaymentPasswordFormat("!@#$%%%^1"))

        assertFalse(Validator.isValidPaymentPasswordFormat("1111111"))
        assertFalse(Validator.isValidPaymentPasswordFormat("aaaaaaa"))
        assertFalse(Validator.isValidPaymentPasswordFormat("AAAAAAAA"))
        assertFalse(Validator.isValidPaymentPasswordFormat("1111aa AA"))
        assertFalse(Validator.isValidPaymentPasswordFormat("111aaA"))
        assertFalse(Validator.isValidPaymentPasswordFormat("!@#$%%%^"))

        assertTrue(Validator.isValidPaymentPasswordFormat("Ab123456$.1234567890"))
        assertFalse(Validator.isValidPaymentPasswordFormat("Ab123456$.12345678901"))
    }
}
