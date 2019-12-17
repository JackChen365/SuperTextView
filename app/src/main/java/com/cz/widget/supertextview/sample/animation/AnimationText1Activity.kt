package com.cz.widget.supertextview.sample.animation

import android.os.Bundle
import com.cz.widget.supertextview.library.animation.AbsAnimationTextRender
import com.cz.widget.supertextview.library.animation.DefaultAnimationTextRender
import com.cz.widget.supertextview.library.spannable.SpannableString
import com.cz.widget.supertextview.sample.R
import com.okay.sampletamplate.ToolBarActivity
import kotlinx.android.synthetic.main.activity_animation_text1.*

class AnimationText1Activity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation_text1)

        val text=assets.open("lyric").bufferedReader().readText()
        val spannableString = SpannableString(text)
        textLayout.setTextRender(DefaultAnimationTextRender())
        textLayout.setText(spannableString)
    }
}
