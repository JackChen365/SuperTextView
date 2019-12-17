package com.cz.widget.supertextview.library.animation.impl;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

import com.cz.widget.supertextview.library.animation.AnimationLetter;
import com.cz.widget.supertextview.library.animation.ITextAnimator;

/**
 * @author Created by cz
 * @date 2019-05-15 16:53
 * @email bingo110@126.com
 */
public class TranslationXTextAnimator implements ITextAnimator {
    @Override
    public Animator getEnterAnimator(AnimationLetter animationLetter) {
        Rect bounds = animationLetter.getBounds();
        animationLetter.setTranslationX(-bounds.width());
        animationLetter.setClipRect(bounds, Region.Op.INTERSECT);
        ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(animationLetter,"translationX",0);
        valueAnimator.setDuration(1000);
        return valueAnimator;
    }

    @Override
    public Animator getExitAnimator(AnimationLetter animationLetter) {
        Rect bounds = animationLetter.getBounds();
        animationLetter.setClipRect(bounds, Region.Op.INTERSECT);
        ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(animationLetter,"translationX",bounds.width());
        return valueAnimator;
    }
}
