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
package com.dimension.maskbook.util

import android.content.Context
import androidx.core.content.edit

private val NAME = "MaskBook"
fun <T> Context.getSettings(name: String, defaultValue: T): T {
    return this.getSharedPreferences(NAME, Context.MODE_PRIVATE).let {
        @Suppress("UNCHECKED_CAST")
        when (defaultValue) {
            is String -> it.getString(name, defaultValue) as T
            is Float -> it.getFloat(name, defaultValue) as T
            is Boolean -> it.getBoolean(name, defaultValue) as T
            is Int -> it.getInt(name, defaultValue) as T
            is Long -> it.getLong(name, defaultValue) as T
            else -> throw NotImplementedError()
        }
    }
}

fun <T> Context.setSettings(name: String, value: T) {
    this.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit {
        when (value) {
            is String -> putString(name, value)
            is Float -> putFloat(name, value)
            is Boolean -> putBoolean(name, value)
            is Int -> putInt(name, value)
            is Long -> putLong(name, value)
            else -> throw NotImplementedError()
        }
    }
}
