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
package com.dimension.maskbook.extension.ext

import android.net.Uri
import com.dimension.maskbook.extension.export.model.Site

internal val Site.url: String
    get() = when (this) {
        Site.Twitter -> "https://m.twitter.com"
        Site.Facebook -> "https://m.facebook.com"
    }

internal val String.site: Site?
    get() {
        if (this.isEmpty()) {
            return null
        }
        val uri = Uri.parse(this)
        return when (uri.host?.lowercase()) {
            "mobile.twitter.com" -> Site.Twitter
            "m.facebook.com" -> Site.Facebook
            else -> null
        }
    }
