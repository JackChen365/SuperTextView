package com.cz.widget.supertextview.sample.animation.render

import android.animation.*
import android.graphics.Canvas
import android.view.animation.LinearInterpolator
import com.cz.widget.supertextview.library.animation.AbsAnimationTextRender
import com.cz.widget.supertextview.library.animation.AnimationLetter

/**
 * @author Created by cz
 * @date 2019-05-16 15:41
 * bingo110@126.com
 * 仿windows文字加载效果
 */
class WindowsTextLoadController: AbsAnimationTextRender() {

    override fun getEnterAnimator(animationLetters:List<AnimationLetter>?): Animator {
        val animatorSet=AnimatorSet()
        animationLetters?.forEachIndexed{ index,animationLetter->
            val leftPadding = 0
            val bounds = animationLetter.bounds
            val left=leftPadding+bounds.left
            val frame1= Keyframe.ofFloat(0f, (-bounds.width()).toFloat())
            val frame2= Keyframe.ofFloat(0.2f, (left-60).toFloat())
            val frame3= Keyframe.ofFloat(0.8f, (left+60).toFloat())
            val frame4= Keyframe.ofFloat(1.0f, (width+bounds.width()).toFloat())
            val animator = ObjectAnimator.ofPropertyValuesHolder(animationLetter,
                PropertyValuesHolder.ofKeyframe("x", frame1, frame2, frame3,frame4))
            animator.interpolator= LinearInterpolator()
            animator.duration=4000
            animator.startDelay=(animationLetters.size-index)*100L
            animator.repeatCount=-1
            animator.repeatMode= ValueAnimator.RESTART
            //重绘
            animator.addUpdateListener { invalidate() }
            animatorSet.playTogether(animator)
        }
        return animatorSet
    }

    override fun getExitAnimator(animationLetters:List<AnimationLetter>?): Animator? {
        return null
    }

    override fun onDraw(canvas: Canvas?) {
    }
}