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

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dimension.maskbook.MaskbookAppKt;
import com.dimension.maskbook.common.WebExtensionDelegate;
import com.dimension.maskbook.common.component.IWebExtensionManager;
import com.dimension.maskbook.site.Site;
import com.dimension.maskbook.util.JsonRPCUtil;
import com.dimension.maskbook.widget.ActionButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.geckoview.AllowOrDeny;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.Image;
import org.mozilla.geckoview.WebExtension;
import org.mozilla.geckoview.WebExtensionController;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WebExtensionManager implements IWebExtensionManager,
        WebExtension.ActionDelegate,
        WebExtension.PortDelegate,
        WebExtension.SessionTabDelegate,
        WebExtension.TabDelegate,
        WebExtension.MessageDelegate,
        WebExtensionController.PromptDelegate,
        WebExtensionController.DebuggerDelegate,
        TabSessionManager.TabObserver {
    public static final int POST_MESSAGE_ID_GET_URL = 1001;
    public static final int POST_MESSAGE_ID_GET_STORAGE = 1002;
    public static final int POST_MESSAGE_ID_GET_PERSONAS = 1003;
    public static final int POST_MESSAGE_ID_GET_QR_CODE = 1004;

    public static final int IN_SETUP_PORT_NOT_IN_POST_PENDING = -1;
    public int mPendingPostMessageID = IN_SETUP_PORT_NOT_IN_POST_PENDING;
    public Site mPendingSwitchToSiteName = null;
    public String mSettingLaunchPage;
    public HashMap<String, Boolean> mConnectedPersonas;
    public String mDashboardUrl;

    public WebExtension extension;

    private LruCache<Image, Bitmap> mBitmapCache = new LruCache<>(5);
    private GeckoRuntime mRuntime;
    private WebExtension.Action mDefaultAction;
    private TabSessionManager mTabManager;
    public WebExtension.Port mPort;

    private WeakReference<WebExtensionDelegate> mExtensionDelegate;

    @Nullable
    @Override
    public GeckoResult<AllowOrDeny> onInstallPrompt(final @NonNull WebExtension extension) {
        return GeckoResult.fromValue(AllowOrDeny.ALLOW);
    }

    @Nullable
    @Override
    public GeckoResult<AllowOrDeny> onUpdatePrompt(@NonNull WebExtension currentlyInstalled,
                                                   @NonNull WebExtension updatedExtension,
                                                   @NonNull String[] newPermissions,
                                                   @NonNull String[] newOrigins) {
        return GeckoResult.fromValue(AllowOrDeny.ALLOW);
    }

    @Override
    public void onExtensionListUpdated() {
        refreshExtensionList();
    }

    // We only support either one browserAction or one pageAction
    private void onAction(final WebExtension extension, final GeckoSession session,
                          final WebExtension.Action action) {
        WebExtensionDelegate delegate = mExtensionDelegate.get();
        if (delegate == null) {
            return;
        }

        WebExtension.Action resolved;

        if (session == null) {
            // This is the default action
            mDefaultAction = action;
            resolved = actionFor(delegate.getCurrentSession());
        } else {
            if (delegate.getSession(session) == null) {
                return;
            }
            delegate.getSession(session).action = action;
            if (delegate.getCurrentSession() != session) {
                // This update is not for the session that we are currently displaying,
                // no need to update the UI
                return;
            }
            resolved = action.withDefault(mDefaultAction);
        }

        updateAction(resolved);
    }

    @Override
    public GeckoResult<GeckoSession> onNewTab(WebExtension source,
                                              WebExtension.CreateTabDetails details) {
        WebExtensionDelegate delegate = mExtensionDelegate.get();
        if (delegate == null) {
            return GeckoResult.fromValue(null);
        }
        return GeckoResult.fromValue(delegate.openNewTab(details));
    }

    @Override
    public GeckoResult<AllowOrDeny> onCloseTab(WebExtension extension, GeckoSession session) {
        final WebExtensionDelegate delegate = mExtensionDelegate.get();
        if (delegate == null) {
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        }

        final TabSession tabSession = mTabManager.getSession(session);
        if (tabSession != null) {
            delegate.closeTab(tabSession);
        }

        return GeckoResult.fromValue(AllowOrDeny.ALLOW);
    }

    @Override
    public GeckoResult<AllowOrDeny> onUpdateTab(WebExtension extension,
                                                GeckoSession session,
                                                WebExtension.UpdateTabDetails updateDetails) {
        final WebExtensionDelegate delegate = mExtensionDelegate.get();
        if (delegate == null) {
            return GeckoResult.fromValue(AllowOrDeny.DENY);
        }

        final TabSession tabSession = mTabManager.getSession(session);
        if (tabSession != null) {
            delegate.updateTab(tabSession, updateDetails);
        }

        return GeckoResult.fromValue(AllowOrDeny.ALLOW);
    }

    @Override
    public void onPageAction(final WebExtension extension,
                             final GeckoSession session,
                             final WebExtension.Action action) {
        onAction(extension, session, action);
    }

    @Override
    public void onBrowserAction(final WebExtension extension,
                                final GeckoSession session,
                                final WebExtension.Action action) {
        onAction(extension, session, action);
    }

    private GeckoResult<GeckoSession> togglePopup(boolean force) {
        WebExtensionDelegate extensionDelegate = mExtensionDelegate.get();
        if (extensionDelegate == null) {
            return null;
        }

        GeckoSession session = extensionDelegate.toggleBrowserActionPopup(false);
        if (session == null) {
            return null;
        }

        return GeckoResult.fromValue(session);
    }

    @Override
    public GeckoResult<GeckoSession> onTogglePopup(final @NonNull WebExtension extension,
                                                   final @NonNull WebExtension.Action action) {
        return togglePopup(false);
    }

    @Override
    public GeckoResult<GeckoSession> onOpenPopup(final @NonNull WebExtension extension,
                                                 final @NonNull WebExtension.Action action) {
        return togglePopup(true);
    }

    private WebExtension.Action actionFor(TabSession session) {
        if (session == null || session.action == null) {
            return mDefaultAction;
        } else {
            return session.action.withDefault(mDefaultAction);
        }
    }

    private void updateAction(WebExtension.Action resolved) {
        WebExtensionDelegate extensionDelegate = mExtensionDelegate.get();
        if (extensionDelegate == null) {
            return;
        }

        if (resolved == null || resolved.enabled == null || !resolved.enabled) {
            extensionDelegate.onActionButton(null);
            return;
        }

        if (resolved.icon != null) {
            if (mBitmapCache.get(resolved.icon) != null) {
                extensionDelegate.onActionButton(new ActionButton(
                        mBitmapCache.get(resolved.icon), resolved.badgeText,
                        resolved.badgeTextColor,
                        resolved.badgeBackgroundColor
                ));
            } else {
                resolved.icon.getBitmap(100).accept(bitmap -> {
                    mBitmapCache.put(resolved.icon, bitmap);
                    extensionDelegate.onActionButton(new ActionButton(
                            bitmap, resolved.badgeText,
                            resolved.badgeTextColor,
                            resolved.badgeBackgroundColor));
                });
            }
        } else {
            extensionDelegate.onActionButton(null);
        }
    }

    public void onClicked(TabSession session) {
        WebExtension.Action action = actionFor(session);
        if (action != null) {
            action.click();
        }
    }

    public void setExtensionDelegate(WebExtensionDelegate delegate) {
        mExtensionDelegate = new WeakReference<>(delegate);
    }

    @Override
    public void onCurrentSession(TabSession session) {
        if (mDefaultAction == null) {
            // No action was ever defined, so nothing to do
            return;
        }

        if (session.action != null) {
            updateAction(session.action.withDefault(mDefaultAction));
        } else {
            updateAction(mDefaultAction);
        }
    }

    public GeckoResult<Void> unregisterExtension() {
        if (extension == null) {
            return GeckoResult.fromValue(null);
        }

        mTabManager.unregisterWebExtension();

        return mRuntime.getWebExtensionController().uninstall(extension).accept((unused) -> {
            extension = null;
            mDefaultAction = null;
            updateAction(null);
        });
    }

    public GeckoResult<WebExtension> updateExtension() {
        if (extension == null) {
            return GeckoResult.fromValue(null);
        }

        return mRuntime.getWebExtensionController().update(extension).then((newExtension) -> {
            registerExtension(newExtension);
            return GeckoResult.fromValue(newExtension);
        });
    }

    public void registerExtension(WebExtension extension) {
        extension.setActionDelegate(this);
        extension.setTabDelegate(this);
        extension.setMessageDelegate(this, "browser");
        mTabManager.setWebExtensionDelegates(extension, this, this);
        this.extension = extension;
    }

    private void refreshExtensionList() {
        mRuntime.getWebExtensionController()
                .list().accept(extensions -> {
            for (final WebExtension extension : extensions) {
                registerExtension(extension);
            }
        });
    }

    public WebExtensionManager(GeckoRuntime runtime,
                               TabSessionManager tabManager) {
        mTabManager = tabManager;
        mRuntime = runtime;
        refreshExtensionList();
    }

    @Nullable
    @Override
    public GeckoResult<Object> onMessage(@NonNull String nativeApp, @NonNull Object message, @NonNull WebExtension.MessageSender sender) {
        return null;
    }

    @Nullable
    @Override
    public void onConnect(@NonNull WebExtension.Port port) {
        mPort = port;

        mPort.setDelegate(this);
        sendSetupPortMessages();
        MaskbookAppKt.initModule();
        MaskbookAppKt.initRepository();
        MaskbookAppKt.initEvent();
        MaskbookAppKt.initWalletConnect();

//        MaskbookAppKt.initKoinModules();
    }

    @Override
    public void onPortMessage(@NonNull Object message, @NonNull WebExtension.Port port) {
        WebExtensionDelegate delegate = mExtensionDelegate.get();
        if (delegate != null) {
            delegate.onPortMessage(message, port);
        }

        Log.d("PortDelegate", "Received message from WebExtension: "
                + message);
    }

    @NonNull
    @Override
    public void onDisconnect(@NonNull WebExtension.Port port) {
        if (port == mPort) {
            mPort = null;
        }
    }

    public void sendGetUrlPortMessage() {

        Log.d("[App-Ext-Interact]", "[ask] sendGetUrlPortMessage()");

        if (mPort == null) {
            return;
        }

        mPendingPostMessageID = WebExtensionManager.POST_MESSAGE_ID_GET_URL;
        sendMessage(WebExtensionManager.POST_MESSAGE_ID_GET_URL, "getDashboardURL", new ArrayList<>());

//        try {
//            mPendingPostMessageID = WebExtensionManager.POST_MESSAGE_ID_GET_URL;
//            JSONObject message = null;
//            List<String> list = new ArrayList<>();
//            message = JsonRPCUtil.createMessage(mPendingPostMessageID,"getDashboardURL", list);
//            mPort.postMessage(message);
//        } catch (JSONException e) {
//            mPendingPostMessageID = -1;
//            e.printStackTrace();
//        }
    }

    public void sendSetupPortMessages() {

        Log.d("[App-Ext-Interact]", "[ask] sendSetupPortMessages()");

        if (mPort == null) {
            return;
        }

        Log.d("[App-Ext-Interact]", "[ask] before delay");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("[App-Ext-Interact]", "[ask] after delay 5s");
                // Do something after 5s = 5000ms
                sendMessage(WebExtensionManager.POST_MESSAGE_ID_GET_URL, "getDashboardURL", new ArrayList<>());
                List<String> list2 = new ArrayList<>();
                list2.add("launchPageSettings");
                sendMessage(WebExtensionManager.POST_MESSAGE_ID_GET_STORAGE, "getSettings", list2);
                sendMessage(WebExtensionManager.POST_MESSAGE_ID_GET_PERSONAS, "getConnectedPersonas", new ArrayList<>());
            }
        }, 30);

