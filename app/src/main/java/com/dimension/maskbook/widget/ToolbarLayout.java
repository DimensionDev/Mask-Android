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
package com.dimension.maskbook.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.dimension.maskbook.R;
import com.dimension.maskbook.component.TabSessionManager;

public class ToolbarLayout extends LinearLayout {
    public interface TabListener {
        void switchToTab(int tabId);
        void onBrowserActionClick();
    }

    private LocationView mLocationView;
    private Button mTabsCountButton;
    private View mBrowserAction;
    private TabListener mTabListener;
    private TabSessionManager mSessionManager;

    public ToolbarLayout(Context context, TabSessionManager sessionManager) {
        super(context);
        mSessionManager = sessionManager;
        initView();
    }

    private void initView() {
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
        setOrientation(LinearLayout.HORIZONTAL);
        mLocationView = new LocationView(getContext());
        mLocationView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1.0f));
        mLocationView.setId(R.id.url_bar);
        addView(mLocationView);

        mTabsCountButton = getTabsCountButton();
        addView(mTabsCountButton);

        mBrowserAction = getBrowserAction();
        addView(mBrowserAction);
    }

    private Button getTabsCountButton() {
        Button button = new Button(getContext());
        button.setLayoutParams(new LayoutParams(150, LayoutParams.MATCH_PARENT));
        button.setId(R.id.tabs_button);
        button.setOnClickListener(this::onTabButtonClicked);
        button.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.tab_number_background));
        button.setTypeface(button.getTypeface(), Typeface.BOLD);
        return button;
    }

    private View getBrowserAction() {
        View browserAction = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
            .inflate(R.layout.browser_action, this, false);
        browserAction.setVisibility(GONE);
        return browserAction;
    }

    public void setBrowserActionButton(ActionButton button) {
        if (button == null) {
            mBrowserAction.setVisibility(GONE);
            return;
        }

        BitmapDrawable drawable = new BitmapDrawable(getContext().getResources(), button.icon);
        ImageView view = mBrowserAction.findViewById(R.id.browser_action_icon);
        view.setOnClickListener(this::onBrowserActionButtonClicked);
        view.setBackground(drawable);

        TextView badge = mBrowserAction.findViewById(R.id.browser_action_badge);
        if (button.text != null && !button.text.equals("")) {
            if (button.backgroundColor != null) {
                GradientDrawable backgroundDrawable = ((GradientDrawable) badge.getBackground().mutate());
                backgroundDrawable.setColor(button.backgroundColor);
                backgroundDrawable.invalidateSelf();
            }
            if (button.textColor != null) {
                badge.setTextColor(button.textColor);
            }
            badge.setText(button.text);
            badge.setVisibility(VISIBLE);
        } else {
            badge.setVisibility(GONE);
        }

        mBrowserAction.setVisibility(VISIBLE);
    }

    public void onBrowserActionButtonClicked(View view) {
        mTabListener.onBrowserActionClick();
    }

    public LocationView getLocationView() {
        return mLocationView;
    }

    public void setTabListener(TabListener listener) {
        this.mTabListener = listener;
    }

    public void updateTabCount() {
        mTabsCountButton.setText(String.valueOf(mSessionManager.sessionCount()));
    }

    public void onTabButtonClicked(View view) {
        PopupMenu tabButtonMenu = new PopupMenu(getContext(), mTabsCountButton);
        for(int idx = 0; idx < mSessionManager.sessionCount(); ++idx) {
            tabButtonMenu.getMenu().add(0, idx, idx,
                    mSessionManager.getSession(idx).getTitle());
        }
        tabButtonMenu.setOnMenuItemClickListener(item -> {
            mTabListener.switchToTab(item.getItemId());
            return true;
        });
        tabButtonMenu.show();
    }

}
