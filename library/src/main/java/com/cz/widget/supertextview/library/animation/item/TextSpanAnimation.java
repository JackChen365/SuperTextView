package com.cz.widget.supertextview.library.animation.item;

import android.graphics.Canvas;
import android.text.TextPaint;
import android.view.View;

import com.cz.widget.supertextview.library.span.ViewSpan;
import com.cz.widget.supertextview.library.style.ReplacementSpan;

public class TextSpanAnimation extends BaseTextAnimation {
    /**
     * 当前文本
     */
    private ReplacementSpan replacementSpan;

    public void setReplacementSpan(ReplacementSpan replacementSpan) {
        this.replacementSpan = replacementSpan;
    }

    public ReplacementSpan getReplacementSpan() {
        return replacementSpan;
    }

    @Override
    public void onDraw(Canvas canvas, float x, float y, TextPaint textPaint) {
        super.onDraw(canvas, x, y, textPaint);
        if(null!=replacementSpan){
            if(replacementSpan instanceof ViewSpan){
                ViewSpan viewSpan = (ViewSpan) this.replacementSpan;
                View view = viewSpan.getView();
                float alpha = getAlpha();
                view.setAlpha(alpha);
            } else {
                CharSequence source = getSource();
                int start = getStart();
                int end = getEnd();
                replacementSpan.draw(canvas,source,start,end,x,y,textPaint);
            }
        }
    }
}
