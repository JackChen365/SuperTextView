package com.cz.widget.supertextview.library.render;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;

import com.cz.widget.supertextview.library.style.ReplacementSpan;

/**
 * 文本渲染器
 * 可用于扩展自定义规则渲染
 */
public abstract class TextRender {

    /**
     * 当前关联对象
     */
    private ViewGroup target;
    /**
     * 设置关联target对象
     * @param target
     */
    public void setTarget(ViewGroup target) {
        this.target = target;
    }

    public ViewGroup getTarget() {
        return target;
    }

    /**
     * 绘制视图
     */
    public void invalidate(){
        target.invalidate();
    }

    /**
     * 添加文本信息
     * @param text
     * @param start
     * @param end
     * @param x
     * @param y
     * @param textPaint
     */
    public abstract void addText(CharSequence text, int start, int end, float x, float y,float textWidth,TextPaint textPaint);
    /**
     * 移除文本信息
     * @param source
     * @param start
     * @param end
     */
    public abstract void removeText(CharSequence source, int start, int end);

    /**
     * 添加添加信息
     * @param span
     * @param x
     * @param y
     */
    public abstract void addReplacementSpan(ReplacementSpan span, Paint.FontMetricsInt fontMetricsInt,CharSequence text, int start, int end, float x, float y, float width);

    /**
     * 移除span信息信息
     */
    public abstract void removeSpan(ReplacementSpan span,CharSequence source, int start, int end);

    /**
     * 绘制span元素
     * @param canvas
     * @param replacementSpan
     * @param text
     * @param start
     * @param end
     * @param x
     * @param y
     * @param textPaint
     */
    public abstract void drawReplacementSpan(Canvas canvas, TextPaint textPaint,TextPaint workPaint,ReplacementSpan replacementSpan,CharSequence text, int start, int end, float x, float y,int top,int bottom);

    /**
     * 绘制文本信息
     * @param canvas
     * @param text
     * @param start
     * @param end
     * @param x
     * @param y
     * @param textPaint
     */
    public abstract void drawText(Canvas canvas,TextPaint textPaint,CharSequence text, int start, int end,float x,float y,int top,int bottom);

    /**
     * 绘制其他元素
     * @param canvas
     */
    public void onDraw(Canvas canvas){
    }

}
