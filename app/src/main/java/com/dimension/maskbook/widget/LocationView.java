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
import androidx.appcompat.widget.AppCompatEditText;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.dimension.maskbook.R;

public class LocationView extends AppCompatEditText {

    private CommitListener mCommitListener;
    private FocusAndCommitListener mFocusCommitListener = new FocusAndCommitListener();

    public interface CommitListener {
        void onCommit(String text);
    }

    public LocationView(Context context) {
        super(context);

        this.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_URI);
        this.setSingleLine(true);
        this.setSelectAllOnFocus(true);
        this.setHint(R.string.location_hint);

        setOnFocusChangeListener(mFocusCommitListener);
        setOnEditorActionListener(mFocusCommitListener);
    }

    public void setCommitListener(CommitListener listener) {
        mCommitListener = listener;
    }

    private class FocusAndCommitListener implements OnFocusChangeListener, OnEditorActionListener {
        private String mInitialText;
        private boolean mCommitted;

        @Override
        public void onFocusChange(View view, boolean focused) {
            if (focused) {
                mInitialText = ((TextView)view).getText().toString();
                mCommitted = false;
            } else if (!mCommitted) {
                setText(mInitialText);
            }
        }

        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (mCommitListener != null) {
                mCommitListener.onCommit(textView.getText().toString());
            }

            mCommitted = true;
            return true;
        }
    }
}
