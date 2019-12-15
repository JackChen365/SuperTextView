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

import android.os.Parcel;
import android.text.TextPaint;

import com.cz.widget.supertextview.library.style.MetricAffectingSpan;
import com.cz.widget.supertextview.library.utils.TextUtilsCompat;

public class SuperscriptSpan extends MetricAffectingSpan {
    public SuperscriptSpan() {
    }
    
    public SuperscriptSpan(Parcel src) {
    }
    
    public int getSpanTypeId() {
        return TextUtilsCompat.SUPERSCRIPT_SPAN;
    }
    
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public void updateDrawState(TextPaint tp) {
//        tp.baselineShift += (int) (tp.ascent() / 2);
    }

    @Override
    public void updateMeasureState(TextPaint tp) {
//        tp.baselineShift += (int) (tp.ascent() / 2);
    }
}
