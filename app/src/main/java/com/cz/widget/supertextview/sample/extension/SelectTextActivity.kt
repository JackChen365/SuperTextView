package com.cz.widget.supertextview.sample.extension

import android.graphics.BlurMaskFilter
import android.graphics.Color
import android.graphics.EmbossMaskFilter
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.view.LayoutInflater
import com.cz.widget.supertextview.library.span.*
import com.cz.widget.supertextview.library.spannable.SpannableString

import com.cz.widget.supertextview.sample.R
import com.okay.sampletamplate.ToolBarActivity
import kotlinx.android.synthetic.main.activity_select_text.*

class SelectTextActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_text)
//初始化span列表
        val spanList= mutableMapOf<String,Any>()
        //        //添加View
        val layoutInflater = LayoutInflater.from(this)
        val imageLayout1 = layoutInflater.inflate(R.layout.image_layout1, textLayout, false)
        spanList.put("VewSpan 添加View体1", ViewSpan(imageLayout1))
        val imageLayout2 = layoutInflater.inflate(R.layout.progress_layout1, textLayout, false)
        spanList.put("VewSpan 添加View体2", ViewSpan(imageLayout2))
        val d1 = resources.getDrawable(R.mipmap.ic_launcher)
        d1.setBounds(0, 0, 100, 100)
        spanList.put("ImageSpan 图片1", ImageSpan(d1))
        spanList.put("BackgroundColorSpan", BackgroundColorSpan(Color.RED))
        spanList.put("ForegroundColorSpan", ForegroundColorSpan(Color.YELLOW))
        spanList.put("RelativeSizeSpan 相对大小（文本字体）", RelativeSizeSpan(2.5f))
        spanList.put("MaskFilterSpan 修饰效果",
            MaskFilterSpan(BlurMaskFilter(3f, BlurMaskFilter.Blur.OUTER))
        )
        spanList.put("浮雕(EmbossMaskFilter)",
            MaskFilterSpan(EmbossMaskFilter(floatArrayOf(1f, 1f, 3f), 1.5f, 8f, 3f))
        )
        spanList.put("StrikethroughSpan 删除线（中划线）", StrikethroughSpan())
        spanList.put("UnderlineSpan 下划线", UnderlineSpan())
        spanList.put("AbsoluteSizeSpan 绝对大小（文本字体）", AbsoluteSizeSpan(20, true))
        val drawableSpan = object : DynamicDrawableSpan(ALIGN_BASELINE) {
            override fun getDrawable(): Drawable {
                val d = resources.getDrawable(R.mipmap.ic_launcher)
                d.setBounds(0, 0, 50, 50)
                return d
            }
        }
        spanList.put("DynamicDrawableSpan",drawableSpan)
        spanList.put("RelativeSizeSpan 相对大小（文本字体）", RelativeSizeSpan(2.5f))
        spanList.put("ScaleXSpan 基于x轴缩放", ScaleXSpan(3.8f))
        spanList.put("StyleSpan 字体样式：粗体、斜体等", StyleSpan(Typeface.BOLD_ITALIC))
        spanList.put("SuperscriptSpan", SuperscriptSpan())
//

        val text=assets.open("dynamic_chapter1").bufferedReader().readText()
        val spannableString = SpannableString(text)
        var start=0
        spanList.entries.forEachIndexed { index, (title,spanItem)->
            //            val index=source.indexOf('\n',lineStart)
//            var lineEnd=if(-1==index) source.length else index
            spannableString.setSpan(spanItem, start, start+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            start+=8
        }
        textLayout.setText(spannableString)
    }
}
