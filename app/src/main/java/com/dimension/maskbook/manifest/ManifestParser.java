/*
 *  Mask-Android
 *
 *  Copyright (C) DimensionDev and Contributors
 * 
 *  This file is part of Mask-Android.
 * 
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Mask-Android. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.manifest;

import android.content.Context;

import com.dimension.maskbook.util.AssetParser;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class ManifestParser {

    private Context context;
    private Manifest manifest;
    private List<Pattern> universalLinkPatterns;

    public ManifestParser(Context context) {
        this.context = context;
        parseManifest();
    }

    private void parseManifest() {
        String webextensionString = AssetParser.getJsonFromAssets(context, "web_extensions/Maskbook/manifest.webextension-shim.json");
        Gson gson = new Gson();
        this.manifest = gson.fromJson(webextensionString, Manifest.class);
        if (this.manifest != null && this.manifest.universalLink != null) {
            List<Pattern> universalPattern = new ArrayList<>();
            for (String universalLink : this.manifest.universalLink) {
                Pattern pattern = Pattern.compile(universalLink);
                if (pattern != null) {
                    universalPattern.add(pattern);
                }
            }
            this.universalLinkPatterns = universalPattern;
        }
    }

    public List<String> getExternalWebsites() {
        if (this.manifest == null) {
            return new ArrayList<>();
        }
        return this.manifest.externalWebsite;
    }

    public List<String> getUniversalLinks() {
        if (this.manifest == null) {
            return new ArrayList<>();
        }
        return this.manifest.universalLink;
    }

    public List<Pattern> getUniversalLinkPatterns() {
        return universalLinkPatterns;
    }
}
