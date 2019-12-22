package com.cz.widget.supertextview.library.animation;

import android.animation.PropertyValuesHolder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.SparseArray;
import android.view.View;

import com.cz.widget.supertextview.library.animation.item.BaseTextAnimation;
import com.cz.widget.supertextview.library.animation.item.TextLineAnimation;
import com.cz.widget.supertextview.library.animation.item.TextSpanAnimation;
import com.cz.widget.supertextview.library.render.TextRender;
import com.cz.widget.supertextview.library.style.ReplacementSpan;

/**
 * 动画的文本渲染器，用于扩展文本动画
 */
public class SimpleTextAnimation extends TextRender {
    /**
     * 当前文本绘制区域
     */
    private final Rect bounds=new Rect();
    /**
     * 动画字符集
     */
    private final SparseArray<BaseTextAnimation> animationItemArray =new SparseArray<>();

    @Override
    public void addText(CharSequence text, int start, int end, float x, float y, float textWidth,TextPaint textPaint) {
        //添加新的字符元素
        Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
        PropertyValuesHolder alphaValueHolder = PropertyValuesHolder.ofFloat("alpha", 0f,1f);
        PropertyValuesHolder xValueHolder = PropertyValuesHolder.ofFloat("x",-textWidth,x);
        BaseTextAnimation textAnimation = TextSpanAnimation.ofPropertyValuesHolder(TextLineAnimation.class,alphaValueHolder,xValueHolder);
        animationItemArray.put(start,textAnimation);
        View target = getTarget();
        textAnimation.setTarget(target);
//        textAnimation.setDuration(600);
        textAnimation.setText(text,start,end);
        int textHeight= fontMetricsInt.descent-fontMetricsInt.ascent;
        textAnimation.setBounds(Math.round(x),Math.round(y), Math.round(x+textWidth),Math.round(y+textHeight));
        textAnimation.start();
    }

    @Override
    public void removeText(CharSequence source, int start, int end) {
        BaseTextAnimation textAnimation = animationItemArray.get(start);
        if(null!=textAnimation){
            textAnimation.recycler();
            animationItemArray.remove(start);
        }
    }

    @Override
    public void addReplacementSpan(ReplacementSpan span,Paint.FontMetricsInt fontMetricsInt,CharSequence text, int start, int end, float x, float y,float width) {
        //添加新的字符元素
        PropertyValuesHolder alphaValueHolder = PropertyValuesHolder.ofFloat("alpha", 0f,1f);
        PropertyValuesHolder translationXValueHolder = PropertyValuesHolder.ofFloat("translationX",-x,0);
        TextSpanAnimation textAnimation = TextSpanAnimation.ofPropertyValuesHolder(TextSpanAnimation.class,alphaValueHolder,translationXValueHolder);
        animationItemArray.put(start,textAnimation);
        textAnimation.setReplacementSpan(span);
        View target = getTarget();
        textAnimation.setTarget(target);
//        textAnimation.setDuration(600);
        textAnimation.setText(text,start,end);
        int textHeight= fontMetricsInt.descent-fontMetricsInt.ascent;
        textAnimation.setBounds(Math.round(x),Math.round(y), Math.round(x+width),Math.round(y+textHeight));
        textAnimation.start();
    }

    @Override
    public void removeSpan(ReplacementSpan span,CharSequence source, int start, int end) {
        BaseTextAnimation textAnimation = animationItemArray.get(start);
        if(null!=textAnimation){
            textAnimation.recycler();
            animationItemArray.remove(start);
        }
    }

    @Override
    public void drawReplacementSpan(Canvas canvas, TextPaint textPaint, TextPaint workPaint,ReplacementSpan replacementSpan, CharSequence text, int start, int end, float x, float y,int top,int bottom) {
        BaseTextAnimation baseTextAnimation = animationItemArray.get(start);
        if(null!= baseTextAnimation){
            baseTextAnimation.draw(canvas,workPaint,text,start,end,x,y,top,bottom);
        }
    }

    @Override
    public void drawText(Canvas canvas,TextPaint textPaint,CharSequence text, int start, int end, float x, float y,int top,int bottom) {
        //更新字符动画
        BaseTextAnimation baseTextAnimation = animationItemArray.get(start);
        if(null!= baseTextAnimation){
            baseTextAnimation.setY(y);
            baseTextAnimation.draw(canvas,textPaint,text,start,end,x,y,top,bottom);
        }
    }

    public int getLeftPadding(){
        View target = getTarget();
        return target.getPaddingLeft();
    }

    public int getTopPadding(){
        View target = getTarget();
        return target.getPaddingTop();
    }

    public int getRightPadding(){
        View target = getTarget();
        return target.getPaddingRight();
    }

    public int getBottomPadding(){
        View target = getTarget();
        return target.getPaddingBottom();
    }

    public int getWidth(){
        View target = getTarget();
        return target.getWidth();
    }

    public int getHeight(){
        View target = getTarget();
        return target.getHeight();
    }

    /**
     * 获得当前layout绘制区域
     * @return Rect 此区域为绘制文本的总区域
     */
    protected Rect getLayoutBounds(){
        View target = getTarget();
        int totalPaddingLeft = target.getPaddingLeft();
        int totalPaddingTop = target.getPaddingTop();
        int totalPaddingRight = target.getPaddingRight();
        int totalPaddingBottom = target.getPaddingBottom();
        int width = target.getWidth();
        int height = target.getHeight();
        bounds.set(totalPaddingLeft,totalPaddingTop,width-totalPaddingRight,height-totalPaddingBottom);
        return bounds;
    }

}
