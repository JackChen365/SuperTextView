package com.cz.widget.supertextview.sample.animation.render

import android.animation.*
import android.graphics.*

import com.cz.widget.supertextview.library.animation.AbsAnimationTextRender
import com.cz.widget.supertextview.library.animation.AnimationLetter
import com.cz.widget.supertextview.sample.animation.render.drawable.BallDrawable

/**
 * @author :Created by cz
 * @date 2019-05-16 13:54
 * @email bingo110@126.com
 * 一个球形加载效果
 */
class BallLoadTextController : AbsAnimationTextRender() {
    /**
     * 附加drawable对象
     */
    private val ballDrawable = BallDrawable()
    /**
     * 动画更新重绘监听对象
     */
    private val updateListener = ValueAnimator.AnimatorUpdateListener { invalidate() }

    init {
        ballDrawable.setColor(Color.WHITE)
        ballDrawable.setBounds(0, 0, 8, 8)
    }

    override fun getEnterAnimator(animationLetters: List<AnimationLetter>): Animator {
        var lastAnimator: Animator? = null
        val animatorSet = AnimatorSet()
        var lastElement: AnimationLetter? = null
        val findAnimationLetterList = animationLetters.filter { it.latter != ' ' }
        val leftPadding = 0
        val topPadding = 0
        for (animationLetter in findAnimationLetterList) {
            val bounds = animationLetter.bounds
            val offsetX = lastElement?.bounds?.centerX() ?: bounds.left - bounds.width() / 2
            val rectF = RectF(
                leftPadding + offsetX,
                topPadding + bounds.top - bounds.height(),
                leftPadding + bounds.centerX(),
                topPadding + bounds.top
            )
            val arcAnimator = getArcAnimator(rectF)
            //第二个动画
            val animator1 = ObjectAnimator.ofFloat(animationLetter, "translationY", 10f)
            val animator2 = ObjectAnimator.ofFloat(animationLetter, "translationY", 0f)
            animator1.addUpdateListener(updateListener)
            animator2.addUpdateListener(updateListener)
            animatorSet.playSequentially(arcAnimator, animator1, animator2)
            //组合动画
            if (null != lastAnimator) {
                animatorSet.playSequentially(lastAnimator, arcAnimator)
            }
            lastElement = animationLetter
            lastAnimator = arcAnimator
        }
        //最后一个
        if (!animationLetters.isEmpty()) {
            //重新播放
            animatorSet.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    animatorSet.start()
                }
            })
        }
        return animatorSet
    }

    override fun getExitAnimator(animationLetters: List<AnimationLetter>): Animator? {
        return null
    }

    override fun startExitAnimator() {}

    override fun onDraw(canvas: Canvas) {
        //测试测试信息
        debugDraw(canvas)
        // 绘白色小球
        ballDrawable.draw(canvas)
    }

    /**
     * 测试绘制信息
     * @param canvas
     */
    private fun debugDraw(canvas: Canvas) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.RED
        paint.strokeWidth = 1f
        paint.style = Paint.Style.STROKE
        val findAnimationLetterList = animationLetterArray.filter { it.latter != ' ' }
        for (animationLetter in findAnimationLetterList) {
            val bounds = animationLetter.bounds
            canvas.drawRect(bounds.left,bounds.top,bounds.right,bounds.bottom, paint)
        }
    }

    /**
     * 获得一个弧形动画对象
     * @param rectF
     * @return
     */
    private fun getArcAnimator(rectF: RectF): ArcAnimator {
        val arcAnimator = ArcAnimator(rectF, 180f, 360f)
        arcAnimator.duration = 600
        arcAnimator.addUpdateListener {
            ballDrawable.setX((arcAnimator.x - ballDrawable.bounds.width() / 2).toInt())
            ballDrawable.setY(arcAnimator.y.toInt())
            invalidate()
        }
        return arcAnimator
    }

    inner class ArcAnimator(rect: RectF, private val start: Float, private val end: Float) :
        ValueAnimator() {
        private val path = Path()
        private val pathMeasure = PathMeasure()
        private val pos = FloatArray(2)
        private val tan = FloatArray(2)

        val x: Float
            get() = pos[0]

        val y: Float
            get() = pos[1]

        init {
            setFloatValues(1f)
            addUpdateListener { animation -> setFraction(animation.animatedFraction) }
            path.addOval(rect, Path.Direction.CW)
            pathMeasure.setPath(path, false)
        }

        private fun setFraction(fraction: Float) {
            //当前行走进度
            val startFraction = start / 360f
            val endFraction = end / 360f
            val fractionValue = startFraction + (endFraction - startFraction) * fraction
            val distance = pathMeasure.length * fractionValue
            pathMeasure.getPosTan(distance, pos, tan)
            //设置旋转角度
            val degrees = (Math.atan2(tan[1] * 1.0, tan[0] * 1.0) * 180 / Math.PI).toFloat()
        }
    }
}
