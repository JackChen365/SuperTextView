package com.cz.widget.supertextview.library.animation.impl;

import android.animation.Animator;
import android.animation.ObjectAnimator;

import com.cz.widget.supertextview.library.animation.AnimationLetter;
import com.cz.widget.supertextview.library.animation.ITextAnimator;

/**
 * @author Created by cz
 * @date 2019-05-15 16:50
 * @email bingo110@126.com
 */
public class AlphaTextAnimator implements ITextAnimator {

    @Override
    public Animator getEnterAnimator(AnimationLetter animationLetter) {
        //动画开始前,将一alpha属性置为0,然后再执行0->255
        animationLetter.setAlpha(0);
        ObjectAnimator valueAnimator = ObjectAnimator.ofInt(animationLetter,"alpha",0,0xFF);
        return valueAnimator;
    }

    @Override
    public Animator getExitAnimator(AnimationLetter animationLetter) {
        //动画开始前,将一alpha属性置为255,然后再执行255->0
        animationLetter.setAlpha(0xFF);
        ObjectAnimator valueAnimator = ObjectAnimator.ofInt(animationLetter,"alpha",0xFF,0);
        return valueAnimator;
    }
}
