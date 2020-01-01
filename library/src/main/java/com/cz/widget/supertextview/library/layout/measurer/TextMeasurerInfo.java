package com.cz.widget.supertextview.library.layout.measurer;

import android.graphics.Paint;
import android.text.Spanned;
import android.text.TextPaint;

/**
 * 文本测量运算信息条目
 */
public class TextMeasurerInfo {
    /**
     * 缓存的TextMeasurerItem对象
     */
    private static final TextMeasurerInfo[] cached = new TextMeasurerInfo[3];
    public CharSequence source;
    public Spanned spanned;
    public TextPaint paint;
    public TextPaint workPaint;
    public Paint.FontMetricsInt fontMetricsInt;
    public int outerWidth;
    public int start;
    public int end;
    public float top;
    public int align;

    private TextMeasurerInfo() {
    }

    /**
     * 获取一个直接使用的TextMeasurerItem运算对象
     * @return
     */
    public static TextMeasurerInfo obtain() {
        TextMeasurerInfo tl;
        synchronized (cached) {
            for (int i = cached.length; --i >= 0;) {
                if (cached[i] != null) {
                    tl = cached[i];
                    cached[i] = null;
                    return tl;
                }
            }
        }
        tl = new TextMeasurerInfo();
        return tl;
    }

    /**
     * 回收一个textLine对象
     * @param item
     * @return
     */
    public static void recycle(TextMeasurerInfo item) {
        item.recycler();
    }

    /**
     * 回收信息
     */
    public void recycler() {
        source=null;
        spanned=null;
        fontMetricsInt=null;
        paint=null;
        workPaint=null;
        start =0;
        end =0;
        outerWidth=0;
        top =0;
        align=0;
        synchronized(cached) {
            for (int i = 0; i < cached.length; ++i) {
                if (cached[i] == null) {
                    cached[i] = this;
                    break;
                }
            }
        }
    }
}
