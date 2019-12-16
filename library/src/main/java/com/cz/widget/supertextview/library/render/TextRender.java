package com.cz.widget.supertextview.library.render;

import android.graphics.Canvas;
import android.text.TextPaint;

/**
 * 文本渲染器
 */
public abstract class TextRender {
    private Callback callback;

    public void setCallback(Callback callback){
        this.callback=callback;
    }

    /**
     * 绘制视图
     */
    void invalidate(){
        callback.invalidate();
    }

    public abstract void drawText(Canvas canvas, TextPaint textPaint,CharSequence text, int start, int end,float x,float y);
}
