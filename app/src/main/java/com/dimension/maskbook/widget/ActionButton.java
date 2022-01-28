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

import android.graphics.Bitmap;

public class ActionButton {
    final Bitmap icon;
    final String text;
    final Integer textColor;
    final Integer backgroundColor;

    public ActionButton(final Bitmap icon, final String text, final Integer textColor,
                        final Integer backgroundColor) {
        this.icon = icon;
        this.text = text;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }
}
