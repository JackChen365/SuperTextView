package com.cz.widget.supertextview.library.render;

import android.graphics.Canvas;
import android.text.TextPaint;

import com.cz.widget.supertextview.library.style.ReplacementSpan;

/**
 * 默认的文本渲染器
 */
public class DefaultTextRender extends TextRender {

    @Override
    public void addText(CharSequence text, int start, int end, float x, float y, TextPaint textPaint) {
    }

    @Override
    public void removeText(CharSequence source, int start, int end) {
    }

    @Override
    public void drawReplacementSpan(Canvas canvas, ReplacementSpan replacementSpan, CharSequence text, int start, int end, float x, int top, int y, int bottom, TextPaint textPaint) {
        replacementSpan.draw(canvas, text, start, end, x, top, y, bottom, textPaint);
    }

    /**
     *
     * @param canvas
     * @param text
     * @param start
     * @param end
     * @param textPaint
     */
    @Override
    public void drawText(Canvas canvas, CharSequence text, int start, int end,float x,float y,TextPaint textPaint) {
        canvas.drawText(text,start,end,x,y,textPaint);
    }
}
