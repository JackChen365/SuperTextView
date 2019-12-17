package com.cz.widget.supertextview.library.animation;

import android.animation.Animator;
import android.graphics.RectF;
import android.graphics.Region;

import com.cz.widget.supertextview.library.animation.AnimationLetter;

/**
 * @author Created by cz
 * @date 2019-05-15 16:21
 * @email bingo110@126.com
 * 文本元素动画接口
 * @see AnimationLetter
 */
public interface ITextAnimator {
    /**
     * 文本进入动画
     * @param animationLetter 操作对象
     * @return {@link Animator}
     * @see AnimationLetter#setAlpha(int) 渐变值
     * @see AnimationLetter#setX(float) x值
     * @see AnimationLetter#setY(float) y值
     * @see AnimationLetter#setTranslationX(float) 横向平移距离
     * @see AnimationLetter#setTranslationY(float) 纵向平移距离
     * @see AnimationLetter#setRotate(float) 旋转值
     * @see AnimationLetter#setClipRect(RectF, Region.Op)
     */
    Animator getEnterAnimator(AnimationLetter  animationLetter);

    /**
     * 文本退出动画
     * @param animationLetter 操作对象
     * @return {@link Animator}
     * @see AnimationLetter#setAlpha(int) 渐变值
     * @see AnimationLetter#setX(float) x值
     * @see AnimationLetter#setY(float) y值
     * @see AnimationLetter#setTranslationX(float) 横向平移距离
     * @see AnimationLetter#setTranslationY(float) 纵向平移距离
     * @see AnimationLetter#setRotate(float) 旋转值
     * @see AnimationLetter#setClipRect(RectF, Region.Op)
     */
    Animator getExitAnimator(AnimationLetter  animationLetter);
}
