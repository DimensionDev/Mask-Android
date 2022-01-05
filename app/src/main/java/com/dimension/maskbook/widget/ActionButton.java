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
