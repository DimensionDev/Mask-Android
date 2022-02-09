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

import com.dimension.maskbook.persona.R
import com.dimension.maskbook.wallet.repository.Network
import com.dimension.maskbook.wallet.repository.PlatformType

val Network.icon: Int
    get() = when (this) {
        Network.Twitter -> R.drawable.twitter
        Network.Facebook -> R.drawable.facebook
        Network.Instagram -> R.drawable.instagram
        Network.Minds -> R.drawable.ic_persona_empty_mind
    }

val Network.title: String
    get() = when (this) {
        Network.Twitter -> "Twitter"
        Network.Facebook -> "Facebook"
        Network.Instagram -> "Instagram"
        Network.Minds -> "Minds"
    }

val Network.platform: PlatformType?
    get() = when (this) {
        Network.Twitter -> PlatformType.Twitter
        Network.Facebook -> PlatformType.Facebook
        Network.Instagram -> null
        Network.Minds -> null
    }
