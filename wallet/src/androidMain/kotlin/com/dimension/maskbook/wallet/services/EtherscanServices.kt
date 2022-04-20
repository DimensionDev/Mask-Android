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

import com.dimension.maskbook.wallet.services.model.EtherscanAssetResult
import com.dimension.maskbook.wallet.services.model.EtherscanResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface EtherscanServices {
    @GET("/api")
    suspend fun assetEvent(
        @Query("contractaddress") contractaddress: String,
        @Query("address") address: String,
        @Query("module") module: String = "account",
        @Query("action") action: String = "tokennfttx",
        @Query("apikey") apikey: String = "",
        @Query("page") page: Int = 1,
        @Query("offset") offset: Int = 100,
    ): EtherscanResponse<EtherscanAssetResult>
}
