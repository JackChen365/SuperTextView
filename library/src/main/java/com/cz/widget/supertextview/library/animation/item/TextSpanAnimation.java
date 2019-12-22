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
    public void recycler() {
        super.recycler();
        replacementSpan=null;
    }

    @Override
    public void onDraw(Canvas canvas, TextPaint textPaint, CharSequence text, int start, int end, float x, float y, int top, int bottom) {
        super.onDraw(canvas, textPaint, text, start, end, x, y, top, bottom);
        if(null!=replacementSpan){
            if(replacementSpan instanceof ViewSpan){
                ViewSpan viewSpan = (ViewSpan) this.replacementSpan;
                View view = viewSpan.getView();
                float alpha = getAlpha();
                float translationX = getTranslationX();
                float translationY = getTranslationY();
                view.setAlpha(alpha);
                view.setTranslationX(translationX);
                view.setTranslationY(translationY);
            } else {
                CharSequence source = getSource();
                float offsetX = getX();
                float offsetY = getY();
                replacementSpan.draw(canvas,source,start,end,offsetX,offsetY,textPaint);
            }
        }
    }
}
