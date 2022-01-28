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
package com.dimension.maskbook.component;

import android.os.Parcel;
import android.util.Log;

import androidx.annotation.*;

import com.dimension.maskbook.site.SiteConfig;

import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoSessionSettings;
import org.mozilla.geckoview.WebExtension;

public class TabSession extends GeckoSession {
    private String mTitle;
    private String mUri;
    public WebExtension.Action action;

    public TabSession() { super(); }

    public TabSession(GeckoSessionSettings settings) {
        super(settings);
    }

    public String getTitle() {
        return mTitle == null || mTitle.length() == 0 ? "about:blank" : mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getUri() {
        return mUri;
    }

    @Override
    public void loadUri(@NonNull String uri) {
        super.loadUri(uri);
        mUri = uri;
        Log.d("[App-Ext-Interact]", "[onPortMessage] TabSession.loadUri():" + uri.toString());
    }

    public void onLocationChange(@NonNull String uri) {
        mUri = uri;
    }
}
