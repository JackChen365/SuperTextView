/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cz.widget.supertextview.library.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

import com.cz.widget.supertextview.library.style.ReplacementSpan;

import java.lang.ref.WeakReference;

/**
 *
 */
public abstract class DynamicDrawableSpan extends ReplacementSpan {
    private static final String TAG = "DynamicDrawableSpan";
    private final int gravity;

    public DynamicDrawableSpan(){
        this.gravity=Gravity.TOP;
    }

    public DynamicDrawableSpan(int gravity) {
        this.gravity=gravity;
    }

    /**
     * Your subclass must implement this method to provide the bitmap   
     * to be drawn.  The dimensions of the bitmap must be the same
     * from each call to the next.
     */
    public abstract Drawable getDrawable();

    @Override
    public int getSize(Paint paint, CharSequence text,
                         int start, int end,
                         Paint.FontMetricsInt fm) {
        Drawable d = getCachedDrawable();
        Rect rect = d.getBounds();

        if (fm != null) {
            fm.ascent = -rect.bottom; 
            fm.descent = 0; 

            fm.top = fm.ascent;
            fm.bottom = 0;
        }

        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text,
                     int start, int end, float x, float y, Paint paint) {
        Drawable b = getCachedDrawable();
        canvas.save();
        canvas.translate(x, y);
        b.draw(canvas);
        canvas.restore();
    }

    private Drawable getCachedDrawable() {
        WeakReference<Drawable> wr = mDrawableRef;
        Drawable d = null;

        if (wr != null)
            d = wr.get();

        if (d == null) {
            d = getDrawable();
            mDrawableRef = new WeakReference<>(d);
        }

        return d;
    }

    private WeakReference<Drawable> mDrawableRef;
}

