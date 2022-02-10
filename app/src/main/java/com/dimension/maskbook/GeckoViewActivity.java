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
package com.dimension.maskbook;

import com.dimension.maskbook.common.platform.IPlatformSwitcher;
import com.dimension.maskbook.common.util.MessageChannel;
import com.dimension.maskbook.persona.export.model.PlatformType;
import com.dimension.maskbook.persona.repository.IPersonaRepository;
import org.json.JSONException;
import org.json.JSONObject;

import org.mozilla.geckoview.AllowOrDeny;
import org.mozilla.geckoview.BasicSelectionActionDelegate;
import org.mozilla.geckoview.BuildConfig;
import org.mozilla.geckoview.ContentBlocking;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoRuntimeSettings;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoSessionSettings;
import org.mozilla.geckoview.GeckoView;
import org.mozilla.geckoview.GeckoWebExecutor;
import org.mozilla.geckoview.SlowScriptResponse;
import org.mozilla.geckoview.WebExtension;
import org.mozilla.geckoview.WebExtensionController;
import org.mozilla.geckoview.WebNotification;
import org.mozilla.geckoview.WebNotificationDelegate;
import org.mozilla.geckoview.WebRequest;
import org.mozilla.geckoview.WebRequestError;
import org.mozilla.geckoview.RuntimeTelemetry;
import org.mozilla.geckoview.WebResponse;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dimension.maskbook.component.DelegatePanel;
import com.dimension.maskbook.component.TabSession;
import com.dimension.maskbook.component.TabSessionManager;
import com.dimension.maskbook.component.WebExtensionManager;
import com.dimension.maskbook.manifest.ManifestParser;
import com.dimension.maskbook.manifest.NavigationType;
import com.dimension.maskbook.manifest.NavigationUtil;
import com.dimension.maskbook.platform.PlatformSwitcher;
import com.dimension.maskbook.site.ConnectedPersona;
import com.dimension.maskbook.site.Site;
import com.dimension.maskbook.site.SiteConfig;
import com.dimension.maskbook.widget.BasicGeckoViewPrompt;
import com.dimension.maskbook.widget.LocationView;
import com.dimension.maskbook.widget.ToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import io.reactivex.disposables.Disposable;
import kotlin.Lazy;

import static org.koin.java.KoinJavaComponent.inject;

public class GeckoViewActivity
        extends AppCompatActivity
        implements
        ToolbarLayout.TabListener,
