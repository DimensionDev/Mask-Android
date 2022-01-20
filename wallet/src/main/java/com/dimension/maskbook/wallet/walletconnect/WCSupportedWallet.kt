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