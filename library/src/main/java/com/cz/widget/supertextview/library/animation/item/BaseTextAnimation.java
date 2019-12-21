package com.cz.widget.supertextview.library.animation.item;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;

import androidx.annotation.FloatRange;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by cz
 * @date 2019-05-15 15:51
 * 文本动画元素信息封装
 */
public class BaseTextAnimation {
    private static final String TAG="BaseTextAnimation";
    /**
     * 关联控件对象
     */
    private View target;
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
    private float alpha=1f;
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
     * 关联本对象的动画对象
     */
    protected ObjectAnimator objectAnimator;

    protected BaseTextAnimation() {
    }

    /**
     * 绘缓存的元素信息
     */
    public static final Map<Class,BaseTextAnimation[]> LetterCacheArray=new HashMap<>();
    /**
     * 获取一个直接使用的textLine对象
     * @return
     */
    protected static<T extends BaseTextAnimation> T obtain(Class<T> clazz) {
        T tl;
        synchronized (LetterCacheArray) {
            BaseTextAnimation[] baseTextAnimations = LetterCacheArray.get(clazz);
            if(null==baseTextAnimations){
                baseTextAnimations=new BaseTextAnimation[5];
                LetterCacheArray.put(clazz,baseTextAnimations);
            }
            for (int i = baseTextAnimations.length; --i >= 0;) {
                if (baseTextAnimations[i] != null) {
                    tl = (T) baseTextAnimations[i];
                    baseTextAnimations[i] = null;
                    return tl;
                }
            }
        }
        T textAnimation=null;
        try {
            textAnimation = clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return textAnimation;
    }
    public static<T extends BaseTextAnimation> T ofFloat(Class<T> clazz,String propertyName,float... values) {
        final T anim = obtain(clazz);
        anim.objectAnimator=ObjectAnimator.ofFloat(anim,propertyName,values);
        anim.objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                anim.invalidate();
            }
        });
        return anim;
    }

    public static<T extends BaseTextAnimation> T ofInt(Class<T> clazz,String propertyName, int... values) {
        final T anim = obtain(clazz);
        anim.objectAnimator=ObjectAnimator.ofInt(anim,propertyName,values);
        anim.objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                anim.invalidate();
            }
        });
        return anim;
    }

    public static<T extends BaseTextAnimation> T ofPropertyValuesHolder(Class<T> clazz,PropertyValuesHolder... values){
        final T anim = obtain(clazz);
        anim.objectAnimator=ObjectAnimator.ofPropertyValuesHolder(anim,values);
        anim.objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                anim.invalidate();
            }
        });
        return anim;
    }


    /**
     * 回收此对象
     */
    public void recycler() {
        color = 0;
        alpha = 1f;
        rotate = 0;
        translationX=0;
        translationY=0;
        clipRect=null;
        bounds.setEmpty();
        op=null;
        target=null;
        cancel();
        //回收此对象
        Class<? extends BaseTextAnimation> clazz = getClass();
        synchronized(LetterCacheArray) {
            BaseTextAnimation[] baseTextAnimations = LetterCacheArray.get(clazz);
            for (int i = 0; i < baseTextAnimations.length; ++i) {
                if (baseTextAnimations[i] == null) {
                    baseTextAnimations[i] = this;
                    break;
                }
            }
        }
    }

    public void setTarget(View target) {
        this.target = target;
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

    public void setColor(int color) {
        this.color = color;
    }
    public int getColor() {
        return color;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(@FloatRange(from = 0f,to=1f) float alpha) {
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
    }

    public float getRotate() {
        return rotate;
    }

    public void setRotate(@FloatRange(from = 0,to=360) float rotate) {
        this.rotate = rotate;
    }

    public CharSequence getSource() {
        return source;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    //设置动画相关属性
    public void setDuration(long duration){
        objectAnimator.setDuration(duration);
    }

    public void setInterpolator(TimeInterpolator value) {
        objectAnimator.setInterpolator(value);
    }

    public void setRepeatCount(int value) {
        objectAnimator.setRepeatCount(value);
    }

    public void setRepeatMode(int value) {
        objectAnimator.setRepeatMode(value);
    }

    public void start(){
        objectAnimator.start();
    }

    public void cancel(){
        if(null!=objectAnimator){
            objectAnimator.removeAllUpdateListeners();
            objectAnimator.removeAllListeners();
            objectAnimator.cancel();
            objectAnimator=null;
        }
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
     * 刷新视图
     */
    public void invalidate(){
        if(null!=target){
            target.invalidate();
        }
    }
    /**
     * 重置动画属性
     */
    public void reset(){
        op=null;
        clipRect=null;
        rotate=0;
        alpha=1f;
        translationX=0;
        translationY=0;
    }

    /**
     * 绘制信息
     * @param canvas
     */
    public void draw(Canvas canvas,TextPaint textPaint, TextPaint workPaint) {
        //开始绘图
        canvas.save();
        //旋转
        canvas.rotate(rotate);
        //渐变
        workPaint.setAlpha((int) (alpha*0xFF));
        //剪切指定矩阵
        if(null!=clipRect&&null!=op){
            canvas.clipRect(clipRect, op);
        }
        //绘制元素
        onDraw(canvas,translationX+bounds.left,translationY+bounds.top,workPaint);
        canvas.restore();
    }

    /**
     * 绘制元素
     * @param canvas
     * @param textPaint
     */
    public void onDraw(Canvas canvas,float x,float y, TextPaint textPaint){
    }
}