//        WebExtensionDelegate,
        SharedPreferences.OnSharedPreferenceChangeListener,
        IPlatformSwitcher {
    private static final String LOGTAG = "GeckoViewActivity";
    private static final String FULL_ACCESSIBILITY_TREE_EXTRA = "full_accessibility_tree";
    private static final String SEARCH_URI_BASE = "https://www.google.com/search?q=";
    private static final String ACTION_SHUTDOWN = "com.dimension.maskbook.SHUTDOWN";
    private static final String CHANNEL_ID = "Maskbook";
    private static final int REQUEST_FILE_PICKER = 1;
    private static final int REQUEST_PERMISSIONS = 2;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 3;
    private static final int REQUEST_SAVE_DIR = 4;

    private static final int REQUEST_CODE_SCAN = 1001;

    private static GeckoRuntime sGeckoRuntime;

    private static WebExtensionManager sExtensionManager;

    private DelegatePanel delegatePanel;

    private TabSessionManager mTabSessionManager;

    public TabSessionManager getTabSessionManager() {
        return mTabSessionManager;
    }

    private GeckoView mGeckoView;
    private boolean mFullAccessibilityTree;
    private boolean mUsePrivateBrowsing;
    private boolean mKillProcessOnDestroy;
    private boolean mDesktopMode;
    private boolean mTrackingProtectionException;

    private final Lazy<IPersonaRepository> personaRepository = inject(IPersonaRepository.class);
    private final Lazy<PlatformSwitcher> platformSwitcher = inject(PlatformSwitcher.class);

    private TabSession mPopupSession;

    public TabSession getPopupSession() {
        return mPopupSession;
    }

    private View mPopupView;

    public View getPopupView() {
        return mPopupView;
    }

    private boolean mShowNotificationsRejected;
    private final ArrayList<String> mAcceptedPersistentStorage = new ArrayList<String>();

    private ToolbarLayout mToolbarView;

    public ToolbarLayout getToolbarView() {
        return mToolbarView;
    }

    private String mCurrentUri;
    private boolean mCanGoBack;
    private boolean mCanGoForward;
    private boolean mFullScreen;

    private final HashMap<String, Integer> mNotificationIDMap = new HashMap<>();
    private final HashMap<Integer, WebNotification> mNotificationMap = new HashMap<>();
    private int mLastID = 100;

    private MaterialToolbar mMaterialToolbar;
    private ProgressBar mProgressView;
    private ManifestParser manifestParser;

    private LinkedList<WebResponse> mPendingDownloads = new LinkedList<>();

    private int mNextActivityResultCode = 10;
    private final HashMap<Integer, GeckoResult<Intent>> mPendingActivityResult = new HashMap<>();

    private WebResponse mDownloadResponse;
    private boolean mInitialized = false;

    private final LocationView.CommitListener mCommitListener = new LocationView.CommitListener() {
        @Override
        public void onCommit(String text) {
            if ((text.contains(".") || text.contains(":")) && !text.contains(" ")) {
                mTabSessionManager.getCurrentSession().loadUri(text);
            } else {
                mTabSessionManager.getCurrentSession().loadUri(SEARCH_URI_BASE + text);
            }
            mGeckoView.requestFocus();
        }
    };

//    @Override
//    public TabSession openNewTab(WebExtension.CreateTabDetails details) {
//        final TabSession newSession = createSession(details.cookieStoreId);
//        mToolbarView.updateTabCount();
//        if (details.active == Boolean.TRUE) {
//            setGeckoViewSession(newSession, false);
//        }
//        return newSession;
//    }

    private final List<Setting<?>> SETTINGS = new ArrayList<>();

    @Override
    public void switchTo(@NonNull PlatformType platformType) {
        switch (platformType) {
            case Twitter:
                switchToSite(Site.TWITTER);
                break;
            case Facebook:
                switchToSite(Site.FACEBOOK);
                break;
        }
    }

    @Override
    public void showTooltips(boolean visible) {
        findViewById(R.id.top_tooltips).post(() -> findViewById(R.id.top_tooltips).setVisibility(visible ? View.VISIBLE : View.GONE));
    }

    @Override
    public void launchDeeplink(@NonNull String deeplink) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(deeplink)));
    }

    @Override
    public void showModal(@NonNull String route, @Nullable Object data) {
        new ComposeBottomSheetDialogFragment(route, data).show(getSupportFragmentManager(), ComposeBottomSheetDialogFragment.TAG);
    }

    private abstract class Setting<T> {
        private final int mKey;
        private final int mDefaultKey;
        private final boolean mReloadCurrentSession;
        private T mValue;

        public Setting(final int key, final int defaultValueKey, final boolean reloadCurrentSession) {
            mKey = key;
            mDefaultKey = defaultValueKey;
            mReloadCurrentSession = reloadCurrentSession;

            SETTINGS.add(this);
        }

        public void onPrefChange(SharedPreferences pref) {
            final T defaultValue = getDefaultValue(mDefaultKey, getResources());
            final String key = getResources().getString(this.mKey);
            final T value = getValue(key, defaultValue, pref);
            if (!value().equals(value)) {
                setValue(value);
            }
        }

        private void setValue(final T newValue) {
            mValue = newValue;
            for (final TabSession session : mTabSessionManager.getSessions()) {
                setValue(session.getSettings(), value());
            }
            if (sGeckoRuntime != null) {
                setValue(sGeckoRuntime.getSettings(), value());
                if (sExtensionManager != null) {
                    setValue(sGeckoRuntime.getWebExtensionController(), value());
                }
            }

            final GeckoSession current = mTabSessionManager.getCurrentSession();
            if (mReloadCurrentSession && current != null) {
                current.reload();
            }
        }

        public T value() {
            return mValue == null ? getDefaultValue(mDefaultKey, getResources()) : mValue;
        }

        protected abstract T getDefaultValue(final int key, final Resources res);

        protected abstract T getValue(final String key, final T defaultValue,
                                      final SharedPreferences preferences);

        /**
         * Override one of these to define the behavior when this setting changes.
         */
        protected void setValue(final GeckoSessionSettings settings, final T value) {
        }

        protected void setValue(final GeckoRuntimeSettings settings, final T value) {
        }

        protected void setValue(final WebExtensionController controller, final T value) {
        }
    }

    private class StringSetting extends Setting<String> {
        public StringSetting(final int key, final int defaultValueKey) {
            this(key, defaultValueKey, false);
        }

        public StringSetting(final int key, final int defaultValueKey,
                             final boolean reloadCurrentSession) {
            super(key, defaultValueKey, reloadCurrentSession);
        }

        @Override
        protected String getDefaultValue(int key, final Resources res) {
            return res.getString(key);
        }

        @Override
        public String getValue(final String key, final String defaultValue,
                               final SharedPreferences preferences) {
            return preferences.getString(key, defaultValue);
        }
    }

    private class BooleanSetting extends Setting<Boolean> {
        public BooleanSetting(final int key, final int defaultValueKey) {
            this(key, defaultValueKey, false);
        }

        public BooleanSetting(final int key, final int defaultValueKey,
                              final boolean reloadCurrentSession) {
            super(key, defaultValueKey, reloadCurrentSession);
        }

        @Override
        protected Boolean getDefaultValue(int key, Resources res) {
            return res.getBoolean(key);
        }

        @Override
        public Boolean getValue(final String key, final Boolean defaultValue,
                                final SharedPreferences preferences) {
            return preferences.getBoolean(key, defaultValue);
        }
    }

    private class IntSetting extends Setting<Integer> {
        public IntSetting(final int key, final int defaultValueKey) {
            this(key, defaultValueKey, false);
        }

        public IntSetting(final int key, final int defaultValueKey,
                          final boolean reloadCurrentSession) {
            super(key, defaultValueKey, reloadCurrentSession);
        }

        @Override
        protected Integer getDefaultValue(int key, Resources res) {
            return res.getInteger(key);
        }

        @Override
        public Integer getValue(final String key, final Integer defaultValue,
                                final SharedPreferences preferences) {
            return Integer.parseInt(
                    preferences.getString(key, Integer.toString(defaultValue)));
        }
    }

    private final IntSetting mDisplayMode = new IntSetting(
            R.string.key_display_mode, R.integer.display_mode_default) {
        @Override
        public void setValue(final GeckoSessionSettings settings, final Integer value) {
            settings.setDisplayMode(value);
        }
    };

    private final IntSetting mPreferredColorScheme = new IntSetting(
            R.string.key_preferred_color_scheme, R.integer.preferred_color_scheme_default,
            /* reloadCurrentSession */ true
    ) {
        @Override
        public void setValue(final GeckoRuntimeSettings settings, final Integer value) {
            settings.setPreferredColorScheme(value);
        }
    };

    private final StringSetting mUserAgent = new StringSetting(
            R.string.key_user_agent_override, R.string.user_agent_override_default,
            /* reloadCurrentSession */ true
    ) {
        @Override
        public void setValue(final GeckoSessionSettings settings, final String value) {
            settings.setUserAgentOverride(value.isEmpty() ? null : value);
        }
    };

    private final BooleanSetting mRemoteDebugging = new BooleanSetting(
            R.string.key_remote_debugging, R.bool.remote_debugging_default
    ) {
        @Override
        public void setValue(final GeckoRuntimeSettings settings, final Boolean value) {
            settings.setRemoteDebuggingEnabled(value);
        }
    };

    private final BooleanSetting mJavascriptEnabled = new BooleanSetting(
            R.string.key_javascript_enabled, R.bool.javascript_enabled_default,
            /* reloadCurrentSession */ true
    ) {
        @Override
        public void setValue(final GeckoRuntimeSettings settings, final Boolean value) {
            settings.setJavaScriptEnabled(value);
        }
    };

    private final BooleanSetting mTrackingProtection = new BooleanSetting(
            R.string.key_tracking_protection, R.bool.tracking_protection_default
    ) {
        @Override
        public void setValue(final GeckoRuntimeSettings settings, final Boolean value) {
            mTabSessionManager.setUseTrackingProtection(value);
            settings.getContentBlocking()
                    .setStrictSocialTrackingProtection(value);
        }
    };

    private final StringSetting mEnhancedTrackingProtection = new StringSetting(
            R.string.key_enhanced_tracking_protection, R.string.enhanced_tracking_protection_default
    ) {
        @Override
        public void setValue(final GeckoRuntimeSettings settings, final String value) {
            int etpLevel;
            switch (value) {
                case "disabled":
                    etpLevel = ContentBlocking.EtpLevel.NONE;
                    break;
                case "standard":
                    etpLevel = ContentBlocking.EtpLevel.DEFAULT;
                    break;
                case "strict":
                    etpLevel = ContentBlocking.EtpLevel.STRICT;
                    break;
                default:
                    throw new RuntimeException("Invalid ETP level: " + value);
            }

            settings.getContentBlocking().setEnhancedTrackingProtectionLevel(etpLevel);
        }
    };

    private final BooleanSetting mDynamicFirstPartyIsolation = new BooleanSetting(
            R.string.key_dfpi, R.bool.dfpi_default
    ) {
        @SuppressLint("WrongConstant")
        @Override
        public void setValue(final GeckoRuntimeSettings settings, final Boolean value) {
            int cookieBehavior = value ?
                    ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY_AND_ISOLATE_OTHERS :
                    ContentBlocking.CookieBehavior.ACCEPT_NON_TRACKERS;
            settings.getContentBlocking().setCookieBehavior(cookieBehavior);
        }
    };

    private final BooleanSetting mAllowAutoplay = new BooleanSetting(
            R.string.key_autoplay, R.bool.autoplay_default, /* reloadCurrentSession */ true
    );

    private final BooleanSetting mAllowExtensionsInPrivateBrowsing = new BooleanSetting(
            R.string.key_allow_extensions_in_private_browsing,
            R.bool.allow_extensions_in_private_browsing_default
    ) {
        @Override
        public void setValue(final WebExtensionController controller, final Boolean value) {
            controller.setAllowedInPrivateBrowsing(
                    sExtensionManager.extension,
                    value);
        }
    };

    private void onPreferencesChange(SharedPreferences preferences) {
        for (Setting<?> setting : SETTINGS) {
            setting.onPrefChange(preferences);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("[App-Ext-Interact]", "[GeckoViewActivity] onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("[App-Ext-Interact]", "[GeckoViewActivity] onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("[App-Ext-Interact]", "[GeckoViewActivity] onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("[App-Ext-Interact]", "[GeckoViewActivity] onResume");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("[App-Ext-Interact]", "[GeckoViewActivity] onCreate");
        Log.i(LOGTAG, "zerdatime " + SystemClock.elapsedRealtime() +
                " - application start");
        createNotificationChannel();
        setContentView(R.layout.geckoview_activity);
        setSupportActionBar(findViewById(R.id.topAppBar));
        var nightModeFlags =
                getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        var splash = findViewById(R.id.splash_container);
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            splash.setBackgroundResource(android.R.color.background_dark);
        } else {
            splash.setBackgroundResource(android.R.color.background_light);
        }
        personaRepository.getValue().getRedirect().observe(this, redirectTarget -> {
            if (redirectTarget != null) {
                switch (redirectTarget) {
                    case Gecko:
                        switchToSite(Site.TWITTER);
                        splash.setVisibility(View.GONE);
                        break;
                    case Setup:
                        var intent = new Intent(GeckoViewActivity.this, ComposeActivity.class);
                        intent.putExtra("startDestination",  "Register");
                        startActivity(intent);
                        splash.setVisibility(View.GONE);
                        break;
                }
            }
        });
        platformSwitcher.getValue().setActualSwitcher(this);
        manifestParser = new ManifestParser(getApplicationContext());
        mGeckoView = findViewById(R.id.gecko_view);
        mTabSessionManager = new TabSessionManager();


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
        // Read initial preference state
        onPreferencesChange(preferences);

        mMaterialToolbar = findViewById(R.id.topAppBar);
        mMaterialToolbar.setNavigationOnClickListener(v -> {
            sExtensionManager.sendGetUrlPortMessage();
            sExtensionManager.onClicked(mTabSessionManager.getCurrentSession());
        });
        mMaterialToolbar.getMenu().findItem(R.id.debug_menu).setVisible(BuildConfig.DEBUG);
        mMaterialToolbar.setNavigationOnClickListener(view -> {
            sExtensionManager.sendSwitchToSiteMessage(Site.TWITTER);
        });

        mMaterialToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.twitter) {
                sExtensionManager.sendSwitchToSiteMessage(Site.TWITTER);
            } else if (item.getItemId() == R.id.facebook) {
                sExtensionManager.sendSwitchToSiteMessage(Site.FACEBOOK);
            } else if (item.getItemId() == R.id.scanner) {
                RxPermissions rxPermissions = new RxPermissions(GeckoViewActivity.this);
                final Disposable subscribe = rxPermissions
                        .request(Manifest.permission.CAMERA) // ask single or multiple permission once
                        .subscribe(
                                granted -> {
                                    if (granted) {
                                        Log.d("permission", "request permission success");
                                        // All requested permissions are granted
                                        Intent intent = new Intent(GeckoViewActivity.this, ScannerActivity.class);
                                        startActivityForResult(intent, REQUEST_CODE_SCAN);
                                    } else {
                                        // At least one permission is denied
                                        Log.d("permission", "request permission failed");
                                    }
                                }
                        );
            } else if (item.getItemId() == R.id.dashboard) {
                var intent = new Intent(GeckoViewActivity.this, ComposeActivity.class);
                intent.putExtra("startDestination", "Main");
                startActivity(intent);
            } else if (item.getItemId() == R.id.debug_menu) {
                new ComposeBottomSheetDialogFragment().show(getSupportFragmentManager(), ComposeBottomSheetDialogFragment.TAG);
            }
            return true;
        });

        mToolbarView = new ToolbarLayout(this, mTabSessionManager);
        mToolbarView.setId(R.id.toolbar_layout);
        mToolbarView.setTabListener(this);

