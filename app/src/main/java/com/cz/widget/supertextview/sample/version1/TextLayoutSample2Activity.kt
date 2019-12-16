package com.cz.widget.supertextview.sample.version1

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Color
import android.graphics.EmbossMaskFilter
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.cz.widget.supertextview.library.span.*
import com.cz.widget.supertextview.library.spannable.SpannableString
import com.cz.widget.supertextview.sample.R
import com.cz.widget.supertextview.sample.linedecoration.HighlightLineDecoration
import com.okay.sampletamplate.ToolBarActivity
import com.okay.sampletamplate.data.DataProvider
import kotlinx.android.synthetic.main.activity_text_layout_sample2.*
import kotlin.random.Random


class TextLayoutSample2Activity : ToolBarActivity() {
    companion object{
        private const val TAG="TextLayoutSample2Activity"
        private const val SPAN_COUNT=13
    }
    private val output=StringBuilder()
    private var lineDecoration: HighlightLineDecoration?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_layout_sample2)
        //点击设置不同的布局
        lineDecoration=HighlightLineDecoration(this)
        createText()
        copyButton.setOnClickListener {
            val myClipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val text = output.toString()
            val myClip = ClipData.newPlainText("text", text)
            myClipboard.primaryClip = myClip
            Toast.makeText(applicationContext, "$text 已复制~", Toast.LENGTH_SHORT).show()
        }
        randomButton.setOnClickListener {
            createText()
        }
    }

    private fun createText(){
        val spanCount=10+Random.nextInt(10)
        val text=(0 until spanCount).
            map { DataProvider.ITEMS[DataProvider.RANDOM.nextInt(DataProvider.ITEMS.size)] }.joinToString(" ")
        val spannableString=SpannableString(text)
        //初始化span
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
                    spannableString.setSpan(textSpan, index, Math.min(text.length,index+5), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            start=index
        }
        output.clear()
        output.append("$text\n")
        output.append("$positionList\n")
        output.append("$spanPositionList\n")

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


//        val text="Ardi Gasna Frinault Raschera Baby Swiss Prince-Jean Fougerus St. Agur Blue Cheese Breakfast Cheese Bleu de Gex Briquette de Brebis Aubisque Pyrenees Bruder Basil Cabrales Quark Cheshire Cream Cheese Fromage Corse Cottage Cheese Waimata Farmhouse Blue"
//        val spannableString=SpannableString(text)
//        //初始化span
//        val positionList= mutableListOf(10, 14,10)
//        val spanPositionList= mutableListOf(10,11,2)
//        positionList.forEachIndexed { index, position->
//            var textSpan = getTextSpan(spanPositionList[index])
//            spannableString.setSpan(textSpan, position, position+5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        }
        //更新布局
        textLayout.clear()
        textLayout.setLineDecoration(lineDecoration)
        textLayout.setText(spannableString)
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
            2-> BackgroundColorSpan(Color.RED)
            3-> ForegroundColorSpan(Color.YELLOW)
            4-> RelativeSizeSpan(2f)
            5-> MaskFilterSpan(BlurMaskFilter(3f, BlurMaskFilter.Blur.OUTER))
            6->MaskFilterSpan(EmbossMaskFilter(floatArrayOf(1f, 1f, 3f), 1.5f, 8f, 3f))
            7->StrikethroughSpan()
            8->UnderlineSpan()
            9->AbsoluteSizeSpan(35, true)
            10->RelativeSizeSpan(2.5f)
            11->ScaleXSpan(3.8f)
            12->StyleSpan(Typeface.BOLD_ITALIC)
            else -> null
        }
    }

}