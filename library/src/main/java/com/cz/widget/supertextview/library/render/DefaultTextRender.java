package com.cz.widget.supertextview.library.render;

import android.graphics.Canvas;
import android.text.TextPaint;

import com.cz.widget.supertextview.library.style.ReplacementSpan;

/**
 * 默认的文本渲染器
 */
public class DefaultTextRender extends TextRender {

    @Override
    public void addText(CharSequence text, int start, int end, float x, float y, float textWidth, TextPaint textPaint) {
    }

    @Override
    public void removeText(CharSequence source, int start, int end) {
    }

    @Override
    public void addReplacementSpan(ReplacementSpan span, CharSequence text, int start, int end, float x, float y, float width, TextPaint textPaint) {
    }

    @Override
    public void removeSpan(ReplacementSpan span, CharSequence source, int start, int end) {

    }

    @Override
    public void drawReplacementSpan(Canvas canvas, TextPaint textPaint,TextPaint workPaint,ReplacementSpan replacementSpan, CharSequence text, int start, int end, float x, float y) {
        replacementSpan.draw(canvas, text, start, end, x, y, textPaint);
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
    public void drawText(Canvas canvas,TextPaint textPaint,TextPaint workPaint, CharSequence text, int start, int end,float x,float y) {
        canvas.drawText(text,start,end,x,y,textPaint);
    }
}
