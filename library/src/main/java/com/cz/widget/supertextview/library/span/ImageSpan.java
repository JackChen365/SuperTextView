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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;

import java.io.InputStream;

public class ImageSpan extends DynamicDrawableSpan {
    private Drawable mDrawable;
    private Uri mContentUri;
    private int mResourceId;
    private Context mContext;
    private String mSource;

    /**
     * @deprecated Use {@link #ImageSpan(android.graphics.drawable.Drawable, int)} instead.
     */
    @Deprecated
    public ImageSpan(Bitmap b, int gravity) {
        this(null, b,gravity);
    }

    public ImageSpan(Context context, Bitmap b, int gravity) {
        super(gravity);
        mContext = context;
        mDrawable = context != null
                ? new BitmapDrawable(context.getResources(), b)
                : new BitmapDrawable(b);
        int width = mDrawable.getIntrinsicWidth();
        int height = mDrawable.getIntrinsicHeight();
        mDrawable.setBounds(0, 0, width > 0 ? width : 0, height > 0 ? height : 0);
    }

    public ImageSpan(Drawable d){
        this(d, Gravity.TOP);
    }

    public ImageSpan(Drawable d, int gravity) {
        super(gravity);
        mDrawable = d;
    }

    public ImageSpan(Drawable d, String source, int gravity) {
        super(gravity);
        mDrawable = d;
        mSource = source;
    }

    public ImageSpan(Context context, Uri uri, int gravity) {
        super(gravity);
        mContext = context;
        mContentUri = uri;
        mSource = uri.toString();
    }

    public ImageSpan(Context context, int resourceId, int gravity) {
        super(gravity);
        mContext = context;
        mResourceId = resourceId;
    }

    @Override
    public Drawable getDrawable() {
        Drawable drawable = null;
        
        if (mDrawable != null) {
            drawable = mDrawable;
        } else  if (mContentUri != null) {
            Bitmap bitmap = null;
            try {
                InputStream is = mContext.getContentResolver().openInputStream(
                        mContentUri);
                bitmap = BitmapFactory.decodeStream(is);
                drawable = new BitmapDrawable(mContext.getResources(), bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
                is.close();
            } catch (Exception e) {
                Log.e("sms", "Failed to loaded content " + mContentUri, e);
            }
        } else {
            try {
                drawable = mContext.getResources().getDrawable(mResourceId);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
            } catch (Exception e) {
                Log.e("sms", "Unable to find resource: " + mResourceId);
            }                
        }

        return drawable;
    }

    /**
     * Returns the source string that was saved during construction.
     */
    public String getSource() {
        return mSource;
    }

}
