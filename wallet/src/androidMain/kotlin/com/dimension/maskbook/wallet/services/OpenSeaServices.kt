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

import com.dimension.maskbook.wallet.services.model.OpenSeaAssetModel
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface OpenSeaServices {
    @GET("/api/v1/assets")
    suspend fun assets(
        @Query("owner") owner: String,
        @Query("order_direction") order_direction: String = "desc",
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 50,
        @Header("X-API-KEY") apiKey: String = ""
    ): OpenSeaAssetModel
}
