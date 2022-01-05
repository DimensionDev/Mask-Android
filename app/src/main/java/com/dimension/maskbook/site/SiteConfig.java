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