//        getSupportActionBar().setCustomView(mToolbarView,
//                new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
//                        ActionBar.LayoutParams.WRAP_CONTENT));
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        mFullAccessibilityTree = getIntent().getBooleanExtra(FULL_ACCESSIBILITY_TREE_EXTRA, false);
        mProgressView = findViewById(R.id.page_progress);

        Log.d("[App-Ext-Interact]", "[GeckoViewActivity] onCreate if (sGeckoRuntime == null)");
        if (sGeckoRuntime == null) {
            Log.d("[App-Ext-Interact]", "[GeckoViewActivity] onCreate : sGeckoRuntime == null");

            final GeckoRuntimeSettings.Builder runtimeSettingsBuilder =
                    new GeckoRuntimeSettings.Builder();

            if (BuildConfig.DEBUG) {
                // In debug builds, we want to load JavaScript resources fresh with
                // each build.
                runtimeSettingsBuilder.arguments(new String[]{"-purgecaches"});
            }

            final Bundle extras = getIntent().getExtras();
            if (extras != null) {
                runtimeSettingsBuilder.extras(extras);
            }
            runtimeSettingsBuilder
                    .remoteDebuggingEnabled(BuildConfig.DEBUG)
                    .consoleOutput(true)
                    .loginAutofillEnabled(true)
                    .contentBlocking(new ContentBlocking.Settings.Builder()
                            .antiTracking(ContentBlocking.AntiTracking.DEFAULT |
                                    ContentBlocking.AntiTracking.STP)
                            .safeBrowsing(ContentBlocking.SafeBrowsing.DEFAULT)
                            .cookieBehavior(ContentBlocking.CookieBehavior.ACCEPT_NON_TRACKERS)
                            .enhancedTrackingProtectionLevel(ContentBlocking.EtpLevel.DEFAULT)
                            .build())
//                    .crashHandler(MaskbookCrashHandler.class)
                    .preferredColorScheme(mPreferredColorScheme.value())
                    .telemetryDelegate(new MaskbookTelemetryDelegate())
                    .aboutConfigEnabled(true);

            try {
                sGeckoRuntime = GeckoRuntime.create(this, runtimeSettingsBuilder.build());
            } catch (Throwable e) {
                Log.d("[App-Ext-Interact]", "[GeckoViewActivity] GeckoRuntime.create exception!");
                e.printStackTrace();
            }

            sExtensionManager = new WebExtensionManager(sGeckoRuntime, mTabSessionManager);
            mTabSessionManager.setTabObserver(sExtensionManager);
            MessageChannel.INSTANCE.setManager(sExtensionManager);
            sGeckoRuntime.getWebExtensionController().setDebuggerDelegate(sExtensionManager);

            // `getSystemService` call requires API level 23
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                sGeckoRuntime.setWebNotificationDelegate(new WebNotificationDelegate() {
                    final NotificationManager notificationManager = getSystemService(NotificationManager.class);

                    @Override
                    public void onShowNotification(@NonNull WebNotification notification) {
                        Intent clickIntent = new Intent(GeckoViewActivity.this, GeckoViewActivity.class);
                        clickIntent.putExtra("onClick", notification.tag);
                        PendingIntent dismissIntent = PendingIntent.getActivity(GeckoViewActivity.this, mLastID, clickIntent, 0);

                        Notification.Builder builder = new Notification.Builder(GeckoViewActivity.this)
                                .setContentTitle(notification.title)
                                .setContentText(notification.text)
                                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                                .setContentIntent(dismissIntent)
                                .setAutoCancel(true);

                        mNotificationIDMap.put(notification.tag, mLastID);
                        mNotificationMap.put(mLastID, notification);

                        if (notification.imageUrl != null && notification.imageUrl.length() > 0) {
                            final GeckoWebExecutor executor = new GeckoWebExecutor(sGeckoRuntime);

                            GeckoResult<WebResponse> response = executor.fetch(
                                    new WebRequest.Builder(notification.imageUrl)
                                            .addHeader("Accept", "image")
                                            .build());
                            response.accept(value -> {
                                Bitmap bitmap = BitmapFactory.decodeStream(value.body);
                                builder.setLargeIcon(Icon.createWithBitmap(bitmap));
                                notificationManager.notify(mLastID++, builder.build());
                            });
                        } else {
                            notificationManager.notify(mLastID++, builder.build());
                        }

                    }

                    @Override
                    public void onCloseNotification(@NonNull WebNotification notification) {
                        if (mNotificationIDMap.containsKey(notification.tag)) {
                            int id = mNotificationIDMap.get(notification.tag);
                            notificationManager.cancel(id);
                            mNotificationMap.remove(id);
                            mNotificationIDMap.remove(notification.tag);
                        }
                    }
                });


            }

            sGeckoRuntime.setDelegate(() -> {
                mKillProcessOnDestroy = true;
                finish();
            });

            sGeckoRuntime.setActivityDelegate(pendingIntent -> {
                final GeckoResult<Intent> result = new GeckoResult<>();
                try {
                    final int code = mNextActivityResultCode++;
                    mPendingActivityResult.put(code, result);
                    GeckoViewActivity.this.startIntentSenderForResult(pendingIntent.getIntentSender(), code, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    result.completeExceptionally(e);
                }
                return result;
            });
        }

        delegatePanel = new DelegatePanel(sGeckoRuntime, sExtensionManager, GeckoViewActivity.this);
        sExtensionManager.setExtensionDelegate(delegatePanel);

        if (savedInstanceState == null) {
            TabSession session = getIntent().getParcelableExtra("session");
            if (session != null) {
                connectSession(session);

                if (!session.isOpen()) {
                    session.open(sGeckoRuntime);
                }

                mFullAccessibilityTree = session.getSettings().getFullAccessibilityTree();

                mTabSessionManager.addSession(session);
                session.open(sGeckoRuntime);
                setGeckoViewSession(session);
            } else {
                session = createSession();
                session.open(sGeckoRuntime);
                mTabSessionManager.setCurrentSession(session);
                mGeckoView.setSession(session);
                sGeckoRuntime.getWebExtensionController().setTabActive(session, true);
            }
            loadFromIntent(getIntent());
        }

//        mGeckoView.setDynamicToolbarMaxHeight(findViewById(R.id.toolbar).getLayoutParams().height);
        mGeckoView.setDynamicToolbarMaxHeight(0);
        mToolbarView.getLocationView().setCommitListener(mCommitListener);
        mToolbarView.updateTabCount();


        sGeckoRuntime.getWebExtensionController()
                .ensureBuiltIn(
                        "resource://android/assets/web_extensions/Maskbook/",
                        "info@dimension.com"
                ).accept(result -> {
            sExtensionManager.registerExtension(result);
        });
        findViewById(R.id.top_tooltips).setOnClickListener((view) -> {
            new ComposeBottomSheetDialogFragment("UserNameInput", null).show(getSupportFragmentManager(), ComposeBottomSheetDialogFragment.TAG);
        });
        findViewById(R.id.top_tooltips_close_btn).setOnClickListener((view) -> {
            showTooltips(false);
            personaRepository.getValue().cancelConnectingProcess();
        });
    }
