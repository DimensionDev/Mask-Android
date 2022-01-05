package com.dimension.maskbook.manifest;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NavigationUtil {

    public static NavigationType detectNavigationType(String uri, ManifestParser parser) {
        for (Pattern universalLinkPattern : parser.getUniversalLinkPatterns()) {
            Matcher matcher = universalLinkPattern.matcher(uri);
            while(matcher.find()) {
                return NavigationType.UNIVERSAL_LINK;
            }
        }
        return NavigationType.DEFAULT;
    }
}
