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
package com.dimension.maskbook.component;

import android.util.Log;
import android.view.ViewGroup;

import com.dimension.maskbook.GeckoViewActivity;
import com.dimension.maskbook.common.WebExtensionDelegate;
import com.dimension.maskbook.site.Site;
import com.dimension.maskbook.util.MessageChannel;
import com.dimension.maskbook.widget.ActionButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.koin.java.KoinJavaComponent;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.WebExtension;

public class DelegatePanel implements WebExtensionDelegate {

    GeckoRuntime gkRuntime;
    WebExtensionManager extManager;
    GeckoViewActivity gkActivity;

    public DelegatePanel(
            GeckoRuntime geckoRuntime,
            WebExtensionManager webExtensionManager,
            GeckoViewActivity geckoViewActivity
    ) {
        this.gkRuntime = geckoRuntime;
        this.extManager = webExtensionManager;
        gkActivity = geckoViewActivity;
    }

    @Override
    public GeckoSession toggleBrowserActionPopup(boolean force) {
        Log.d("[App-Ext-Interact]", "[notify] toggleBrowserActionPopup()"+force);
        if (gkActivity.getPopupSession() == null) {
            gkActivity.openPopupSession();
        }

        ViewGroup.LayoutParams params = gkActivity.getPopupView().getLayoutParams();
        boolean shouldShow = force || params.width == 0;
        gkActivity.setPopupVisibility(shouldShow);

        return shouldShow ? gkActivity.getPopupSession() : null;
    }

    @Override
    public void onActionButton(ActionButton button) {
        Log.d("[App-Ext-Interact]", "[notify] onActionButton()"+button.toString());
        gkActivity.getToolbarView().setBrowserActionButton(button);
    }


    @Override
    public TabSession getSession(GeckoSession session) {
        Log.d("[App-Ext-Interact]", "[notify] getSession()"+session.toString());
        return gkActivity.getTabSessionManager().getSession(session);
    }

    @Override
    public TabSession getCurrentSession() {
        Log.d("[App-Ext-Interact]", "[notify] getCurrentSession()");
        return gkActivity.getTabSessionManager().getCurrentSession();
    }


    @Override
    public void closeTab(TabSession session) {
        Log.d("[App-Ext-Interact]", "[notify] closeTab()");
        gkActivity.closeTab(session);
//        if (geckoViewActivity.getTabSessionManager().sessionCount() > 1) {
//            geckoViewActivity.getTabSessionManager().closeSession(session);
//            TabSession tabSession = geckoViewActivity.getTabSessionManager().getCurrentSession();
//            geckoViewActivity.setGeckoViewSession(tabSession);
//            tabSession.reload();
//            geckoViewActivity.getToolbarView().updateTabCount();
//        } else {
//            geckoViewActivity.recreateSession(session);
//        }
    }

    @Override
    public void updateTab(TabSession session, WebExtension.UpdateTabDetails details) {
        Log.d("[App-Ext-Interact]", "[notify] updateTab()"+session.toString());
        if (details.active == Boolean.TRUE) {
            gkActivity.switchToSession(session, false);
        }
    }

    @Override
    public TabSession openNewTab(WebExtension.CreateTabDetails details) {
        Log.d("[App-Ext-Interact]", "[notify] openNewTab()"+details.toString());
        final TabSession newSession = gkActivity.createSession(details.cookieStoreId);
        gkActivity.getToolbarView().updateTabCount();
        if (details.active == Boolean.TRUE) {
            gkActivity.setGeckoViewSession(newSession, false);
        }
        return newSession;
    }



