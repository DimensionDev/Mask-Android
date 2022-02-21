package com.dimension.maskbook.persona.services

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface NextIDService {

    @POST("/v1/proof")
    fun modifyProof(@Body param: CreateProofParams): ResponseBody

}