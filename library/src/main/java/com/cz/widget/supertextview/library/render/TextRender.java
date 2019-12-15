package com.cz.widget.supertextview.library.render;

import android.graphics.Canvas;
import android.text.TextPaint;

/**
 * 文本渲染器
 */
public abstract class TextRender {
    public abstract void drawText(Canvas canvas, TextPaint textPaint,CharSequence text, int start, int end,float x,float y);
}
