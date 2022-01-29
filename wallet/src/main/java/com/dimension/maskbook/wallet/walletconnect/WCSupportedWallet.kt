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
package com.dimension.maskbook.wallet.walletconnect

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WCSupportedWallet(
    @SerialName("app")
    val app: App? = null,
    @SerialName("chains")
    val chains: List<String>? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("desktop")
    val desktop: Desktop? = null,
    @SerialName("homepage")
    val homepage: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("metadata")
    val metadata: Metadata? = null,
    @SerialName("mobile")
    val mobile: Mobile? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("versions")
    val versions: List<String>? = null
)

@Serializable
data class Mobile(
    @SerialName("native")
    val native: String? = null,
    @SerialName("universal")
    val universal: String? = null
)

@Serializable
data class Metadata(
    @SerialName("colors")
    val colors: Colors? = null,
    @SerialName("shortName")
    val shortName: String? = null
)

@Serializable
data class Desktop(
    @SerialName("native")
    val native: String? = null,
    @SerialName("universal")
    val universal: String? = null
)

@Serializable
data class Colors(
    @SerialName("primary")
    val primary: String? = null,
    @SerialName("secondary")
    val secondary: String? = null
)

@Serializable
data class App(
    @SerialName("android")
    val android: String? = null,
    @SerialName("browser")
    val browser: String? = null,
    @SerialName("ios")
    val ios: String? = null,
    @SerialName("linux")
    val linux: String? = null,
    @SerialName("mac")
    val mac: String? = null,
    @SerialName("windows")
    val windows: String? = null
)