//
//    @Override
//    public void onPortMessage(Object message, WebExtension.Port port) {
//        if (message instanceof JSONObject) {
//            try {
//                JSONObject jsonObject = (JSONObject) message;
//                int messageId = jsonObject.getInt("id");
//
//                if(sExtensionManager.mPendingPostMessageID == WebExtensionManager.NOT_IN_PENDING){
//                    if (messageId == WebExtensionManager.POST_MESSAGE_ID_GET_URL) {
//                        String result = jsonObject.getString("result");
//                        sExtensionManager.mDashboardUrl = result;
//                    }else if (messageId == WebExtensionManager.POST_MESSAGE_ID_GET_STORAGE) {
//                        String launchPageSetting = jsonObject.getString("result");
//                        if (launchPageSetting != null) {
//                            sExtensionManager.mSettingLaunchPage = launchPageSetting;
//                        } else {
//                            sExtensionManager.mSettingLaunchPage = "dashboard";
//                        }
//                    }else if (messageId == WebExtensionManager.POST_MESSAGE_ID_GET_PERSONAS) {
//                        //[[{"connected":false,"network":"facebook.com"},{"connected":true,"network":"twitter.com"}]]
//                        updatePersonalStatus(jsonObject);
//                    }
//                    checkInitialLoadingStatus();
//
//                }else{
//                    sExtensionManager.mPendingPostMessageID = WebExtensionManager.NOT_IN_PENDING;
//
//                    if (messageId == WebExtensionManager.POST_MESSAGE_ID_GET_URL) {
//                        String result = jsonObject.getString("result");
//                        sExtensionManager.mDashboardUrl = result;
//                        switchToSite(Site.DASHBOARD);
//                    }else if (messageId == WebExtensionManager.POST_MESSAGE_ID_GET_PERSONAS) {
//                        updatePersonalStatus(jsonObject);
//                        if (sExtensionManager.mPendingSwitchToSiteName == Site.TWITTER) {
//                            if (sExtensionManager.mConnectedPersonas.get("twitter.com") == Boolean.TRUE) {
//                                switchToSite(sExtensionManager.mPendingSwitchToSiteName);
//                            } else {
//
//                            }
//                            sExtensionManager.mPendingSwitchToSiteName = Site.DASHBOARD;
//                        }
//                        if (sExtensionManager.mPendingSwitchToSiteName == Site.FACEBOOK) {
//                            if (sExtensionManager.mConnectedPersonas.get("facebook.com") == Boolean.TRUE) {
//                                switchToSite(sExtensionManager.mPendingSwitchToSiteName);
//                            } else {
//
//                            }
//                            sExtensionManager.mPendingSwitchToSiteName = Site.DASHBOARD;
//                        }
//                    }
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public void extractPersonalStatus(JSONObject jsonObject) throws JSONException {
        sExtensionManager.mConnectedPersonas = new HashMap<>();
        String personasString = jsonObject.getString("result");
        Gson gson = new Gson();
        Type userListType = new TypeToken<ArrayList<ArrayList<ConnectedPersona>>>() {
        }.getType();
        ArrayList<ArrayList<ConnectedPersona>> connectedPersonaArray = gson.fromJson(personasString, userListType);
        for (ArrayList<ConnectedPersona> personasList : connectedPersonaArray) {
            for (ConnectedPersona persona : personasList) {
                if (persona.connected) {
                    sExtensionManager.mConnectedPersonas.put(persona.network, Boolean.TRUE);
                    Log.d("[App-Ext-Interact]", "[onPortMessage] extractPersonalStatus() put Boolean.TRUE: " + persona.network);
                }
            }
        }
    }

    public void updateConnectedPersonaStatus() {
        Log.d("[App-Ext-Interact]", "[onPortMessage] updateConnectedPersonaStatus() " + sExtensionManager.mConnectedPersonas.get("twitter.com"));
        Boolean twitterConnected = sExtensionManager.mConnectedPersonas.get("twitter.com") == Boolean.TRUE;
        Boolean facebookConnected = sExtensionManager.mConnectedPersonas.get("facebook.com") == Boolean.TRUE;
        mMaterialToolbar.getMenu().findItem(R.id.twitter).setVisible(twitterConnected);
        mMaterialToolbar.getMenu().findItem(R.id.facebook).setVisible(facebookConnected);
    }


    public void checkInitialLoadingStatus() {
        // When mInitialized is false, and all needed result fetched, switch to the default site
        if (sExtensionManager.mDashboardUrl != null &&
                sExtensionManager.mSettingLaunchPage != null &&
                sExtensionManager.mConnectedPersonas != null &&
                !mInitialized) {
            mInitialized = true;

            switch (sExtensionManager.mSettingLaunchPage.toLowerCase()) {
                case "dashboard":
                    switchToSite(Site.DASHBOARD);
                case "twitter":
                    if (sExtensionManager.mConnectedPersonas.get("twitter.com") == Boolean.TRUE) {
                        switchToSite(Site.TWITTER);
                    } else {
                        switchToSite(Site.DASHBOARD);
                    }
                case "facebook":
                    if (sExtensionManager.mConnectedPersonas.get("facebook.com") == Boolean.TRUE) {
                        switchToSite(Site.FACEBOOK);
                    } else {
                        switchToSite(Site.DASHBOARD);
                    }
                default:
                    switchToSite(Site.DASHBOARD);
            }
        }
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

//    @Override
//    public TabSession getSession(GeckoSession session) {
//        return mTabSessionManager.getSession(session);
//    }

//    @Override
//    public TabSession getCurrentSession() {
//        return mTabSessionManager.getCurrentSession();
//    }

//    @Override
//    public void onActionButton(ActionButton button) {
//        mToolbarView.setBrowserActionButton(button);
//    }

//    @Override
//    public GeckoSession toggleBrowserActionPopup(boolean force) {
//        if (mPopupSession == null) {
//            openPopupSession();
//        }
//
//        ViewGroup.LayoutParams params = mPopupView.getLayoutParams();
//        boolean shouldShow = force || params.width == 0;
//        setPopupVisibility(shouldShow);
//
//        return shouldShow ? mPopupSession : null;
//    }

    public void setPopupVisibility(boolean visible) {
        if (mPopupView == null) {
            return;
        }

        ViewGroup.LayoutParams params = mPopupView.getLayoutParams();

        if (visible) {
            params.height = 1100;
            params.width = 1200;
        } else {
            params.height = 0;
            params.width = 0;
        }

        mPopupView.setLayoutParams(params);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        onPreferencesChange(sharedPreferences);
    }

    private class PopupSessionContentDelegate implements GeckoSession.ContentDelegate {
        @Override
        public void onCloseRequest(final GeckoSession session) {
            setPopupVisibility(false);
            if (mPopupSession != null) {
                mPopupSession.close();
            }
            mPopupSession = null;
            mPopupView = null;
        }
    }

    public void openPopupSession() {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        mPopupView = inflater.inflate(R.layout.browser_action_popup, null);
        GeckoView geckoView = mPopupView.findViewById(R.id.gecko_view_popup);
        geckoView.setViewBackend(GeckoView.BACKEND_TEXTURE_VIEW);
        mPopupSession = new TabSession();
        mPopupSession.setContentDelegate(new PopupSessionContentDelegate());
        mPopupSession.open(sGeckoRuntime);
        geckoView.setSession(mPopupSession);

        mPopupView.setOnFocusChangeListener(this::hideBrowserAction);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, 0);
        params.addRule(RelativeLayout.ABOVE, R.id.toolbar);
        mPopupView.setLayoutParams(params);
        mPopupView.setFocusable(true);
        ((ViewGroup) findViewById(R.id.main)).addView(mPopupView);
    }

    private void hideBrowserAction(View view, boolean hasFocus) {
        if (!hasFocus) {
            ViewGroup.LayoutParams params = mPopupView.getLayoutParams();
            params.height = 0;
            params.width = 0;
            mPopupView.setLayoutParams(params);
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.activity_label);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public TabSession createSession(final @Nullable String cookieStoreId) {
        GeckoSessionSettings.Builder settingsBuilder = new GeckoSessionSettings.Builder();
        settingsBuilder
                .usePrivateMode(mUsePrivateBrowsing)
                .fullAccessibilityTree(mFullAccessibilityTree)
                .userAgentOverride(mUserAgent.value())
                .viewportMode(mDesktopMode
                        ? GeckoSessionSettings.VIEWPORT_MODE_DESKTOP
                        : GeckoSessionSettings.VIEWPORT_MODE_MOBILE)
                .userAgentMode(mDesktopMode
                        ? GeckoSessionSettings.USER_AGENT_MODE_DESKTOP
                        : GeckoSessionSettings.USER_AGENT_MODE_MOBILE)
                .useTrackingProtection(mTrackingProtection.value())
                .displayMode(mDisplayMode.value());

        if (cookieStoreId != null) {
            settingsBuilder.contextId(cookieStoreId);
        }

        TabSession session = mTabSessionManager.newSession(settingsBuilder.build());
        connectSession(session);

        return session;
    }

    private TabSession createSession() {
        return createSession(null);
    }

    private void connectSession(GeckoSession session) {
        session.setContentDelegate(new MaskbookContentDelegate());
        session.setHistoryDelegate(new MaskbookHistoryDelegate());
        final MaskbookContentBlockingDelegate cb = new MaskbookContentBlockingDelegate();
        session.setContentBlockingDelegate(cb);
        session.setProgressDelegate(new MaskbookProgressDelegate(cb));
        session.setNavigationDelegate(new MaskbookNavigationDelegate(this.manifestParser));

        final BasicGeckoViewPrompt prompt = new BasicGeckoViewPrompt(this);
        prompt.filePickerRequestCode = REQUEST_FILE_PICKER;
        session.setPromptDelegate(prompt);

        final MaskbookPermissionDelegate permission = new MaskbookPermissionDelegate();
        permission.androidPermissionRequestCode = REQUEST_PERMISSIONS;
        session.setPermissionDelegate(permission);

        session.setMediaDelegate(new MaskbookMediaDelegate(this));

        session.setSelectionActionDelegate(new BasicSelectionActionDelegate(this));
        if (sExtensionManager.extension != null) {
            final WebExtension.SessionController sessionController =
                    session.getWebExtensionController();
            sessionController.setActionDelegate(sExtensionManager.extension, sExtensionManager);
            sessionController.setTabDelegate(sExtensionManager.extension, sExtensionManager);
        }

        updateDesktopMode(session);
    }

    private void recreateSession() {
        recreateSession(mTabSessionManager.getCurrentSession());
    }

    public void recreateSession(TabSession session) {
        if (session != null) {
            mTabSessionManager.closeSession(session);
        }

        session = createSession();
        session.open(sGeckoRuntime);
        mTabSessionManager.setCurrentSession(session);
        mGeckoView.setSession(session);
        sGeckoRuntime.getWebExtensionController().setTabActive(session, true);
        if (mCurrentUri != null) {
            session.loadUri(mCurrentUri);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mTabSessionManager.setCurrentSession((TabSession) mGeckoView.getSession());
            if (sGeckoRuntime != null) {
                WebExtensionController webExtensionController = sGeckoRuntime.getWebExtensionController();
                if (webExtensionController != null && mGeckoView.getSession() != null) {
                    webExtensionController.setTabActive(mGeckoView.getSession(), true);
                }
            }
//            sGeckoRuntime.getWebExtensionController().setTabActive(mGeckoView.getSession(), true);
        } else {
            recreateSession();
        }
    }

    private void updateDesktopMode(GeckoSession session) {
        session.getSettings().setViewportMode(mDesktopMode
                ? GeckoSessionSettings.VIEWPORT_MODE_DESKTOP
                : GeckoSessionSettings.VIEWPORT_MODE_MOBILE);
        session.getSettings().setUserAgentMode(mDesktopMode
                ? GeckoSessionSettings.USER_AGENT_MODE_DESKTOP
                : GeckoSessionSettings.USER_AGENT_MODE_MOBILE);
    }


    private void clearGeckoViewSessionRuntime() {

        Log.d("[App-Ext-Interact]", "[GeckoViewActivity] clearGeckoViewRuntime");

        if (sGeckoRuntime != null) {
            sGeckoRuntime.shutdown();
            sGeckoRuntime = null;
        }

        GeckoSession geckoSession = mGeckoView.getSession();
        if (geckoSession != null) {
            geckoSession.close();
        }
        mGeckoView.releaseSession();

    }

    @Override
    public void onBackPressed() {
        GeckoSession session = mTabSessionManager.getCurrentSession();
        if (mFullScreen && session != null) {
            session.exitFullScreen();
            return;
        }

        if (mCanGoBack && session != null) {
            session.goBack();
            return;
        }

        Log.d("[App-Ext-Interact]", "[GeckoViewActivity] onBackPressed");
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.actions, menu);
        inflater.inflate(R.menu.top_nav_menu, menu);
        return true;
    }

    private void updateTrackingProtectionException() {
        if (sGeckoRuntime == null) {
            return;
        }

        final GeckoSession session = mTabSessionManager.getCurrentSession();
        if (session == null) {
            return;
        }

        sGeckoRuntime.getContentBlockingController()
                .checkException(session)
                .accept(value -> mTrackingProtectionException = value.booleanValue());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        menu.findItem(R.id.action_pb).setChecked(mUsePrivateBrowsing);
//        menu.findItem(R.id.desktop_mode).setChecked(mDesktopMode);
//        menu.findItem(R.id.action_tpe).setChecked(mTrackingProtectionException);
//        menu.findItem(R.id.action_forward).setEnabled(mCanGoForward);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        GeckoSession session = mTabSessionManager.getCurrentSession();
        switch (item.getItemId()) {
            case R.id.action_reload:
                session.reload();
                break;
            case R.id.action_forward:
                session.goForward();
                break;
            case R.id.action_tpe:
                sGeckoRuntime.getContentBlockingController().checkException(session).accept(value -> {
                    if (value.booleanValue()) {
                        sGeckoRuntime.getContentBlockingController().removeException(session);
                    } else {
                        sGeckoRuntime.getContentBlockingController().addException(session);
                    }
                    session.reload();
                });
                break;
            case R.id.desktop_mode:
                mDesktopMode = !mDesktopMode;
                updateDesktopMode(session);
                session.reload();
                break;
            case R.id.action_pb:
                mUsePrivateBrowsing = !mUsePrivateBrowsing;
                recreateSession();
                break;
            case R.id.install_addon:
                installAddon();
                break;
            case R.id.update_addon:
                updateAddon();
                break;
            case R.id.settings:
                openSettingsActivity();
                break;
            case R.id.action_new_tab:
                createNewTab();
                break;
            case R.id.action_close_tab:
                closeTab((TabSession) session);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void installAddon() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.install_addon);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(R.string.install_addon_hint);
        builder.setView(input);

        builder.setPositiveButton(R.string.install, (dialog, which) -> {
            final String uri = input.getText().toString();

            // We only suopport one extension at a time, so remove the currently installed
            // extension if there is one
            setPopupVisibility(false);
            mPopupView = null;
            mPopupSession = null;
            sExtensionManager.unregisterExtension().then(unused -> {
                final WebExtensionController controller = sGeckoRuntime.getWebExtensionController();
                controller.setPromptDelegate(sExtensionManager);
                return controller.install(uri);
            }).then(extension ->
                    sGeckoRuntime.getWebExtensionController().setAllowedInPrivateBrowsing(
                            extension, mAllowExtensionsInPrivateBrowsing.value())
            ).accept(extension ->
                    sExtensionManager.registerExtension(extension));
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            // Nothing to do
        });

        builder.show();
    }

    private void updateAddon() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.update_addon);

        sExtensionManager.updateExtension().accept(extension -> {
            if (extension != null) {
                builder.setMessage("Success");
            } else {
                builder.setMessage("No addon to update");
            }
            builder.show();
        }, exception -> {
            builder.setMessage("Failed: " + exception);
            builder.show();
        });
    }

    private void createNewTab() {
        Double startTime = sGeckoRuntime.getProfilerController().getProfilerTime();
        TabSession newSession = createSession();
        newSession.open(sGeckoRuntime);
        setGeckoViewSession(newSession);
        mToolbarView.updateTabCount();
        sGeckoRuntime.getProfilerController().addMarker("Create new tab", startTime);
    }

    public void closeTab(TabSession session) {
        if (mTabSessionManager.sessionCount() > 1) {
            mTabSessionManager.closeSession(session);
            TabSession tabSession = mTabSessionManager.getCurrentSession();
            if (tabSession == null) {
                return;
            }
            setGeckoViewSession(tabSession);
            tabSession.reload();
            mToolbarView.updateTabCount();
        } else {
            recreateSession(session);
        }
    }

