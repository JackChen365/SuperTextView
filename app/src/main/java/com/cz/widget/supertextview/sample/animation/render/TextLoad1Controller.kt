package com.cz.widget.supertextview.sample.animation.render

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import com.cz.widget.supertextview.library.animation.AbsAnimationTextRender
import com.cz.widget.supertextview.library.animation.AnimationLetter

/**
 * @author Created by cz
 * @date 2019-05-16 15:49
 * @email bingo110@126.com
 * 扩展动画效果1
 */
class TextLoad1Controller: AbsAnimationTextRender() {
    override fun getEnterAnimator(animationLetters:List<AnimationLetter>): Animator {
        val animatorSet=AnimatorSet()
        animationLetters.filter { it.latter=='.' }.forEachIndexed { index, latter ->
            val animator = ObjectAnimator.ofFloat(latter,"translationY",0f,4f)
            animator.duration=600
            animator.startDelay=index*200L
            animator.repeatCount=-1
            animator.repeatMode= ValueAnimator.REVERSE
            animator.addUpdateListener { invalidate() }
            animatorSet.playTogether(animator)
        }
        return animatorSet
    }

    override fun getExitAnimator(animationLetters:List<AnimationLetter>): Animator? {
        return null
    }
}