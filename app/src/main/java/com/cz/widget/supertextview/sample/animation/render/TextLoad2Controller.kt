package com.cz.widget.supertextview.sample.animation.render

import android.animation.*
import com.cz.widget.supertextview.library.animation.AbsAnimationTextRender
import com.cz.widget.supertextview.library.animation.AnimationLetter

/**
 * @author Created by cz
 * @date 2019-05-16 15:49
 * @email bingo110@126.com
 * 扩展动画效果2
 */
class TextLoad2Controller: AbsAnimationTextRender() {

    override fun getEnterAnimator(animationLetters:List<AnimationLetter>): Animator {
        val animatorSet=AnimatorSet()
        animationLetters?.filter { it.latter=='.' }?.forEachIndexed { index, element ->
            val animator = ObjectAnimator.ofInt(element,"alpha",0x0,0xFF)
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