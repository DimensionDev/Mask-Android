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

import com.ensarsarajcic.kotlinx.serialization.msgpack.MsgPack
import com.ensarsarajcic.kotlinx.serialization.msgpack.MsgPackConfiguration
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToHexString

val msgPack = MsgPack(configuration = MsgPackConfiguration(ignoreUnknownKeys = true))

inline fun <reified T> T.encodeMsgPackHex(): String = msgPack.encodeToHexString(this)
inline fun <reified T> T.encodeMsgPack(): ByteArray = msgPack.encodeToByteArray(this)

inline fun <reified T> ByteArray.decodeMsgPack(): T = msgPack.decodeFromByteArray(this)
inline fun <reified T> String.decodeMsgPack(): T = msgPack.decodeFromHexString(this)