    @Override
    public void onPortMessage(Object message, WebExtension.Port port) {
        if (message instanceof JSONObject) {
            try {
                JSONObject jsonObject = (JSONObject) message;
                int messageId;
                try {
                    messageId = jsonObject.getInt("id");
                } catch (Throwable e) {
                    messageId = -1;
                }
                Log.d("[App-Ext-Interact]", "[onPortMessage] messageId:"+messageId);

                switch (messageId) {
                    case WebExtensionManager.POST_MESSAGE_ID_GET_URL:
                        Log.d("[App-Ext-Interact]", "[onPortMessage] msg: POST_MESSAGE_ID_GET_URL");

                        String result = jsonObject.getString("result");
                        extManager.mDashboardUrl = result;
                        Log.d("[App-Ext-Interact]", "[onPortMessage] POST_MESSAGE_ID_GET_URL result:"+result);
                        if (inSetupPortPeriod()) {
                            Log.d("[App-Ext-Interact]", "[onPortMessage] POST_MESSAGE_ID_GET_URL inSetupPortPeriod()");
                            gkActivity.checkInitialLoadingStatus();
                        } else {
                            Log.d("[App-Ext-Interact]", "[onPortMessage] POST_MESSAGE_ID_GET_URL switchToSite:"+Site.DASHBOARD);
                            gkActivity.switchToSite(Site.DASHBOARD);
                        }

                        break;

                    case WebExtensionManager.POST_MESSAGE_ID_GET_STORAGE:
                        Log.d("[App-Ext-Interact]", "[onPortMessage] msg: POST_MESSAGE_ID_GET_STORAGE");

                        if (inSetupPortPeriod()) {
                            Log.d("[App-Ext-Interact]", "[onPortMessage] POST_MESSAGE_ID_GET_STORAGE inSetupPortPeriod()");
                            String launchPageSetting;
                            if (jsonObject.has("result")) {
                                launchPageSetting = jsonObject.getString("result");
                            } else {
                                launchPageSetting = null;
                            }
                            if (launchPageSetting != null) {
                                extManager.mSettingLaunchPage = launchPageSetting;
                            } else {
                                extManager.mSettingLaunchPage = "dashboard";
                            }
                            gkActivity.checkInitialLoadingStatus();
                        } else {
                            Log.d("[App-Ext-Interact]", "[onPortMessage] should not be here!");
                        }

                        break;

                    case WebExtensionManager.POST_MESSAGE_ID_GET_PERSONAS:
                        Log.d("[App-Ext-Interact]", "[onPortMessage] msg: POST_MESSAGE_ID_GET_PERSONAS");

                        gkActivity.extractPersonalStatus(jsonObject);

                        gkActivity.updateConnectedPersonaStatus();

                        if (inSetupPortPeriod()) {
                            Log.d("[App-Ext-Interact]", "[onPortMessage] POST_MESSAGE_ID_GET_PERSONAS inSetupPortPeriod()");
                            gkActivity.checkInitialLoadingStatus();

                        } else {
                            Log.d("[App-Ext-Interact]", "[onPortMessage] POST_MESSAGE_ID_GET_PERSONAS not inSetupPortPeriod()");
                            if (extManager.mPendingSwitchToSiteName == Site.TWITTER) {
                                Log.d("[App-Ext-Interact]", "[onPortMessage] POST_MESSAGE_ID_GET_PERSONAS site is twitter");
                                if (extManager.mConnectedPersonas.get("twitter.com") == Boolean.TRUE) {
                                    Log.d("[App-Ext-Interact]", "[onPortMessage] POST_MESSAGE_ID_GET_PERSONAS switchToSite:"+extManager.mPendingSwitchToSiteName);
                                    gkActivity.switchToSite(extManager.mPendingSwitchToSiteName);
                                } else {

                                }
                                extManager.mPendingSwitchToSiteName = Site.DASHBOARD;
                            }
                            if (extManager.mPendingSwitchToSiteName == Site.FACEBOOK) {
                                Log.d("[App-Ext-Interact]", "[onPortMessage] POST_MESSAGE_ID_GET_PERSONAS site is fb");
                                if (extManager.mConnectedPersonas.get("facebook.com") == Boolean.TRUE) {
                                    Log.d("[App-Ext-Interact]", "[onPortMessage] POST_MESSAGE_ID_GET_PERSONAS switchToSite:"+extManager.mPendingSwitchToSiteName);
                                    gkActivity.switchToSite(extManager.mPendingSwitchToSiteName);
                                } else {

                                }
                                extManager.mPendingSwitchToSiteName = Site.DASHBOARD;
                            }
                        }

                        break;

                    case WebExtensionManager.POST_MESSAGE_ID_GET_QR_CODE:
                        Log.d("[App-Ext-Interact]", "[onPortMessage] msg: POST_MESSAGE_ID_GET_QR_CODE");

                        break;
                    default:
                        MessageChannel.INSTANCE.onMessage(jsonObject);
                        break;
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean inSetupPortPeriod(){
        return extManager.mPendingPostMessageID == WebExtensionManager.IN_SETUP_PORT_NOT_IN_POST_PENDING;
    }

}
