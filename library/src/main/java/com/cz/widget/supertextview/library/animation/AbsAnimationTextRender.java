package com.cz.widget.supertextview.library.animation;

import android.animation.Animator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;

import com.cz.widget.supertextview.library.render.TextRender;
import com.cz.widget.supertextview.library.style.ReplacementSpan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 动画的文本渲染器，用于扩展文本动画
 */
public abstract class AbsAnimationTextRender extends TextRender {
    private static final String TAG="AbsAnimationTextRender";
    /**
     * 当前文本绘制区域
     */
    private final Rect bounds=new Rect();
    /**
     * 动画字符集
     */
    private final ArrayList<AnimationLetter> animationLetterList =new ArrayList<>();
    /**
     * 当前执行动画对象
     */
    private Animator currentAnimator;
    /**
     * 动画集
     */
    private ITextAnimator textAnimator;
    /**
     * 动画执行状态
     */
    private AnimationTextState animationTextState=AnimationTextState.PRE_ANIMATION;

    @Override
    public void addText(CharSequence text, int start, int end, float x, float y, TextPaint textPaint) {
        //添加新的字符元素
        Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
        int textHeight= fontMetricsInt.descent-fontMetricsInt.ascent;
        for(int i=start;i<end;i++){
            AnimationLetter animationLetter = AnimationLetter.obtain();
            animationLetter.setText(text,i,i+1);
            float textWidth = textPaint.measureText(text, i, i + 1);
            animationLetter.setBounds(Math.round(x),Math.round(y), Math.round(x+textWidth),Math.round(y+textHeight));
            animationLetterList.add(animationLetter);
            x+=textWidth;
        }
    }

    @Override
    public void removeText(CharSequence source, int start, int end) {
        //缩小范围元素
        List<AnimationLetter> animationLetterList = this.animationLetterList;
        //移除范围内字符
        for(Iterator<AnimationLetter> iterator = animationLetterList.iterator(); iterator.hasNext();){
            AnimationLetter animationLetter = iterator.next();
            int latterStart = animationLetter.getStart();
            int latterEnd = animationLetter.getEnd();
            if(latterStart>=start&&latterEnd<=end){
                animationLetter.recycler();
                iterator.remove();
            }
        }
    }

    @Override
    public void drawReplacementSpan(Canvas canvas, ReplacementSpan replacementSpan, CharSequence text, int start, int end, float x, int top, int y, int bottom, TextPaint textPaint) {
    }

    @Override
    public void drawText(Canvas canvas, CharSequence text, int start, int end, float x, float y, TextPaint textPaint) {
        //更新字符动画
        for(AnimationLetter animationLetter: animationLetterList){
            int latterStart = animationLetter.getStart();
            int latterEnd = animationLetter.getEnd();
            if(latterStart>=start&&latterEnd<=end){
                animationLetter.setY(y);
                animationLetter.draw(canvas,textPaint);
            }
        }
    }

    /**
     * 设置元素动画
     * @param textAnimator
     */
    public void setTextAnimator(ITextAnimator textAnimator){
        this.textAnimator=textAnimator;
    }

    public int getLeftPadding(){
        View target = getTarget();
        return target.getPaddingLeft();
    }

    public int getTopPadding(){
        View target = getTarget();
        return target.getPaddingTop();
    }

    /**
     * 获得文本动画对象
     * @return
     */
    protected ITextAnimator getTextAnimator(){
        return textAnimator;
    }

    public ArrayList<AnimationLetter> getAnimationLetterArray() {
        return animationLetterList;
    }

    /**
     * 根据元素集,获得一个进入的动画体
     * @param animationLetters
     * @return
     */
    protected abstract Animator getEnterAnimator(List<AnimationLetter> animationLetters);
    /**
     * 初始启动进入动画
     */
    public void startEnterAnimator(){
        //清理动画
        cleanAnimator();
        //获取进入动画对象
        List<AnimationLetter> animationLetterList = this.animationLetterList;
        if(!animationLetterList.isEmpty()){
            Animator enterAnimator = getEnterAnimator(animationLetterList);
            if(null!=enterAnimator){
                //记录此动画
                currentAnimator=enterAnimator;
                animationTextState=AnimationTextState.RUNNING;
                //启动动画
                enterAnimator.start();
            }
        }
    }

    /**
     * 根据元素集,获得一个进入的动画体
     * @param animationLetters
     * @return
     */
    protected abstract Animator getExitAnimator(List<AnimationLetter> animationLetters);

    /**
     * 初始启动退出动画
     */
    public void startExitAnimator(){
        List<AnimationLetter> animationLetterList = this.animationLetterList;
        if(!animationLetterList.isEmpty()){
            Animator exitAnimator = getExitAnimator(animationLetterList);
            if(null!=exitAnimator){
                //记录此动画
                currentAnimator=exitAnimator;
                animationTextState=AnimationTextState.RUNNING;
                //启动动画
                exitAnimator.start();
            }
        }
    }

    /**
     * 清空文本动画,用于在主动结束动画后作一些清空操作
     * @return
     */
    public void cleanAnimator(){
        //还原元素
        List<AnimationLetter> animationLetterList = this.animationLetterList;
        for(int i=0;i<animationLetterList.size();i++) {
            AnimationLetter elementSpan = animationLetterList.get(i);
            elementSpan.reset();
        }
        //清理动画
        if(null!=currentAnimator){
            currentAnimator.removeAllListeners();
            currentAnimator.cancel();
            currentAnimator=null;
        }
        //状态停止
        animationTextState=AnimationTextState.STOP;
        //重绘
        invalidate();
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