//    @Override
//    public void updateTab(TabSession session, WebExtension.UpdateTabDetails details) {
//        if (details.active == Boolean.TRUE) {
//            switchToSession(session, false);
//        }
//    }

    public void onBrowserActionClick() {
        sExtensionManager.onClicked(mTabSessionManager.getCurrentSession());
    }

    public void switchToSession(TabSession session, boolean activateTab) {
        Log.d("[App-Ext-Interact]", "[onPortMessage] switchToSession:" + session.getUri());

        TabSession currentSession = mTabSessionManager.getCurrentSession();

        if (session != currentSession) {
            setGeckoViewSession(session, activateTab);
            mCurrentUri = session.getUri();
            if (!session.isOpen()) {
                // Session's process was previously killed; reopen
                session.open(sGeckoRuntime);
                session.loadUri(mCurrentUri);
                Log.d("[App-Ext-Interact]", "[onPortMessage] switchToSession !session.isOpen():" + mCurrentUri);
            }
            mToolbarView.getLocationView().setText(mCurrentUri);
            Log.d("[App-Ext-Interact]", "[onPortMessage] switchToSession session != currentSession:" + mCurrentUri);
        }
    }

    public void switchToSite(Site site) {
        Log.d("[App-Ext-Interact]", "[onPortMessage] switchToSite():" + site.name());

        if (site == Site.DASHBOARD) {
            if (sExtensionManager.mDashboardUrl != null) {
                mTabSessionManager.closeSession(mTabSessionManager.getCurrentSession());
                TabSession newSession = this.createSession();
                newSession.loadUri(sExtensionManager.mDashboardUrl);

                switchToSession(newSession, true);
            }
            return;
        }

        TabSession currentSession = mTabSessionManager.getCurrentSession();
        Uri currentUri = Uri.parse(currentSession.getUri());
        if (currentUri != null && currentUri.getHost() != null && currentUri.getHost().equalsIgnoreCase(SiteConfig.getSiteHost(site))) {
            // Already on the same site, just load the site on current session
            Log.d("[App-Ext-Interact]", "[onPortMessage] currentSession switchToSite:" + SiteConfig.getSiteUrl(site));
            currentSession.loadUri(SiteConfig.getSiteUrl(site));
        } else {
            mTabSessionManager.closeSession(mTabSessionManager.getCurrentSession());
            TabSession newSession = this.createSession();

            Log.d("[App-Ext-Interact]", "[onPortMessage] newSession switchToSite:" + SiteConfig.getSiteUrl(site));

            newSession.loadUri(SiteConfig.getSiteUrl(site));
            switchToSession(newSession, true);
        }
    }

    public void switchToTab(int index) {
        TabSession nextSession = mTabSessionManager.getSession(index);
        switchToSession(nextSession, true);
    }

    public void setGeckoViewSession(TabSession session) {
        setGeckoViewSession(session, true);
    }

    public void setGeckoViewSession(TabSession session, boolean activateTab) {
        if (session == null) {
            return;
        }

        Log.d("[App-Ext-Interact]", "[onPortMessage] setGeckoViewSession()");

        if (sGeckoRuntime != null) {

            Log.d("[App-Ext-Interact]", "[onPortMessage] setGeckoViewSession() sGeckoRuntime != null");

            final WebExtensionController controller = sGeckoRuntime.getWebExtensionController();
            final GeckoSession previousSession = mGeckoView.releaseSession();

            if (previousSession != null) {
                controller.setTabActive(previousSession, false);
                Log.d("[App-Ext-Interact]", "[onPortMessage] setGeckoViewSession() setTabActive(previousSession, true)");
            }
            if (activateTab) {
                controller.setTabActive(session, true);
                Log.d("[App-Ext-Interact]", "[onPortMessage] setGeckoViewSession() setTabActive(session, true)");
            }
        }

        mGeckoView.setSession(session);
        mTabSessionManager.setCurrentSession(session);
        Log.d("[App-Ext-Interact]", "[onPortMessage] setGeckoViewSession() after setCurrentSession(session)");
    }

    @Override
    public void onDestroy() {

        clearGeckoViewSessionRuntime();
        Log.d("[App-Ext-Interact]", "[GeckoViewActivity] onDestroy, clearGeckoViewSessionRuntime()");

        if (mKillProcessOnDestroy) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        super.onDestroy();
        Log.d("[App-Ext-Interact]", "[GeckoViewActivity] onDestroy");
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);

        if (ACTION_SHUTDOWN.equals(intent.getAction())) {
            mKillProcessOnDestroy = true;
            if (sGeckoRuntime != null) {
                sGeckoRuntime.shutdown();
            }
            finish();
            return;
        }

        if (intent.hasExtra("onClick")) {
            int key = intent.getExtras().getInt("onClick");
            WebNotification notification = mNotificationMap.get(key);
            if (notification != null) {
                notification.click();
                mNotificationMap.remove(key);
            }
        }

        setIntent(intent);

        if (intent.getData() != null) {
            loadFromIntent(intent);
        }
    }


    private void loadFromIntent(final Intent intent) {
        final Uri uri = intent.getData();
        if (uri != null) {
            mTabSessionManager.getCurrentSession().loadUri(uri.toString());
        }
    }

    @SuppressLint("ShowToast")
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {

        if (requestCode == REQUEST_CODE_SCAN) {
            if (resultCode == Activity.RESULT_OK) {

                String res = data.getStringExtra("scanResult");

                if (res != null && res.length() > 0) {
                    Log.d("scanner", "scanner return: " + res);
                    Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG).show();
                } else {
                    Log.d("scanner", "scanner return empty!");
                    Toast.makeText(getApplicationContext(), "scanner return empty", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.d("scanner", "scanner return failure!");
                Toast.makeText(getApplicationContext(), "scanner return failure", Toast.LENGTH_LONG).show();
            }

        } else if (requestCode == REQUEST_FILE_PICKER) {
            final BasicGeckoViewPrompt prompt = (BasicGeckoViewPrompt)
                    mTabSessionManager.getCurrentSession().getPromptDelegate();
            prompt.onFileCallbackResult(resultCode, data);
        } else if (requestCode == REQUEST_SAVE_DIR) {
            if (resultCode != Activity.RESULT_OK) {
                mDownloadResponse = null;
                return;
            }
            if (data != null) {
                if (mDownloadResponse != null) {
                    try {
                        FileOutputStream fileOutputStream = (FileOutputStream) getContentResolver().openOutputStream(data.getData());
                        OutputStream out = new BufferedOutputStream(fileOutputStream);
                        int bufferSize = 1024; // to read in 1Mb increments
                        byte[] buffer = new byte[bufferSize];
                        int len;
                        while ((len = mDownloadResponse.body.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                        }
                        out.close();
                        Toast.makeText(getApplicationContext(), "Save Successfully!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Fail to save!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            mDownloadResponse = null;
        } else if (mPendingActivityResult.containsKey(requestCode)) {
            final GeckoResult<Intent> result = mPendingActivityResult.remove(requestCode);

            if (resultCode == Activity.RESULT_OK) {
                result.complete(data);
            } else {
                result.completeExceptionally(new RuntimeException("Unknown error"));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           final String[] permissions,
                                           final int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            final MaskbookPermissionDelegate permission = (MaskbookPermissionDelegate)
                    mTabSessionManager.getCurrentSession().getPermissionDelegate();
            permission.onRequestPermissionsResult(permissions, grantResults);
        } else if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            continueDownloads();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void continueDownloads() {
        final LinkedList<WebResponse> downloads = mPendingDownloads;
        mPendingDownloads = new LinkedList<>();

        for (final WebResponse response : downloads) {
            downloadFile(response);
        }
    }

    private void downloadFile(final WebResponse response) {
        if (response.body == null) {
            return;
        }

        if (ContextCompat.checkSelfPermission(GeckoViewActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            mPendingDownloads.add(response);
            ActivityCompat.requestPermissions(GeckoViewActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
            return;
        }

        final String filename = getFileName(response);

        try {
            mDownloadResponse = response;

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(getMimeType(response));
            intent.putExtra(Intent.EXTRA_TITLE, filename);
            startActivityForResult(intent, REQUEST_SAVE_DIR);
        } catch (Throwable e) {
            Log.i(LOGTAG, String.valueOf(e.getStackTrace()));
        }
    }

    private String getFileName(final WebResponse response) {
        String filename = "MaskNetworkDownload";
        String contentDispositionHeader;
        if (response.headers.containsKey("content-disposition")) {
            contentDispositionHeader = response.headers.get("content-disposition");
            String[] list = contentDispositionHeader.split("=");
            if (list.length > 0) {
                filename = list[list.length - 1];
                filename = filename.replaceAll("\"", "");
                return filename;
            } else {
                return filename;
            }
        } else {
            return filename;
        }
    }

    private String getMimeType(final WebResponse response) {
        if (response.headers.containsKey("content-type")) {
            return response.headers.get("content-type");
        } else {
            return "text/plain";
        }
    }

    private static boolean isForeground() {
        final ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND ||
                appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
    }

    private String mErrorTemplate;

    private String createErrorPage(final String error) {
        if (mErrorTemplate == null) {
            InputStream stream = null;
            BufferedReader reader = null;
            StringBuilder builder = new StringBuilder();
            try {
                stream = getResources().getAssets().open("error.html");
                reader = new BufferedReader(new InputStreamReader(stream));

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append("\n");
                }

                mErrorTemplate = builder.toString();
            } catch (IOException e) {
                Log.d(LOGTAG, "Failed to open error page template", e);
                return null;
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        Log.e(LOGTAG, "Failed to close error page template stream", e);
                    }
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOGTAG, "Failed to close error page template reader", e);
                    }
                }
            }
        }

        return mErrorTemplate.replace("$ERROR", error);
    }

    private class MaskbookHistoryDelegate implements GeckoSession.HistoryDelegate {
        private final HashSet<String> mVisitedURLs;

        private MaskbookHistoryDelegate() {
            mVisitedURLs = new HashSet<String>();
        }

        @Override
        public GeckoResult<Boolean> onVisited(GeckoSession session, String url,
                                              String lastVisitedURL, int flags) {
            Log.i(LOGTAG, "Visited URL: " + url);

            mVisitedURLs.add(url);
            return GeckoResult.fromValue(true);
        }

        @Override
        public GeckoResult<boolean[]> getVisited(GeckoSession session, String[] urls) {
            boolean[] visited = new boolean[urls.length];
            for (int i = 0; i < urls.length; i++) {
                visited[i] = mVisitedURLs.contains(urls[i]);
            }
            return GeckoResult.fromValue(visited);
        }

        @Override
        public void onHistoryStateChange(final GeckoSession session,
                                         final GeckoSession.HistoryDelegate.HistoryList state) {
            Log.i(LOGTAG, "History state updated");
        }
    }

    private class MaskbookContentDelegate implements GeckoSession.ContentDelegate {
        @Override
        public void onTitleChange(GeckoSession session, String title) {
            Log.i(LOGTAG, "Content title changed to " + title);
            TabSession tabSession = mTabSessionManager.getSession(session);
            if (tabSession != null) {
                tabSession.setTitle(title);
            }
        }

        @Override
        public void onFullScreen(final GeckoSession session, final boolean fullScreen) {
            getWindow().setFlags(fullScreen ? WindowManager.LayoutParams.FLAG_FULLSCREEN : 0,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mFullScreen = fullScreen;
            if (fullScreen) {
                getSupportActionBar().hide();
            } else {
                getSupportActionBar().show();
            }
        }

        @Override
        public void onFocusRequest(final GeckoSession session) {
            Log.i(LOGTAG, "Content requesting focus");
        }

        @Override
        public void onCloseRequest(final GeckoSession session) {
            if (mTabSessionManager.sessionCount() > 1) {
                if (session instanceof TabSession) {
                    mTabSessionManager.closeSession((TabSession) session);
                    TabSession tabSession = mTabSessionManager.getCurrentSession();
                    if (tabSession == null) {
                        return;
                    }
                    setGeckoViewSession(tabSession);
                    mToolbarView.updateTabCount();
                }
            } else if (session == mTabSessionManager.getCurrentSession()) {
                finish();
            }
        }

        @Override
        public void onContextMenu(final GeckoSession session,
                                  int screenX, int screenY,
                                  final ContextElement element) {
            Log.d(LOGTAG, "onContextMenu screenX=" + screenX +
                    " screenY=" + screenY +
                    " type=" + element.type +
                    " linkUri=" + element.linkUri +
                    " title=" + element.title +
                    " alt=" + element.altText +
                    " srcUri=" + element.srcUri);
        }

        @Override
        public void onExternalResponse(@NonNull GeckoSession session, @NonNull WebResponse response) {
            downloadFile(response);
        }

        @Override
        public void onCrash(GeckoSession session) {
            Log.e(LOGTAG, "Crashed, reopening session");
            session.open(sGeckoRuntime);
        }

        @Override
        public void onKill(GeckoSession session) {
            TabSession tabSession = mTabSessionManager.getSession(session);
            if (tabSession == null) {
                return;
            }

            if (tabSession != mTabSessionManager.getCurrentSession()) {
                Log.e(LOGTAG, "Background session killed");
                return;
            }

            if (isForeground()) {
                throw new IllegalStateException("Foreground content process unexpectedly killed by OS!");
            }

            Log.e(LOGTAG, "Current session killed, reopening");

            tabSession.open(sGeckoRuntime);
            tabSession.loadUri(tabSession.getUri());
        }

        @Override
        public void onFirstComposite(final GeckoSession session) {
            Log.d(LOGTAG, "onFirstComposite");
        }

        @Override
        public void onWebAppManifest(final GeckoSession session, JSONObject manifest) {
            Log.d(LOGTAG, "onWebAppManifest: " + manifest);
        }

        private boolean activeAlert = false;

        @Override
        public GeckoResult<SlowScriptResponse> onSlowScript(final GeckoSession geckoSession,
                                                            final String scriptFileName) {
            BasicGeckoViewPrompt prompt = (BasicGeckoViewPrompt) mTabSessionManager.getCurrentSession().getPromptDelegate();
            if (prompt != null) {
                GeckoResult<SlowScriptResponse> result = new GeckoResult<SlowScriptResponse>();
                if (!activeAlert) {
                    activeAlert = true;
                    prompt.onSlowScriptPrompt(geckoSession, getString(R.string.slow_script), result);
                }
                return result.then(value -> {
                    activeAlert = false;
                    return GeckoResult.fromValue(value);
                });
            }
            return null;
        }

        @Override
        public void onMetaViewportFitChange(final GeckoSession session, final String viewportFit) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                return;
            }
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            if (viewportFit.equals("cover")) {
                layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            } else if (viewportFit.equals("contain")) {
                layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
            } else {
                layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
            }
            getWindow().setAttributes(layoutParams);
        }
    }

    private class MaskbookProgressDelegate implements GeckoSession.ProgressDelegate {
        private final MaskbookContentBlockingDelegate mCb;

        private MaskbookProgressDelegate(final MaskbookContentBlockingDelegate cb) {
            mCb = cb;
        }

        @Override
        public void onPageStart(GeckoSession session, String url) {
            Log.d("[App-Ext-Interact]", "[progress] Starting to load page at " + url);
            Log.d("[App-Ext-Interact]", "[progress] zerdatime " + SystemClock.elapsedRealtime() +
                    " - page load start");
            mCb.clearCounters();
        }

        @Override
        public void onPageStop(GeckoSession session, boolean success) {
            Log.d("[App-Ext-Interact]", "[progress] Stopping page load " + (success ? "successfully" : "unsuccessfully"));
            Log.d("[App-Ext-Interact]", "[progress] zerdatime " + SystemClock.elapsedRealtime() +
                    " - page load stop");
            mCb.logCounters();
        }

        @Override
        public void onProgressChange(GeckoSession session, int progress) {
            Log.d("[App-Ext-Interact]", "[progress] onProgressChange " + progress);

            mProgressView.setProgress(progress);

            if (progress > 0 && progress < 100) {
                mProgressView.setVisibility(View.VISIBLE);
            } else {
                mProgressView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onSecurityChange(GeckoSession session, SecurityInformation securityInfo) {
            Log.d("[App-Ext-Interact]", "[progress] Security status changed to " + securityInfo.securityMode);
        }

        @Override
        public void onSessionStateChange(GeckoSession session, GeckoSession.SessionState state) {
            Log.d("[App-Ext-Interact]", "[progress] New Session state: " + state.toString());
        }
    }

    private class MaskbookPermissionDelegate implements GeckoSession.PermissionDelegate {

        public int androidPermissionRequestCode = 1;
        private Callback mCallback;

        class MaskbookNotificationCallback implements GeckoSession.PermissionDelegate.Callback {
            private final GeckoSession.PermissionDelegate.Callback mCallback;

            MaskbookNotificationCallback(final GeckoSession.PermissionDelegate.Callback callback) {
                mCallback = callback;
            }

            @Override
            public void reject() {
                mShowNotificationsRejected = true;
                mCallback.reject();
            }

            @Override
            public void grant() {
                mShowNotificationsRejected = false;
                mCallback.grant();
            }
        }

        class MaskbookPersistentStorageCallback implements GeckoSession.PermissionDelegate.Callback {
            private final GeckoSession.PermissionDelegate.Callback mCallback;
            private final String mUri;

            MaskbookPersistentStorageCallback(final GeckoSession.PermissionDelegate.Callback callback, String uri) {
                mCallback = callback;
                mUri = uri;
            }

            @Override
            public void reject() {
                mCallback.reject();
            }

            @Override
            public void grant() {
                mAcceptedPersistentStorage.add(mUri);
                mCallback.grant();
            }
        }

        public void onRequestPermissionsResult(final String[] permissions,
                                               final int[] grantResults) {
            if (mCallback == null) {
                return;
            }

            final Callback cb = mCallback;
            mCallback = null;
            for (final int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    // At least one permission was not granted.
                    cb.reject();
                    return;
                }
            }
            cb.grant();
        }

        @Override
        public void onAndroidPermissionsRequest(final GeckoSession session, final String[] permissions,
                                                final Callback callback) {
            if (Build.VERSION.SDK_INT >= 23) {
                // requestPermissions was introduced in API 23.
                mCallback = callback;
                requestPermissions(permissions, androidPermissionRequestCode);
            } else {
                callback.grant();
            }
        }


        @Override
        public GeckoResult<Integer> onContentPermissionRequest(final GeckoSession session, final ContentPermission perm) {
            final int resId;
            switch (perm.permission) {
                case PERMISSION_GEOLOCATION:
                    resId = R.string.request_geolocation;
                    break;
                case PERMISSION_DESKTOP_NOTIFICATION:
                    resId = R.string.request_notification;
                    break;
                case PERMISSION_PERSISTENT_STORAGE:
                    resId = R.string.request_storage;
                    break;
                case PERMISSION_XR:
                    resId = R.string.request_xr;
                    break;
                case PERMISSION_AUTOPLAY_AUDIBLE:
                case PERMISSION_AUTOPLAY_INAUDIBLE:
                    if (!mAllowAutoplay.value()) {
                        return GeckoResult.fromValue(ContentPermission.VALUE_DENY);
                    } else {
                        return GeckoResult.fromValue(ContentPermission.VALUE_ALLOW);
                    }
                case PERMISSION_MEDIA_KEY_SYSTEM_ACCESS:
                    resId = R.string.request_media_key_system_access;
                    break;
                default:
                    return GeckoResult.fromValue(ContentPermission.VALUE_DENY);
            }

            final String title = getString(resId, Uri.parse(perm.uri).getAuthority());
            final BasicGeckoViewPrompt prompt = (BasicGeckoViewPrompt)
                    mTabSessionManager.getCurrentSession().getPromptDelegate();
            return prompt.onPermissionPrompt(session, title, perm);
        }

        private String[] normalizeMediaName(final MediaSource[] sources) {
            if (sources == null) {
                return null;
            }

            String[] res = new String[sources.length];
            for (int i = 0; i < sources.length; i++) {
                final int mediaSource = sources[i].source;
                final String name = sources[i].name;
                if (MediaSource.SOURCE_CAMERA == mediaSource) {
                    if (name.toLowerCase(Locale.ENGLISH).contains("front")) {
                        res[i] = getString(R.string.media_front_camera);
                    } else {
                        res[i] = getString(R.string.media_back_camera);
                    }
                } else if (!name.isEmpty()) {
                    res[i] = name;
                } else if (MediaSource.SOURCE_MICROPHONE == mediaSource) {
                    res[i] = getString(R.string.media_microphone);
                } else {
                    res[i] = getString(R.string.media_other);
                }
            }

            return res;
        }

        @Override
        public void onMediaPermissionRequest(final GeckoSession session, final String uri,
                                             final MediaSource[] video, final MediaSource[] audio,
                                             final MediaCallback callback) {
            // If we don't have device permissions at this point, just automatically reject the request
            // as we will have already have requested device permissions before getting to this point
            // and if we've reached here and we don't have permissions then that means that the user
            // denied them.
            if ((audio != null
                    && ContextCompat.checkSelfPermission(GeckoViewActivity.this,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                    || (video != null
                    && ContextCompat.checkSelfPermission(GeckoViewActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                callback.reject();
                return;
            }

            final String host = Uri.parse(uri).getAuthority();
            final String title;
            if (audio == null) {
                title = getString(R.string.request_video, host);
            } else if (video == null) {
                title = getString(R.string.request_audio, host);
            } else {
                title = getString(R.string.request_media, host);
            }

            String[] videoNames = normalizeMediaName(video);
            String[] audioNames = normalizeMediaName(audio);

            final BasicGeckoViewPrompt prompt = (BasicGeckoViewPrompt)
                    mTabSessionManager.getCurrentSession().getPromptDelegate();
            prompt.onMediaPrompt(session, title, video, audio, videoNames, audioNames, callback);
        }
    }

    private class MaskbookNavigationDelegate implements GeckoSession.NavigationDelegate {

        private final ManifestParser parser;

        public MaskbookNavigationDelegate(ManifestParser parser) {
            super();
            this.parser = parser;
        }

        @Override
        public void onLocationChange(GeckoSession session, final String url) {

            Log.d("[App-Ext-Interact]", "[navigation] onLocationChange():" + url);

            Uri uri = Uri.parse(url);
            if (uri != null) {
                String title = SiteConfig.getTitleByUri(uri);
                mMaterialToolbar.setTitle(title);
            }
            mToolbarView.getLocationView().setText(url);
            TabSession tabSession = mTabSessionManager.getSession(session);
            if (tabSession != null) {
                tabSession.onLocationChange(url);
            }
            mCurrentUri = url;
            updateTrackingProtectionException();

            Log.d("[App-Ext-Interact]", "[navigation] onLocationChange() ask GET_PERSONAS");
            sExtensionManager.sendMessage(WebExtensionManager.POST_MESSAGE_ID_GET_PERSONAS, "getConnectedPersonas", new ArrayList<>());
        }

        @Override
        public void onCanGoBack(GeckoSession session, boolean canGoBack) {
            mCanGoBack = canGoBack;
        }

        @Override
        public void onCanGoForward(GeckoSession session, boolean canGoForward) {
            mCanGoForward = canGoForward;
        }

        @Override
        public GeckoResult<AllowOrDeny> onLoadRequest(final GeckoSession session,
                                                      final LoadRequest request) {
            Log.d(LOGTAG, "onLoadRequest=" + request.uri +
                    " triggerUri=" + request.triggerUri +
                    " where=" + request.target +
                    " isRedirect=" + request.isRedirect +
                    " isDirectNavigation=" + request.isDirectNavigation);
            if (NavigationUtil.detectNavigationType(request.uri, this.parser) == NavigationType.UNIVERSAL_LINK) {
                Uri requestUri = Uri.parse(request.uri);
                Intent intent = new Intent(Intent.ACTION_VIEW, requestUri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                return GeckoResult.fromValue(AllowOrDeny.DENY);
            }
            return GeckoResult.fromValue(AllowOrDeny.ALLOW);
        }

        @Override
        public GeckoResult<AllowOrDeny> onSubframeLoadRequest(final GeckoSession session,
                                                              final LoadRequest request) {
            Log.d(LOGTAG, "onSubframeLoadRequest=" + request.uri +
                    " triggerUri=" + request.triggerUri +
                    " isRedirect=" + request.isRedirect +
                    "isDirectNavigation=" + request.isDirectNavigation);

            return GeckoResult.fromValue(AllowOrDeny.ALLOW);
        }

        @Override
        public GeckoResult<GeckoSession> onNewSession(final GeckoSession session, final String uri) {
            final TabSession newSession = createSession();
            mToolbarView.updateTabCount();
            setGeckoViewSession(newSession);
            // A reference to newSession is stored by mTabSessionManager,
            // which prevents the session from being garbage-collected.
            return GeckoResult.fromValue(newSession);
        }

        private String categoryToString(final int category) {
            switch (category) {
                case WebRequestError.ERROR_CATEGORY_UNKNOWN:
                    return "ERROR_CATEGORY_UNKNOWN";
                case WebRequestError.ERROR_CATEGORY_SECURITY:
                    return "ERROR_CATEGORY_SECURITY";
                case WebRequestError.ERROR_CATEGORY_NETWORK:
                    return "ERROR_CATEGORY_NETWORK";
                case WebRequestError.ERROR_CATEGORY_CONTENT:
                    return "ERROR_CATEGORY_CONTENT";
                case WebRequestError.ERROR_CATEGORY_URI:
                    return "ERROR_CATEGORY_URI";
                case WebRequestError.ERROR_CATEGORY_PROXY:
                    return "ERROR_CATEGORY_PROXY";
                case WebRequestError.ERROR_CATEGORY_SAFEBROWSING:
                    return "ERROR_CATEGORY_SAFEBROWSING";
                default:
                    return "UNKNOWN";
            }
        }

        private String errorToString(final int error) {
            switch (error) {
                case WebRequestError.ERROR_UNKNOWN:
                    return "ERROR_UNKNOWN";
                case WebRequestError.ERROR_SECURITY_SSL:
                    return "ERROR_SECURITY_SSL";
                case WebRequestError.ERROR_SECURITY_BAD_CERT:
                    return "ERROR_SECURITY_BAD_CERT";
                case WebRequestError.ERROR_NET_RESET:
                    return "ERROR_NET_RESET";
                case WebRequestError.ERROR_NET_INTERRUPT:
                    return "ERROR_NET_INTERRUPT";
                case WebRequestError.ERROR_NET_TIMEOUT:
                    return "ERROR_NET_TIMEOUT";
                case WebRequestError.ERROR_CONNECTION_REFUSED:
                    return "ERROR_CONNECTION_REFUSED";
                case WebRequestError.ERROR_UNKNOWN_PROTOCOL:
                    return "ERROR_UNKNOWN_PROTOCOL";
                case WebRequestError.ERROR_UNKNOWN_HOST:
                    return "ERROR_UNKNOWN_HOST";
                case WebRequestError.ERROR_UNKNOWN_SOCKET_TYPE:
                    return "ERROR_UNKNOWN_SOCKET_TYPE";
                case WebRequestError.ERROR_UNKNOWN_PROXY_HOST:
                    return "ERROR_UNKNOWN_PROXY_HOST";
                case WebRequestError.ERROR_MALFORMED_URI:
                    return "ERROR_MALFORMED_URI";
                case WebRequestError.ERROR_REDIRECT_LOOP:
                    return "ERROR_REDIRECT_LOOP";
                case WebRequestError.ERROR_SAFEBROWSING_PHISHING_URI:
                    return "ERROR_SAFEBROWSING_PHISHING_URI";
                case WebRequestError.ERROR_SAFEBROWSING_MALWARE_URI:
                    return "ERROR_SAFEBROWSING_MALWARE_URI";
                case WebRequestError.ERROR_SAFEBROWSING_UNWANTED_URI:
                    return "ERROR_SAFEBROWSING_UNWANTED_URI";
                case WebRequestError.ERROR_SAFEBROWSING_HARMFUL_URI:
                    return "ERROR_SAFEBROWSING_HARMFUL_URI";
                case WebRequestError.ERROR_CONTENT_CRASHED:
                    return "ERROR_CONTENT_CRASHED";
                case WebRequestError.ERROR_OFFLINE:
                    return "ERROR_OFFLINE";
                case WebRequestError.ERROR_PORT_BLOCKED:
                    return "ERROR_PORT_BLOCKED";
                case WebRequestError.ERROR_PROXY_CONNECTION_REFUSED:
                    return "ERROR_PROXY_CONNECTION_REFUSED";
                case WebRequestError.ERROR_FILE_NOT_FOUND:
                    return "ERROR_FILE_NOT_FOUND";
                case WebRequestError.ERROR_FILE_ACCESS_DENIED:
                    return "ERROR_FILE_ACCESS_DENIED";
                case WebRequestError.ERROR_INVALID_CONTENT_ENCODING:
                    return "ERROR_INVALID_CONTENT_ENCODING";
                case WebRequestError.ERROR_UNSAFE_CONTENT_TYPE:
                    return "ERROR_UNSAFE_CONTENT_TYPE";
                case WebRequestError.ERROR_CORRUPTED_CONTENT:
                    return "ERROR_CORRUPTED_CONTENT";
                default:
                    return "UNKNOWN";
            }
        }

        private String createErrorPage(final int category, final int error) {
            if (mErrorTemplate == null) {
                InputStream stream = null;
                BufferedReader reader = null;
                StringBuilder builder = new StringBuilder();
                try {
                    stream = getResources().getAssets().open("error.html");
                    reader = new BufferedReader(new InputStreamReader(stream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                        builder.append("\n");
                    }

                    mErrorTemplate = builder.toString();
                } catch (IOException e) {
                    Log.d(LOGTAG, "Failed to open error page template", e);
                    return null;
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            Log.e(LOGTAG, "Failed to close error page template stream", e);
                        }
                    }

                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            Log.e(LOGTAG, "Failed to close error page template reader", e);
                        }
                    }
                }
            }

            return GeckoViewActivity.this.createErrorPage(categoryToString(category) + " : " + errorToString(error));
        }

        @Override
        public GeckoResult<String> onLoadError(final GeckoSession session, final String uri,
                                               final WebRequestError error) {
            Log.d(LOGTAG, "onLoadError=" + uri +
                    " error category=" + error.category +
                    " error=" + error.code);

            return GeckoResult.fromValue("data:text/html," + createErrorPage(error.category, error.code));
        }
    }

    private class MaskbookContentBlockingDelegate
            implements ContentBlocking.Delegate {
        private int mBlockedAds = 0;
        private int mBlockedAnalytics = 0;
        private int mBlockedSocial = 0;
        private int mBlockedContent = 0;
        private int mBlockedTest = 0;
        private int mBlockedStp = 0;

        private void clearCounters() {
            mBlockedAds = 0;
            mBlockedAnalytics = 0;
            mBlockedSocial = 0;
            mBlockedContent = 0;
            mBlockedTest = 0;
            mBlockedStp = 0;
        }

        private void logCounters() {
            Log.d(LOGTAG, "Trackers blocked: " + mBlockedAds + " ads, " +
                    mBlockedAnalytics + " analytics, " +
                    mBlockedSocial + " social, " +
                    mBlockedContent + " content, " +
                    mBlockedTest + " test, " +
                    mBlockedStp + "stp");
        }


        @Override
        public void onContentBlocked(final GeckoSession session,
                                     final ContentBlocking.BlockEvent event) {
            Log.d(LOGTAG, "onContentBlocked" +
                    " AT: " + event.getAntiTrackingCategory() +
                    " SB: " + event.getSafeBrowsingCategory() +
                    " CB: " + event.getCookieBehaviorCategory() +
                    " URI: " + event.uri);
            if ((event.getAntiTrackingCategory() &
                    ContentBlocking.AntiTracking.TEST) != 0) {
                mBlockedTest++;
            }
            if ((event.getAntiTrackingCategory() &
                    ContentBlocking.AntiTracking.AD) != 0) {
                mBlockedAds++;
            }
            if ((event.getAntiTrackingCategory() &
                    ContentBlocking.AntiTracking.ANALYTIC) != 0) {
                mBlockedAnalytics++;
            }
            if ((event.getAntiTrackingCategory() &
                    ContentBlocking.AntiTracking.SOCIAL) != 0) {
                mBlockedSocial++;
            }
            if ((event.getAntiTrackingCategory() &
                    ContentBlocking.AntiTracking.CONTENT) != 0) {
                mBlockedContent++;
            }
            if ((event.getAntiTrackingCategory() &
                    ContentBlocking.AntiTracking.STP) != 0) {
                mBlockedStp++;
            }
        }

        @Override
        public void onContentLoaded(final GeckoSession session,
                                    final ContentBlocking.BlockEvent event) {
            Log.d(LOGTAG, "onContentLoaded" +
                    " AT: " + event.getAntiTrackingCategory() +
                    " SB: " + event.getSafeBrowsingCategory() +
                    " CB: " + event.getCookieBehaviorCategory() +
                    " URI: " + event.uri);
        }
    }

    private class MaskbookMediaDelegate
            implements GeckoSession.MediaDelegate {
        private Integer mLastNotificationId = 100;
        private Integer mNotificationId;
        final private Activity mActivity;

        public MaskbookMediaDelegate(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void onRecordingStatusChanged(@NonNull GeckoSession session, RecordingDevice[] devices) {
            String message;
            int icon;
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mActivity);
            RecordingDevice camera = null;
            RecordingDevice microphone = null;

            for (RecordingDevice device : devices) {
                if (device.type == RecordingDevice.Type.CAMERA) {
                    camera = device;
                } else if (device.type == RecordingDevice.Type.MICROPHONE) {
                    microphone = device;
                }
            }
            if (camera != null && microphone != null) {
                Log.d(LOGTAG, "MaskbookDeviceDelegate:onRecordingDeviceEvent display alert_mic_camera");
                message = getResources().getString(R.string.device_sharing_camera_and_mic);
                icon = R.drawable.ic_perm_camera_mic_white_24dp;
            } else if (camera != null) {
                Log.d(LOGTAG, "MaskbookDeviceDelegate:onRecordingDeviceEvent display alert_camera");
                message = getResources().getString(R.string.device_sharing_camera);
                icon = R.drawable.ic_camera_alt_white_24dp;
            } else if (microphone != null) {
                Log.d(LOGTAG, "MaskbookDeviceDelegate:onRecordingDeviceEvent display alert_mic");
                message = getResources().getString(R.string.device_sharing_microphone);
                icon = R.drawable.ic_mic_white_24dp;
            } else {
                Log.d(LOGTAG, "MaskbookDeviceDelegate:onRecordingDeviceEvent dismiss any notifications");
                if (mNotificationId != null) {
                    notificationManager.cancel(mNotificationId);
                    mNotificationId = null;
                }
                return;
            }
            if (mNotificationId == null) {
                mNotificationId = ++mLastNotificationId;
            }

            Intent intent = new Intent(mActivity, GeckoViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(mActivity.getApplicationContext(), 0, intent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(mActivity.getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(icon)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE);

            notificationManager.notify(mNotificationId, builder.build());
        }
    }

    private final class MaskbookTelemetryDelegate
            implements RuntimeTelemetry.Delegate {
        @Override
        public void onHistogram(final @NonNull RuntimeTelemetry.Histogram histogram) {
            Log.d(LOGTAG, "onHistogram " + histogram);
        }

        @Override
        public void onBooleanScalar(final @NonNull RuntimeTelemetry.Metric<Boolean> scalar) {
            Log.d(LOGTAG, "onBooleanScalar " + scalar);
        }

        @Override
        public void onLongScalar(final @NonNull RuntimeTelemetry.Metric<Long> scalar) {
            Log.d(LOGTAG, "onLongScalar " + scalar);
        }

        @Override
        public void onStringScalar(final @NonNull RuntimeTelemetry.Metric<String> scalar) {
            Log.d(LOGTAG, "onStringScalar " + scalar);
        }
    }
}