//        sendMessage(WebExtensionManager.POST_MESSAGE_ID_GET_URL, "getDashboardURL", new ArrayList<>());
//        List<String> list2 = new ArrayList<>();
//        list2.add("launchPageSettings");
//        sendMessage(WebExtensionManager.POST_MESSAGE_ID_GET_STORAGE, "getSettings", list2);
//        sendMessage(WebExtensionManager.POST_MESSAGE_ID_GET_PERSONAS, "getConnectedPersonas", new ArrayList<>());

//        try {
//            List<String> list1 = new ArrayList<>();
//            JSONObject message1 = JsonRPCUtil.createMessage(WebExtensionManager.POST_MESSAGE_ID_GET_URL,"getDashboardURL", list1);
//            mPort.postMessage(message1);
//
//            List<String> list2 = new ArrayList<>();
//            list2.add("launchPageSettings");
//            JSONObject message2 = JsonRPCUtil.createMessage(WebExtensionManager.POST_MESSAGE_ID_GET_STORAGE,"getSettings", list2);
//            mPort.postMessage(message2);
//
//            List<String> list3 = new ArrayList<>();
//            JSONObject message3 = JsonRPCUtil.createMessage(WebExtensionManager.POST_MESSAGE_ID_GET_PERSONAS,"getConnectedPersonas", list3);
//            mPort.postMessage(message3);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public void sendSwitchToSiteMessage(Site site) {

        Log.d("[App-Ext-Interact]", "[ask] sendSwitchToSiteMessage(site):" + site.name());

        mPendingSwitchToSiteName = site;
        mPendingPostMessageID = WebExtensionManager.POST_MESSAGE_ID_GET_PERSONAS;

        sendMessage(WebExtensionManager.POST_MESSAGE_ID_GET_PERSONAS, "getConnectedPersonas", new ArrayList<>());

