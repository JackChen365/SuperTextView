package com.cz.widget.supertextview.library.animation.impl;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.graphics.Region;

import com.cz.widget.supertextview.library.animation.AnimationLetter;
import com.cz.widget.supertextview.library.animation.ITextAnimator;

/**
 * @author Created by cz
 * @date 2019-05-15 16:53
 * @email bingo110@126.com
 */
public class YTextAnimator implements ITextAnimator {
    @Override
    public Animator getEnterAnimator(AnimationLetter animationLetter) {
        Rect bounds = animationLetter.getBounds();
        //给定一个矩阵,记录起始位置
        Rect rect=new Rect();
        rect.set(bounds.left,-bounds.height(),bounds.right,0);
        animationLetter.setY(-bounds.height());
        //裁切此区域,确保在最初text不可见
        animationLetter.setClipRect(rect, Region.Op.DIFFERENCE);
        ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(animationLetter,"y",-bounds.height(), bounds.top);
        return valueAnimator;
    }

    @Override
    public Animator getExitAnimator(AnimationLetter animationLetter) {
        Rect bounds = animationLetter.getBounds();
        //给定一个矩阵,记录起始位置
        Rect rect=new Rect();
        rect.set(bounds.left,-bounds.height(),bounds.right,0);
        //裁切此区域,确保在最初text不可见
        animationLetter.setClipRect(rect, Region.Op.DIFFERENCE);
        ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(animationLetter,"y",-bounds.height());
        return valueAnimator;
    }
}
