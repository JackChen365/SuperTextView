package com.cz.widget.supertextview.sample.other

import android.os.Bundle
import android.text.Spannable
import android.view.Gravity
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.cz.widget.supertextview.library.span.ImageSpan
import com.cz.widget.supertextview.library.span.ViewSpan
import com.cz.widget.supertextview.library.spannable.SpannableString

import com.cz.widget.supertextview.sample.R
import com.cz.widget.supertextview.sample.data.Data
import com.cz.widget.supertextview.sample.linedecoration.HighlightLineDecoration
import com.cz.widget.supertextview.sample.template.ToolBarActivity
import kotlinx.android.synthetic.main.activity_text_layout_sample4.*

class TestLayoutSampleActivity : ToolBarActivity() {
    companion object{
        private const val SPAN_COUNT=6
    }
    private val spanList= mutableListOf<Int>()
    private val output=StringBuilder()
    private var lineDecoration: HighlightLineDecoration?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_layout_sample4)
        //点击设置不同的布局
        lineDecoration=HighlightLineDecoration(this)
        //设置不同模式下添加不同控件

        flowButton.setOnClickListener { appendTextSpan(0) }
        paragraphButton.setOnClickListener { appendTextSpan(1) }
        paragraphLineButton.setOnClickListener { appendTextSpan(2) }
        breakLineButton.setOnClickListener { appendTextSpan(3) }
        considerBreakLineButton.setOnClickListener { appendTextSpan(4) }
        singleLineButton.setOnClickListener { appendTextSpan(5) }
        textButton.setOnClickListener {
            appendTextSpan(6)
        }
        clearButton.setOnClickListener {
            spanList.clear()
            textLayout.clear()
        }
    }

    private fun appendTextSpan(spanIndex:Int){
        spanList.add(spanIndex)
        val text= (0..spanList.size).joinToString(" ") { i -> Data.ITEMS[i] }
        val spannableString=SpannableString(text)
        var start=-1
        for(index in spanList){
            //文本
            val textSpan = getTextSpan(index)
            start = text.indexOf(' ',start+1)
            if(null!=textSpan){
                spannableString.setSpan(textSpan,start, Math.min(text.length,start+1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        textLayout.clear()
        textLayout.setLineDecoration(lineDecoration)
        textLayout.setText(spannableString)
    }

    /**
     * 根据位置动态获得Span对象
     */
    private fun getTextSpan(index:Int):Any?{
        val layoutInflater = LayoutInflater.from(this)
        return when(index){
            0->{
                //流式ImageSpan
                val d1 = ContextCompat.getDrawable(this, R.mipmap.ic_launcher)
                d1?.setBounds(0, 0, 100, 100)
                ImageSpan(d1,Gravity.TOP)
            }
            1->{
                //流式段落
                val layout=layoutInflater.inflate(R.layout.image_paragraph_layout, textLayout, false)
                ViewSpan(layout)
            }
            2->{
                //换行段落
                val layout=layoutInflater.inflate(R.layout.image_paragraph_break_line_layout, textLayout, false)
                ViewSpan(layout)
            }
            3->{
                //换行元素
                val layout=layoutInflater.inflate(R.layout.image_break_line_layout, textLayout, false)
                ViewSpan(layout)
            }
            4->{
                //检测换行元素
                val layout=layoutInflater.inflate(R.layout.image_consider_break_line_layout, textLayout, false)
                ViewSpan(layout)
            }
            5->{
                //单行元素
                val layout=layoutInflater.inflate(R.layout.image_single_line_layout, textLayout, false)
                ViewSpan(layout)
            }
            else -> null
        }
    }

}
