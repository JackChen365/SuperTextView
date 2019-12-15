package com.cz.widget.supertextview.library.render;

import android.graphics.Canvas;
import android.text.TextPaint;

/**
 * 默认的文本渲染器
 */
public class DefaultTextRender extends TextRender {
    /**
     *
     * @param canvas
     * @param textPaint
     * @param text
     * @param start
     * @param end
     */
    @Override
    public void drawText(Canvas canvas, TextPaint textPaint, CharSequence text, int start, int end,float x,float y) {
        canvas.drawText(text,start,end,x,y,textPaint);
    }
}
