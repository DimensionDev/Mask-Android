package com.dimension.maskbook.wallet.services

import com.dimension.maskbook.wallet.services.model.*
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