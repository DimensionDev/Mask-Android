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

import com.dimension.maskbook.common.model.DateType
import org.joda.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

internal class DateUtilsTest {

    @Test
    fun `test date time`() {
        val now = LocalDate(2021, 10, 10)

        assertEquals(DateType.Today, DateUtils.getDateText(now, now))

        val yesterday = now.plusDays(-1)
        assertEquals(DateType.Yesterday, DateUtils.getDateText(yesterday, now))

        val thisMonth = now.plusDays(-5)
        assertEquals(DateType.ThisMonth, DateUtils.getDateText(thisMonth, now))

        val thisYear = now.plusMonths(-2)
        assertEquals(DateType.ThisYear, DateUtils.getDateText(thisYear, now))

        val olderDate = LocalDate(2000, 8, 1)
        assertEquals(DateType.OlderDate, DateUtils.getDateText(olderDate))
    }
}
