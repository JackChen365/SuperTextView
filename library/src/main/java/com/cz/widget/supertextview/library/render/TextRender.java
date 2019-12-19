package com.cz.widget.supertextview.library.render;

import android.graphics.Canvas;
import android.text.TextPaint;
import android.view.View;

import com.cz.widget.supertextview.library.style.ReplacementSpan;

/**
 * 文本渲染器
 */
public abstract class TextRender {

    /**
     * 当前关联对象
     */
    private View target;
    /**
     * 设置关联target对象
     * @param target
     */
    public void setTarget(View target) {
        this.target = target;
    }

    protected View getTarget() {
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
    public abstract void addText(CharSequence text, int start, int end, float x, float y, TextPaint textPaint);
    /**
     * 移除文本信息
     * @param source
     * @param start
     * @param end
     */
    public abstract void removeText(CharSequence source, int start, int end);

    /**
     * 绘制span元素
     * @param canvas
     * @param replacementSpan
     * @param text
     * @param start
     * @param end
     * @param x
     * @param top
     * @param y
     * @param bottom
     * @param textPaint
     */
    public abstract void drawReplacementSpan(Canvas canvas, ReplacementSpan replacementSpan,CharSequence text, int start, int end, float x,
                                             int top, int y, int bottom,TextPaint textPaint);

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
    public abstract void drawText(Canvas canvas,CharSequence text, int start, int end,float x,float y, TextPaint textPaint);

    /**
     * 绘制其他元素
     * @param canvas
     */
    public void onDraw(Canvas canvas){
    }

}
