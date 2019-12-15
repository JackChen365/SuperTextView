package com.cz.widget.supertextview.library.span;

import android.os.Parcel;
import android.text.TextPaint;

import com.cz.widget.supertextview.library.style.CharacterStyle;
import com.cz.widget.supertextview.library.style.UpdateAppearance;
import com.cz.widget.supertextview.library.utils.TextUtilsCompat;

public class UnderlineSpan extends CharacterStyle implements UpdateAppearance {
    public UnderlineSpan() {
    }
    
    public UnderlineSpan(Parcel src) {
    }
    
    public int getSpanTypeId() {
        return TextUtilsCompat.UNDERLINE_SPAN;
    }
    
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
    }

	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setUnderlineText(true);
	}
}
