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
package com.dimension.maskbook.common;

import com.dimension.maskbook.component.TabSession;
import com.dimension.maskbook.widget.ActionButton;

import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.WebExtension;

public interface WebExtensionDelegate {
    default GeckoSession toggleBrowserActionPopup(boolean force) {
        return null;
    }
    default void onActionButton(ActionButton button) {}
    default TabSession getSession(GeckoSession session) {
        return null;
    }
    default TabSession getCurrentSession() {
        return null;
    }
    default void closeTab(TabSession session) {}
    default void updateTab(TabSession session, WebExtension.UpdateTabDetails details) {}
    default TabSession openNewTab(WebExtension.CreateTabDetails details) { return null; }
    default void onPortMessage(Object message, WebExtension.Port port) { }
    }
