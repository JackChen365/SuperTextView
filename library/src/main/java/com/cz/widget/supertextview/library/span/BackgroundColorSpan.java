package com.cz.widget.supertextview.library.span;

import android.os.Parcel;
import android.text.TextPaint;

import com.cz.widget.supertextview.library.style.CharacterStyle;
import com.cz.widget.supertextview.library.style.UpdateAppearance;
import com.cz.widget.supertextview.library.utils.TextUtilsCompat;

public class BackgroundColorSpan extends CharacterStyle implements UpdateAppearance {

    private final int mColor;

	public BackgroundColorSpan(int color) {
		mColor = color;
	}

    public BackgroundColorSpan(Parcel src) {
        mColor = src.readInt();
    }
    
    public int getSpanTypeId() {
        return TextUtilsCompat.BACKGROUND_COLOR_SPAN;
    }
    
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mColor);
    }

	public int getBackgroundColor() {
		return mColor;
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		ds.bgColor = mColor;
	}
}