//        try {
//            mPendingSwitchToSiteName = site;
//            mPendingPostMessageID = WebExtensionManager.POST_MESSAGE_ID_GET_PERSONAS;
//            JSONObject message = null;
//            List<String> list = new ArrayList<>();
//            message = JsonRPCUtil.createMessage(mPendingPostMessageID,"getConnectedPersonas", list);
//            mPort.postMessage(message);
//        } catch (JSONException e) {
//            mPendingPostMessageID = -1;
//            e.printStackTrace();
//        }
    }

    @Override
    public void sendMessage(int messageId, @NonNull String methodName, @NonNull List<String> params){
        try{
            if(mPort!=null){
                mPort.postMessage(JsonRPCUtil.createMessage(messageId, methodName, params));
            }
        } catch (JSONException e) {
            mPendingPostMessageID = IN_SETUP_PORT_NOT_IN_POST_PENDING;
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(int messageId, @NonNull String methodName, @NonNull JSONObject jsonObject){
        try{
            if(mPort!=null){
                mPort.postMessage(JsonRPCUtil.createMessage(messageId, methodName, jsonObject));
            }
        } catch (JSONException e) {
            mPendingPostMessageID = IN_SETUP_PORT_NOT_IN_POST_PENDING;
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(@NonNull String messageId, @NonNull String methodName, @NonNull List<String> params){
        try{
            if(mPort!=null){
                mPort.postMessage(JsonRPCUtil.createMessage(messageId, methodName, params));
            }
        } catch (JSONException e) {
            mPendingPostMessageID = IN_SETUP_PORT_NOT_IN_POST_PENDING;
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(@NonNull String messageId, @NonNull String methodName, @NonNull JSONObject jsonObject){
        try{
            if(mPort!=null){
                mPort.postMessage(JsonRPCUtil.createMessage(messageId, methodName, jsonObject));
            }
        } catch (JSONException e) {
            mPendingPostMessageID = IN_SETUP_PORT_NOT_IN_POST_PENDING;
            e.printStackTrace();
        }
    }

    @Override
    public void sendRawMessage(@NonNull JSONObject jsonObject){
        if(mPort!=null){
            mPort.postMessage(jsonObject);
        }
    }
}
