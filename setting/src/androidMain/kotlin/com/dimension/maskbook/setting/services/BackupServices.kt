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
package com.dimension.maskbook.wallet.services

import com.dimension.maskbook.wallet.services.model.DownloadResponse
import com.dimension.maskbook.wallet.services.model.SendCodeBody
import com.dimension.maskbook.wallet.services.model.UploadBody
import com.dimension.maskbook.wallet.services.model.UploadResponse
import com.dimension.maskbook.wallet.services.model.ValidateCodeBody
import retrofit2.http.Body
import retrofit2.http.POST

interface BackupServices {
    @POST("/api/v1/backup/send_code")
    suspend fun sendCode(
        @Body
        body: SendCodeBody,
    )

    @POST("/api/v1/backup/validate_code")
    suspend fun validateCode(
        @Body
        body: ValidateCodeBody,
    )

    @POST("/api/v1/backup/upload")
    suspend fun upload(
        @Body
        body: UploadBody,
    ): UploadResponse

    @POST("/api/v1/backup/download")
    suspend fun download(
        @Body
        body: ValidateCodeBody,
    ): DownloadResponse
}

