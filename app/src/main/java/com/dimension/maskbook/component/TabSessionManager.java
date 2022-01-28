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

import androidx.annotation.Nullable;

import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoSessionSettings;
import org.mozilla.geckoview.WebExtension;

import java.util.ArrayList;

public class TabSessionManager {
    private static ArrayList<TabSession> mTabSessions = new ArrayList<>();
    private int mCurrentSessionIndex = 0;
    private TabObserver mTabObserver;
    private boolean mTrackingProtection;

    public interface TabObserver {
        void onCurrentSession(TabSession session);
    }

    public TabSessionManager() {
    }

    public void unregisterWebExtension() {
        for (final TabSession session : mTabSessions) {
            session.action = null;
        }
    }

    public void setWebExtensionDelegates(WebExtension extension,
                                         WebExtension.ActionDelegate actionDelegate,
                                         WebExtension.SessionTabDelegate tabDelegate) {
        for (final TabSession session : mTabSessions) {
            if(session != null){
                final WebExtension.SessionController sessionController =
                        session.getWebExtensionController();
                sessionController.setActionDelegate(extension, actionDelegate);
                sessionController.setTabDelegate(extension, tabDelegate);
            }
        }
    }

    public void setUseTrackingProtection(boolean trackingProtection) {
        if (trackingProtection == mTrackingProtection) {
            return;
        }
        mTrackingProtection = trackingProtection;

        for (final TabSession session : mTabSessions) {
            session.getSettings().setUseTrackingProtection(trackingProtection);
        }
    }

    public void setTabObserver(TabObserver observer) {
        mTabObserver = observer;
    }

    public void addSession(TabSession session) {
        mTabSessions.add(session);
    }

    public TabSession getSession(int index) {
        if (index < 0) {
            return null;
        }
        if (index >= mTabSessions.size()) {
            return null;
        }
        return mTabSessions.get(index);
    }

    public TabSession getCurrentSession() {
        return getSession(mCurrentSessionIndex);
    }

    public TabSession getSession(GeckoSession session) {
        int index = mTabSessions.indexOf(session);
        if (index == -1) {
            return null;
        }
        return getSession(index);
    }

    public void setCurrentSession(TabSession session) {
        int index = mTabSessions.indexOf(session);
        if (index == -1) {
            mTabSessions.add(session);
            index = mTabSessions.size() - 1;
        }
        mCurrentSessionIndex = index;

        if (mTabObserver != null) {
            mTabObserver.onCurrentSession(session);
        }
    }

    private boolean isCurrentSession(TabSession session) {
        return session == getCurrentSession();
    }

    public void closeSession(@Nullable TabSession session) {
        if (session == null) { return; }
        if (isCurrentSession(session)
            && mCurrentSessionIndex == mTabSessions.size() - 1) {
            --mCurrentSessionIndex;
        }
        session.close();
        mTabSessions.remove(session);
    }

    public TabSession newSession(GeckoSessionSettings settings) {
        TabSession tabSession = new TabSession(settings);
        mTabSessions.add(tabSession);
        return tabSession;
    }

    public int sessionCount() {
        return mTabSessions.size();
    }

    public ArrayList<TabSession> getSessions() {
        return mTabSessions;
    }
}
