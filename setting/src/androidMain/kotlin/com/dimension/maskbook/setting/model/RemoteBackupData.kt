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
package com.dimension.maskbook.setting.model

import com.ensarsarajcic.kotlinx.serialization.msgpack.MsgPack
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.ListSerializer
import java.security.MessageDigest

private const val BackupHeader = "MASK-BACKUP-V000"
private const val ChecksumLength = 32

data class RemoteBackupData(
    val pbkdf2IV: ByteArray,
    val paramIV: ByteArray,
    val encrypted: ByteArray,
) {
    companion object {
        fun fromByteArray(data: ByteArray): RemoteBackupData {
            val headerData = BackupHeader.toByteArray(Charsets.UTF_8)
            headerData.forEachIndexed { index, byte ->
                if (byte != data[index]) {
                    throw IllegalArgumentException("Invalid backup data")
                }
            }
            val realData = data.copyOfRange(headerData.size, data.size - ChecksumLength)
            val checksum = MessageDigest.getInstance("SHA-256").digest(realData)
            val checksumData = data.copyOfRange(data.size - ChecksumLength, data.size)
            if (checksum.contentEquals(checksumData)) {
                return MsgPack.decodeFromByteArray(
                    ListSerializer(ByteArraySerializer()),
                    realData
                ).let {
                    RemoteBackupData(
                        it[0],
                        it[1],
                        it[2]
                    )
                }
            } else {
                throw IllegalArgumentException("Invalid backup data")
            }
        }
    }

    fun toByteArray(): ByteArray {
        val headerData = BackupHeader.toByteArray(Charsets.UTF_8)
        val data = MsgPack.encodeToByteArray(
            ListSerializer(ByteArraySerializer()),
            listOf(pbkdf2IV, paramIV, encrypted)
        )
        val checksum = MessageDigest.getInstance("SHA-256").digest(data)
        return headerData + data + checksum
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RemoteBackupData

        if (!pbkdf2IV.contentEquals(other.pbkdf2IV)) return false
        if (!paramIV.contentEquals(other.paramIV)) return false
        if (!encrypted.contentEquals(other.encrypted)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pbkdf2IV.contentHashCode()
        result = 31 * result + paramIV.contentHashCode()
        result = 31 * result + encrypted.contentHashCode()
        return result
    }
}
