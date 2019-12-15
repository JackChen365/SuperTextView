package com.cz.widget.supertextview.library.paragraph;

import android.graphics.Paint;

/**
 * 段落的计算信息
 */
public class ParagraphLayoutInfo {
    /**
     * 是否处在段落内
     */
    public boolean inParagraph=false;
    /**
     * 段落的左侧起始位置
     */
    public float paragraphLeftOffset=0;
    /**
     * 当前段落高
     */
    public float paragraphHeight=0;
    /**
     * 段落元素的文本信息
     */
    public Paint.FontMetricsInt paragraphFontMetrics=new Paint.FontMetricsInt();

    public void reset(){
        inParagraph=false;
        paragraphLeftOffset=0;
        paragraphHeight=0;
        paragraphFontMetrics.top=0;
        paragraphFontMetrics.bottom=0;
        paragraphFontMetrics.ascent=0;
        paragraphFontMetrics.descent=0;
    }
}
