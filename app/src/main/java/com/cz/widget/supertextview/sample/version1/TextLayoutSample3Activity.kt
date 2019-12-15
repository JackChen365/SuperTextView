package com.cz.widget.supertextview.sample.version1

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Color
import android.graphics.EmbossMaskFilter
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.cz.widget.supertextview.library.span.*
import com.cz.widget.supertextview.library.spannable.SpannableString
import com.cz.widget.supertextview.library.style.ReplacementSpan
import com.cz.widget.supertextview.sample.R
import com.cz.widget.supertextview.sample.linedecoration.HighlightLineDecoration
import com.okay.sampletamplate.ToolBarActivity
import com.okay.sampletamplate.data.DataProvider
import kotlinx.android.synthetic.main.activity_text_layout_sample3.*
import kotlin.random.Random

class TextLayoutSample3Activity : ToolBarActivity() {
    companion object{
        private const val TAG="TextLayoutSample2Activity"
        private const val SPAN_COUNT=13
    }
    private val output=StringBuilder()
    private var lineDecoration:HighlightLineDecoration?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_layout_sample3)
        lineDecoration=HighlightLineDecoration(this)
        //点击设置不同的布局
        createText()
        copyButton.setOnClickListener {
            val myClipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val text = output.toString()
            val myClip = ClipData.newPlainText("text", text)
            myClipboard.primaryClip = myClip
            Toast.makeText(applicationContext, "文本已复制~", Toast.LENGTH_SHORT).show()
        }
        randomButton.setOnClickListener {
            createText()
        }
    }

    private fun createText(){
//        val spanCount=10+Random.nextInt(10)
//        val text=(0 until spanCount*2).
//            map { DataProvider.ITEMS[DataProvider.RANDOM.nextInt(DataProvider.ITEMS.size)] }.joinToString(" ")
        val text=assets.open("Little Prince小王子.txt").bufferedReader().readText()
        val spannableString=SpannableString(text)
//        初始化span
        var start=0
        val positionList= mutableListOf<Int>()
        val spanPositionList= mutableListOf<Int>()
        while(-1!=start){
            val i=Random.nextInt(SPAN_COUNT)
            var index=text.indexOf(" ",start+1)
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
        output.clear()
        output.append("$text\n")
        output.append("$positionList\n")
        output.append("$spanPositionList\n")
        textLayout.clear()
        textLayout.setLineDecoration(lineDecoration)
        textLayout.setText(spannableString)

//        val spanCount=8
//        val text=(0 until spanCount).
//            map { DataProvider.ITEMS[it] }.joinToString(" ")
//        val spannableString=SpannableString(text)
//        var lineStart=19
//        var textSpan = getTextSpan(1)
//        if(null!=textSpan){
//            spannableString.setSpan(textSpan, lineStart, lineStart+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        }
//        lineStart=28
//        textSpan = getTextSpan(0)
//        if(null!=textSpan){
//            spannableString.setSpan(textSpan, lineStart, lineStart+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        }
//        val text="Feta Geitost Swiss Bel Paese Cotherstone Airag Metton (Cancoillotte)"
//        val text=assets.open("Little Prince小王子.txt").bufferedReader().readText()
//
//        val length = text.length
//        val spannableString=SpannableString(text)
//        //初始化span
//        val index=text.indexOf("\n")
//        val positionList= mutableListOf(28, 45)
//        val spanPositionList= mutableListOf(0, 0)
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
//        textLayout.setText(spannableString)



        //初始化span列表
//        val spanList= mutableMapOf<String,Any>()
//        //        //添加View
//        val layoutInflater = LayoutInflater.from(this)
//        val imageLayout1 = layoutInflater.inflate(R.layout.image_layout1, textLayout, false)
//        spanList.put("VewSpan 添加View体1", ViewSpan(imageLayout1))
//        val imageLayout2 = layoutInflater.inflate(R.layout.progress_layout1, textLayout, false)
//        spanList.put("VewSpan 添加View体2", ViewSpan(imageLayout2))
//        val d1 = resources.getDrawable(R.mipmap.ic_launcher)
//        d1.setBounds(0, 0, 100, 100)
//        spanList.put("ImageSpan 图片1", ImageSpan(d1))
//        spanList.put("BackgroundColorSpan", BackgroundColorSpan(Color.RED))
//        spanList.put("ForegroundColorSpan", ForegroundColorSpan(Color.YELLOW))
//        spanList.put("RelativeSizeSpan 相对大小（文本字体）", RelativeSizeSpan(2.5f))
//        spanList.put("MaskFilterSpan 修饰效果",
//            MaskFilterSpan(BlurMaskFilter(3f, BlurMaskFilter.Blur.OUTER))
//        )
//        spanList.put("浮雕(EmbossMaskFilter)",
//            MaskFilterSpan(EmbossMaskFilter(floatArrayOf(1f, 1f, 3f), 1.5f, 8f, 3f))
//        )
//        spanList.put("StrikethroughSpan 删除线（中划线）", StrikethroughSpan())
//        spanList.put("UnderlineSpan 下划线", UnderlineSpan())
//        spanList.put("AbsoluteSizeSpan 绝对大小（文本字体）", AbsoluteSizeSpan(20, true))
//        val drawableSpan = object : DynamicDrawableSpan(ALIGN_BASELINE) {
//            override fun getDrawable(): Drawable {
//                val d = resources.getDrawable(R.mipmap.ic_launcher)
//                d.setBounds(0, 0, 50, 50)
//                return d
//            }
//        }
//        spanList.put("DynamicDrawableSpan",drawableSpan)
//        spanList.put("RelativeSizeSpan 相对大小（文本字体）", RelativeSizeSpan(2.5f))
//        spanList.put("ScaleXSpan 基于x轴缩放", ScaleXSpan(3.8f))
//        spanList.put("StyleSpan 字体样式：粗体、斜体等", StyleSpan(Typeface.BOLD_ITALIC))
//        spanList.put("SuperscriptSpan", SuperscriptSpan())

//        val text=assets.open("dynamic_chapter1").bufferedReader().readText()
//        val spannableString = SpannableString(text)
//        var start=0
//        spanList.entries.forEachIndexed { index, (title,spanItem)->
//            //            val index=source.indexOf('\n',lineStart)
////            var lineEnd=if(-1==index) source.length else index
//            spannableString.setSpan(spanItem, start, start+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//            start+=8
//        }
//        textLayout.setLineDecoration(lineDecoration)
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
                        textView.text=DataProvider.ITEMS[DataProvider.RANDOM.nextInt(DataProvider.ITEMS.size)]
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
            2->BackgroundColorSpan(Color.RED)
            3->ForegroundColorSpan(Color.YELLOW)
            4->RelativeSizeSpan(2f)
            5->MaskFilterSpan(BlurMaskFilter(3f, BlurMaskFilter.Blur.OUTER))
            6->MaskFilterSpan(EmbossMaskFilter(floatArrayOf(1f, 1f, 3f), 1.5f, 8f, 3f))
            7->StrikethroughSpan()
            8->UnderlineSpan()
            9->AbsoluteSizeSpan(20, true)
            10->RelativeSizeSpan(2f)
            11->ScaleXSpan(2f)
            12->StyleSpan(Typeface.BOLD_ITALIC)
            else -> null
        }
    }

}