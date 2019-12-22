package com.cz.widget.supertextview.sample.layout

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
import com.cz.widget.supertextview.library.style.ReplacementSpan
import com.cz.widget.supertextview.sample.R
import com.cz.widget.supertextview.sample.data.Data
import com.cz.widget.supertextview.sample.linedecoration.HighlightLineDecoration
import com.cz.widget.supertextview.sample.template.ToolBarActivity
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
//        val spanCount=20+Random.nextInt(10)
//        val text=(0 until spanCount).
//            map { Data.ITEMS[Data.RANDOM.nextInt(Data.ITEMS.size)] }.joinToString(" ")
        val text=assets.open("dynamic_chapter1").bufferedReader().readText()
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

//        Coeur de Chevre Raclette Vieux Corse Limburger Canestrato Pave de Chirac Reblochon Barry's Bay Cheddar Vieux Corse Ragusano Mascarpone (Australian) Maroilles Chabis de Gatine Rustinu Ricotta Salata Saint-Nectaire Barry's Bay Cheddar Selles sur Cher Waterloo Leyden Asiago Piora Meredith Blue Pasteurized Processed Durrus Serra da Estrela Le Fium Orbo Dunsyre Blue Buxton Blue
//        [5, 8, 15, 24, 30, 36, 46, 57, 62, 65, 72, 82, 90, 94, 102, 108, 114, 123, 134, 147, 157, 164, 167, 174, 182, 190, 197, 212, 220, 224, 232, 239, 243, 248, 257, 264, 271, 277, 286, 291, 303, 313, 320, 326, 329, 337, 340, 345, 350, 358, 363, 370]
//        [2, 9, 8, 2, 11, 10, 5, 0, 2, 4, 7, 2, 0, 0, 3, 7, 11, 8, 0, 4, 11, 2, 4, 8, 1, 7, 4, 8, 6, 0, 8, 3, 10, 1, 1, 11, 3, 7, 4, 11, 5, 10, 0, 3, 1, 9, 6, 0, 9, 5, 3, 1]



//        val text="Coeur de Chevre Raclette Vieux Corse Limburger Canestrato Pave de Chirac Reblochon Barry's Bay Cheddar Vieux Corse Ragusano Mascarpone (Australian) Maroilles Chabis de Gatine Rustinu Ricotta Salata Saint-Nectaire Barry's Bay Cheddar Selles sur Cher Waterloo Leyden Asiago Piora Meredith Blue Pasteurized Processed Durrus Serra da Estrela Le Fium Orbo Dunsyre Blue Buxton Blue"
//        val spannableString=SpannableString(text)
//        //初始化span
//        val positionList= mutableListOf(5, 8, 15, 24, 30, 36, 46, 57, 62, 65, 72, 82, 90, 94, 102, 108, 114, 123, 134, 147, 157, 164, 167, 174, 182, 190, 197, 212, 220, 224, 232, 239, 243, 248, 257, 264, 271, 277, 286, 291, 303, 313, 320, 326, 329, 337, 340, 345, 350, 358, 363, 370)
//        val spanPositionList= mutableListOf(2, 9, 8, 2, 11, 10, 5, 0, 2, 4, 7, 2, 0, 0, 3, 7, 11, 8, 0, 4, 11, 2, 4, 8, 1, 7, 4, 8, 6, 0, 8, 3, 10, 1, 1, 11, 3, 7, 4, 11, 5, 10, 0, 3, 1, 9, 6, 0, 9, 5, 3, 1)
//        positionList.forEachIndexed { index, position->
//            var textSpan = getTextSpan(spanPositionList[index])
//            if(textSpan is ReplacementSpan){
//                spannableString.setSpan(textSpan,position, Math.min(text.length,position+1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//            } else {
//                spannableString.setSpan(textSpan, position, Math.min(text.length,position+5), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//            }
//        }
//        //更新布局
//        textLayout.clear()
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
                        textView.text=Data.ITEMS[Data.RANDOM.nextInt(Data.ITEMS.size)]
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
            9->AbsoluteSizeSpan(20, true)
            10->RelativeSizeSpan(2f)
            11->ScaleXSpan(2f)
            12->StyleSpan(Typeface.BOLD_ITALIC)
            else -> null
        }
    }

}