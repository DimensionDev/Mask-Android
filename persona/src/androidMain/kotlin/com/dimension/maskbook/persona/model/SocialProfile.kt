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
package com.dimension.maskbook.persona.model

import com.dimension.maskbook.persona.export.model.Network

private const val Prefix = "person:"

class SocialProfile private constructor(
    val network: Network,
    val userId: String,
) {
    companion object {
        fun parse(profileIdentifier: String): SocialProfile? {
            val normalized = profileIdentifier.removePrefix(prefix = Prefix)
            val components = normalized.split("/")
            if (components.size != 2) {
                return null
            }
            val network = components[0].takeIf {
                it != "localhost"
            }?.let {
                Network.values().firstOrNull { value -> value.value == it }
            } ?: return null
            return SocialProfile(network, components[1])
        }
    }

    override fun toString(): String {
        return "${Prefix}${network.value}/$userId"
    }

    fun copy(network: Network? = null, userId: String? = null): SocialProfile {
        return SocialProfile(network ?: this.network, userId ?: this.userId)
    }
}
