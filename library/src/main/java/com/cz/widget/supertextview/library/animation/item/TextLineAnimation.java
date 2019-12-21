package com.cz.widget.supertextview.library.animation.item;

import android.graphics.Canvas;
import android.graphics.Color;
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

    @Override
    public void onDraw(Canvas canvas, float x, float y, TextPaint textPaint) {
        super.onDraw(canvas, x, y, textPaint);
        CharSequence source = getSource();
        int start = getStart();
        int end = getEnd();
        canvas.drawText(source,start,end,x,y,textPaint);
    }
}
