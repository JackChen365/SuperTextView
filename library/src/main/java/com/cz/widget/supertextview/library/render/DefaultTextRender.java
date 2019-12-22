package com.cz.widget.supertextview.library.render;

import android.graphics.Canvas;
import android.graphics.Paint;
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
    public void addReplacementSpan(ReplacementSpan span, Paint.FontMetricsInt fontMetricsInt, CharSequence text, int start, int end, float x, float y, float width) {
    }

    @Override
    public void removeSpan(ReplacementSpan span, CharSequence source, int start, int end) {

    }

    @Override
    public void drawReplacementSpan(Canvas canvas, TextPaint textPaint,TextPaint workPaint,ReplacementSpan replacementSpan, CharSequence text, int start, int end, float x, float y,int top,int bottom) {
        replacementSpan.draw(canvas,text, start, end, x, y,textPaint);
    }

    /**
     * 绘制背景
     * @param canvas
     * @param workPaint
     * @param text
     * @param start
     * @param end
     * @param x
     * @param y
     */
    private void drawTextBackgroundColor(Canvas canvas,TextPaint workPaint,CharSequence text, int start, int end,float x,float y,int top,int bottom){
        if (workPaint.bgColor != 0) {
            int c = workPaint.getColor();
            Paint.Style s = workPaint.getStyle();
            workPaint.setColor(workPaint.bgColor);
            workPaint.setStyle(Paint.Style.FILL);
            float ret = workPaint.measureText(text,start,end);
            canvas.drawRect(x, top, x + ret, bottom, workPaint);
            workPaint.setStyle(s);
            workPaint.setColor(c);
        }
    }
    /**
     *
     * @param canvas
     * @param text
     * @param start
     * @param end
     */
    @Override
    public void drawText(Canvas canvas,TextPaint textPaint, CharSequence text, int start, int end,float x,float y,int top,int bottom) {
        //绘背景
        drawTextBackgroundColor(canvas,textPaint,text,start,end,x,y,top,bottom);
        //绘文本
        canvas.drawText(text,start,end,x,y,textPaint);
    }
}
