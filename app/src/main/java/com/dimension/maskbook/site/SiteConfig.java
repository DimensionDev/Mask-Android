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
package com.dimension.maskbook.site;

import android.net.Uri;

public class SiteConfig {
    public static String getSiteUrl(Site site) {
        switch (site) {
            case TWITTER:
                return "https://mobile.twitter.com";
            case FACEBOOK:
                return "https://m.facebook.com";
            case DASHBOARD:
                return "/index.html";
        }
        return "";
    }

    public static String getSiteHost(Site site) {
        switch (site) {
            case TWITTER:
                return "mobile.twitter.com";
            case FACEBOOK:
                return "m.facebook.com";
            case DASHBOARD:
                return "";
        }
        return "";
    }


    public static String getTitleByUri(Uri uri) {
        if (uri.getScheme().equals("moz-extension")) {
            return "Mask Network";
        }
        for (Site site : Site.values()) {
            if (SiteConfig.getSiteHost(site).equalsIgnoreCase(uri.getHost())) {
                switch (site) {
                    case TWITTER:
                        return "Twitter";
                    case FACEBOOK:
                        return "Facebook";
                    case DASHBOARD:
                        return "Mask Network";
                }
            }
        }
        return "Loading...";
    }
}
