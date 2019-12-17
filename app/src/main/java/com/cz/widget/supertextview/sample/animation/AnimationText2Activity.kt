package com.cz.widget.supertextview.sample.animation

import android.os.Bundle
import com.cz.widget.supertextview.library.animation.AbsAnimationTextRender
import com.cz.widget.supertextview.library.spannable.SpannableString

import com.cz.widget.supertextview.sample.R
import com.cz.widget.supertextview.sample.animation.render.BallLoadTextController
import com.cz.widget.supertextview.sample.animation.render.TextLoad1Controller
import com.cz.widget.supertextview.sample.animation.render.TextLoad2Controller
import com.cz.widget.supertextview.sample.animation.render.WindowsTextLoadController
import com.okay.sampletamplate.ToolBarActivity
import kotlinx.android.synthetic.main.activity_animation_text2.*

/**
 * 演示扩展文字控制器,展示一系列基于文本span的动画扩展
 * @see com.cz.widget.supertextview.library.animation.AbsAnimationTextRender 扩展基础类
 * @see com.cz.widget.supertextview.sample.animation.render.BallLoadTextController 小球点击登陆效果
 * @see com.cz.widget.supertextview.sample.animation.render.TextLoad1Controller 仿windows文本加载效果
 * @see com.cz.widget.supertextview.sample.animation.render.TextLoad2Controller 加载中... .号波动效果
 * @see com.cz.widget.supertextview.sample.animation.render.WindowsTextLoadController 加载中... .号渐变效果
 */
class AnimationText2Activity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation_text2)


        //设置不同文本控件器
        textView1.textRender = BallLoadTextController()
        textView2.textRender = WindowsTextLoadController()
        textView3.textRender = TextLoad1Controller()
        textView4.textRender = TextLoad2Controller()

        textView1.text=SpannableString(getString(R.string.login))
        textView2.text=SpannableString(getString(R.string.loading))
        textView3.text=SpannableString(getString(R.string.loading))
        textView4.text=SpannableString(getString(R.string.loading))

        textView1.setOnClickListener {
            val textRender = textView1.textRender as AbsAnimationTextRender
            textRender.startEnterAnimator()
        }
        textView2.setOnClickListener {
            val textRender = textView1.textRender as AbsAnimationTextRender
            textRender.startEnterAnimator()
        }
        textView3.setOnClickListener {
            val textRender = textView1.textRender as AbsAnimationTextRender
            textRender.startEnterAnimator()
        }
        textView4.setOnClickListener {
            val textRender = textView1.textRender as AbsAnimationTextRender
            textRender.startEnterAnimator()
        }
        //自动启动所有动画
        val textRender = textView1.textRender as AbsAnimationTextRender
        textRender.startEnterAnimator()
        //启动动画
        startButton.setOnClickListener {
            val textRender1 = textView1.textRender as AbsAnimationTextRender
            val textRender2 = textView2.textRender as AbsAnimationTextRender
            val textRender3 = textView3.textRender as AbsAnimationTextRender
            val textRender4 = textView4.textRender as AbsAnimationTextRender
            textRender1.startEnterAnimator()
            textRender2.startEnterAnimator()
            textRender3.startEnterAnimator()
            textRender4.startEnterAnimator()
        }
        //停止动画
        stopButton.setOnClickListener {
            val textRender1 = textView1.textRender as AbsAnimationTextRender
            val textRender2 = textView2.textRender as AbsAnimationTextRender
            val textRender3 = textView3.textRender as AbsAnimationTextRender
            val textRender4 = textView4.textRender as AbsAnimationTextRender
            textRender1.cleanAnimator()
            textRender2.cleanAnimator()
            textRender3.cleanAnimator()
            textRender4.cleanAnimator()
        }
    }
}
