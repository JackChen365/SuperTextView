package com.cz.widget.supertextview.sample.animation

import android.graphics.BlurMaskFilter
import android.graphics.Color
import android.graphics.EmbossMaskFilter
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.view.LayoutInflater
import android.widget.TextView
import com.cz.widget.supertextview.library.animation.SimpleTextAnimation
import com.cz.widget.supertextview.library.span.*
import com.cz.widget.supertextview.library.spannable.SpannableString
import com.cz.widget.supertextview.library.style.ReplacementSpan
import com.cz.widget.supertextview.sample.R
import com.cz.widget.supertextview.sample.data.Data
import com.cz.widget.supertextview.sample.linedecoration.HighlightLineDecoration
import com.cz.widget.supertextview.sample.template.ToolBarActivity
import kotlinx.android.synthetic.main.activity_animation_text1.*
import kotlin.random.Random

class AnimationText1Activity : ToolBarActivity() {

    companion object{
        private const val SPAN_COUNT=13
    }

    private var lineDecoration: HighlightLineDecoration?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation_text1)
        lineDecoration=HighlightLineDecoration(this)
        val text=assets.open("dynamic_chapter1").bufferedReader().readText()
        val spannableString=SpannableString(text)
//        初始化span
        var start=0
        val positionList= mutableListOf<Int>()
        val spanPositionList= mutableListOf<Int>()
        var index=0
        while(-1!=start){
            val i= Random.nextInt(SPAN_COUNT)
            index=text.indexOf(" ",start+1)
            if(-1!=index){
                spanPositionList.add(i)
                val textSpan = getTextSpan(i)
                if(null!=textSpan){
                    positionList.add(index)
                    if(textSpan is ReplacementSpan){
                        spannableString.setSpan(textSpan,index, Math.min(text.length,index+1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    } else {
                        spannableString.setSpan(textSpan, index, Math.min(text.length,index+5), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            }
            start=index
        }
        textLayout.clear()
        textLayout.setLineDecoration(lineDecoration)
        textLayout.textRender=SimpleTextAnimation()
        textLayout.text = spannableString

//        val text="Carre de l'Est Swis"
//        val spannableString=SpannableString(text)
//        val positionList= mutableListOf(14)
//        val spanPositionList= mutableListOf(2)
//        positionList.forEachIndexed { index, position->
//            var textSpan = getTextSpan(spanPositionList[index])
//            if(textSpan is ReplacementSpan){
//                spannableString.setSpan(textSpan, position, position+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//            } else {
//                spannableString.setSpan(textSpan, position, position+5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//            }
//        }
//        //更新布局
//        textLayout.clear()
//        textLayout.setLineDecoration(lineDecoration)
//        textLayout.textRender=SimpleTextAnimation()
//        textLayout.setText(spannableString)

    }

    /**
     * 根据位置动态获得Span对象
     */
    private fun getTextSpan(index:Int):Any?{
        return when(index){
            0->{
                val layoutInflater = LayoutInflater.from(this)
//                val index=Random.nextInt(3)
                val index=0
                val layout = when(index){
                    0->layoutInflater.inflate(R.layout.image_layout1, textLayout, false)
                    1->{
                        val textLayout=layoutInflater.inflate(R.layout.text_layout1, textLayout, false)
                        val textView=textLayout.findViewById<TextView>(R.id.textView)
                        textView.text= Data.ITEMS[Data.RANDOM.nextInt(Data.ITEMS.size)]
                        textLayout
                    }
                    else->layoutInflater.inflate(R.layout.progress_layout1, textLayout, false)
                }
                ViewSpan(layout)
            }
            1->{
//                val d1 = resources.getDrawable(R.mipmap.ic_launcher)
//                d1.setBounds(0, 0, 100, 100)
//                ImageSpan(d1)
                val layout=layoutInflater.inflate(R.layout.progress_layout1, textLayout, false)
                ViewSpan(layout)
            }
            2-> BackgroundColorSpan(Color.RED)
            3-> ForegroundColorSpan(Color.YELLOW)
            4-> RelativeSizeSpan(2f)
            5-> MaskFilterSpan(BlurMaskFilter(3f, BlurMaskFilter.Blur.OUTER))
            6-> MaskFilterSpan(EmbossMaskFilter(floatArrayOf(1f, 1f, 3f), 1.5f, 8f, 3f))
            7-> StrikethroughSpan()
            8-> UnderlineSpan()
            9-> AbsoluteSizeSpan(20, true)
            10-> RelativeSizeSpan(2f)
            11-> ScaleXSpan(2f)
            12-> StyleSpan(Typeface.BOLD_ITALIC)
            else -> null
        }
    }
}
