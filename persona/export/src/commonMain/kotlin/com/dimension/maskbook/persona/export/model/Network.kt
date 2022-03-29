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
package com.dimension.maskbook.persona.export.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Network(val value: String) {
    @SerialName("twitter.com")
    Twitter("twitter.com"),
    @SerialName("facebook.com")
    Facebook("facebook.com"),
    @SerialName("instagram.com")
    Instagram("instagram.com"),
    @SerialName("minds.com")
    Minds("minds.com");

    companion object {
        fun withHost(host: String?) = when (host) {
            "twitter.com" -> Twitter
            "facebook.com" -> Facebook
            "instagram.com" -> Instagram
            "minds.com" -> Minds
            else -> null
        }

        fun withProfileIdentifier(profileIdentifier: String): Network? {
            if (!profileIdentifier.startsWith("person:")) return null
            val endIndex = profileIdentifier.lastIndexOf('/')
            if (endIndex == -1) return null
            return withHost(profileIdentifier.substring(7, endIndex))
        }
    }
}
