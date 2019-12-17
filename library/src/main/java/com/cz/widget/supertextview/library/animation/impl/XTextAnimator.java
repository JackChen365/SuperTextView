package com.cz.widget.supertextview.library.animation.impl;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Rect;

import com.cz.widget.supertextview.library.animation.AnimationLetter;
import com.cz.widget.supertextview.library.animation.ITextAnimator;

/**
 * @author Created by cz
 * @date 2019-05-15 16:53
 * @email bingo110@126.com
 */
public class XTextAnimator implements ITextAnimator {
    @Override
    public Animator getEnterAnimator(AnimationLetter animationLetter) {
        Rect bounds = animationLetter.getBounds();
        animationLetter.setX(-bounds.width());
        ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(animationLetter,"x",-bounds.width(), bounds.left);
        return valueAnimator;
    }

    @Override
    public Animator getExitAnimator(AnimationLetter animationLetter) {
        Rect bounds = animationLetter.getBounds();
        ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(animationLetter,"x",-bounds.width());
        return valueAnimator;
    }
}
