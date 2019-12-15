package com.cz.widget.supertextview.library.span;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcel;
import android.text.TextPaint;

import com.cz.widget.supertextview.library.style.MetricAffectingSpan;
import com.cz.widget.supertextview.library.utils.TextUtilsCompat;


/**
 * 
 * Describes a style in a span.
 * Note that styles are cumulative -- if both bold and italic are set in
 * separate spans, or if the base style is bold and a span calls for italic,
 * you get bold italic.  You can't turn off a style from the base style.
 *
 */
public class StyleSpan extends MetricAffectingSpan {

	private final int mStyle;

	/**
	 * 
	 * @param style An integer constant describing the style for this span. Examples
	 * include bold, italic, and normal. Values are constants defined 
	 * in {@link Typeface}.
	 */
	public StyleSpan(int style) {
		mStyle = style;
	}

    public StyleSpan(Parcel src) {
        mStyle = src.readInt();
    }
    
    public int getSpanTypeId() {
        return TextUtilsCompat.STYLE_SPAN;
    }
    
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mStyle);
    }

	/**
	 * Returns the style constant defined in {@link Typeface}.
	 */
	public int getStyle() {
		return mStyle;
	}

	@Override
    public void updateDrawState(TextPaint ds) {
        apply(ds, mStyle);
    }

	@Override
    public void updateMeasureState(TextPaint paint) {
        apply(paint, mStyle);
    }

    private static void apply(Paint paint, int style) {
        int oldStyle;

        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }
        int want = oldStyle| style;

        Typeface tf;
        if (old == null) {
            tf = Typeface.defaultFromStyle(want);
        } else {
            tf = Typeface.create(old, want);
        }

        int fake = want & ~tf.getStyle();

        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(tf);
    }
}
