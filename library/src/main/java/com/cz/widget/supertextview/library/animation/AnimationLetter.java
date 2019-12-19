package com.cz.widget.supertextview.library.animation;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.text.TextPaint;
import android.util.Log;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;

/**
 * @author Created by cz
 * @date 2019-05-15 15:51
 * 文本动画元素信息封装
 */
public class AnimationLetter {
    /**
     * 绘缓存的元素信息
     */
    public static final AnimationLetter[] LetterCacheArray=new AnimationLetter[5];
    /**
     * 当前文本
     */
    private CharSequence source;
    /**
     * 文本起始
     */
    private int start;
    /**
     * 文本结束
     */
    private int end;
    /**
     * 测试当前元素颜色
     */
    private int color;
    /**
     * 文字透明度
     */
    private int alpha=0xFF;
    /**
     * 平移x轴位置
     */
    private float translationX;
    /**
     * 平移y轴位置
     */
    private float translationY;
    /**
     * 旋转方向0~360
     */
    private float rotate=0f;
    /**
     * 指定的裁切区域
     */
    private RectF clipRect;
    /**
     * 裁切模式
     */
    private Region.Op op;
    /**
     * 当前文本元素测试矩阵
     */
    private final RectF bounds=new RectF();
    /**
     * 动画状态
     */
    private AnimationTextState animationTextState = AnimationTextState.PRE_ANIMATION;

    /**
     * 获取一个直接使用的textLine对象
     * @return
     */
    public static AnimationLetter obtain() {
        AnimationLetter tl;
        synchronized (LetterCacheArray) {
            for (int i = LetterCacheArray.length; --i >= 0;) {
                if (LetterCacheArray[i] != null) {
                    tl = LetterCacheArray[i];
                    LetterCacheArray[i] = null;
                    return tl;
                }
            }
        }
        tl = new AnimationLetter();
        return tl;
    }

    /**
     * 回收一个TextLetter对象
     * @param tl
     * @return
     */
    public static void recycle(AnimationLetter tl) {
        tl.recycler();
    }

    public void recycler() {
        color = 0;
        alpha = 0xFF;
        rotate = 0;
        translationX=0;
        translationY=0;
        clipRect=null;
        bounds.setEmpty();
        op=null;
        animationTextState=AnimationTextState.PRE_ANIMATION;
        synchronized(LetterCacheArray) {
            for (int i = 0; i < LetterCacheArray.length; ++i) {
                if (LetterCacheArray[i] == null) {
                    LetterCacheArray[i] = this;
                    break;
                }
            }
        }
    }

    /**
     * 设置文本
     * @param source
     * @param start
     * @param end
     */
    public void setText(CharSequence source, int start, int end) {
        this.source=source;
        this.start=start;
        this.end=end;
    }

    public char getLatter(){
        return this.source.charAt(start);
    }

    public void setColor(int color) {
        this.color = color;
    }
    public int getColor() {
        return color;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(@IntRange(from = 0,to=0xff) int alpha) {
        this.alpha = alpha;
    }

    public float getX() {
        return translationX+bounds.left;
    }

    public void setX(float x) {
        //x轴位置使用平移计算
        bounds.offsetTo(x,bounds.top);
    }

    public float getY() {
        return translationY+bounds.top;
    }

    public void setY(float y) {
        //y轴位置使用平移计算
        bounds.offsetTo(bounds.left,y);
    }

    /**
     * 设置展示区域
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setBounds(float left,float top,float right,float bottom){
        this.bounds.set(left,top,right,bottom);
    }

    public float getTranslationX() {
        return translationX;
    }

    public void setTranslationX(float translationX) {
        this.translationX = translationX;
    }

    public float getTranslationY() {
        return translationY;
    }

    public void setTranslationY(float translationY) {
        this.translationY = translationY;
        Log.e("setTranslationY","left:"+hashCode()+" "+(translationX+bounds.left)+" top:"+(this.translationY+bounds.top));
    }

    public float getRotate() {
        return rotate;
    }

    public void setRotate(@FloatRange(from = 0,to=360) float rotate) {
        this.rotate = rotate;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    /**
     * 设置动画状态
     * @param animationTextState
     */
    public void setAnimationTextState(AnimationTextState animationTextState) {
        this.animationTextState = animationTextState;
    }

    public AnimationTextState getAnimationTextState() {
        return animationTextState;
    }

    /**
     * 设定指定裁切区域
     * @param clipRect
     * @see Region.Op#INTERSECT
     * @see Region.Op#DIFFERENCE
     */
    public void setClipRect(RectF clipRect,Region.Op op) {
        this.clipRect = clipRect;
        this.op=op;
    }
    public RectF getBounds() {
        return bounds;
    }


    /**
     * 重置动画属性
     */
    public void reset(){
        op=null;
        clipRect=null;
        rotate=0;
        alpha=0xff;
        translationX=0;
        translationY=0;
    }

    /**
     * 绘制信息
     * @param canvas
     */
    public void draw(Canvas canvas, TextPaint textPaint) {
        //开始绘图
        canvas.save();
        //旋转
        canvas.rotate(rotate);
        //渐变
        textPaint.setAlpha(alpha);
        //剪切指定矩阵
        if(null!=clipRect&&null!=op){
            canvas.clipRect(clipRect, op);
        }
        //绘制文本
        canvas.drawText(source,start,end,translationX+bounds.left,translationY+bounds.top,textPaint);
        canvas.restore();
    }
}
