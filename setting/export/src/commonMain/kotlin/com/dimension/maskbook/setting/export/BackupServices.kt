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
package com.dimension.maskbook.setting.export

import com.dimension.maskbook.setting.export.model.BackupMeta

interface BackupServices {
    suspend fun restoreBackupFromJson(value: String)
    suspend fun provideBackupMetaFromJson(value: String): BackupMeta?
    suspend fun downloadBackupWithPhone(phone: String, code: String): String
    suspend fun downloadBackupWithEmail(email: String, code: String): String
    suspend fun validatePhoneCode(phone: String, code: String)
    suspend fun validateEmailCode(email: String, code: String)
    suspend fun sendPhoneCode(phone: String)
    suspend fun sendEmailCode(email: String)
    suspend fun decryptBackup(password: String, account: String, data: ByteArray): String
    suspend fun encryptBackup(password: String, account: String, content: String): ByteArray
}
