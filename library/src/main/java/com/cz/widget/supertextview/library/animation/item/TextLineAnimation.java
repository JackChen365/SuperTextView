package com.cz.widget.supertextview.library.animation.item;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;

/**
 * 文本行动画信息，用于行动画属性扩展
 */
public class TextLineAnimation extends BaseTextAnimation {

    public CharSequence getText() {
        CharSequence source = getSource();
        int start = getStart();
        int end = getEnd();
        return source.subSequence(start,end);
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

    @Override
    public void onDraw(Canvas canvas,TextPaint textPaint, CharSequence text, int start, int end, float x, float y,int top,int bottom) {
        super.onDraw(canvas,textPaint,text,start,end,x,y,top,bottom);
        //开始绘图
        canvas.save();
        //旋转
        canvas.rotate(rotate);
        //渐变
        textPaint.setAlpha((int) (alpha*0xFF));
        //剪切指定矩阵
        if(null!=clipRect&&null!=op){
            canvas.clipRect(clipRect, op);
        }
        //绘制元素
        float offsetX = getX();
        float offsetY = getY();
        drawTextBackgroundColor(canvas,textPaint,text,start,end,offsetX,offsetY,top,bottom);
        canvas.drawText(source,start,end,offsetX,offsetY,textPaint);
        canvas.restore();
    }
}
