package com.cz.widget.supertextview.library.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;

import com.cz.widget.supertextview.library.span.ImageSpan;
import com.cz.widget.supertextview.library.span.ViewSpan;

/**
 * 替换元素,用于扩展自定义元素属性
 * @see ViewSpan 扩展View
 * @see ImageSpan 扩展Image
 */
public abstract class ReplacementSpan extends MetricAffectingSpan {
    /**
     * 跟随内容
     */
    private static final int FLOW_FLAG=0x01;
    /**
     * 当前元素独占一行
     */
    private static final int PARAGRAPH_FLAG = 0x02;
    /**
     * 断行标志
     */
    private static final int BREAK_LINE_FLAG = 0x04;
    /**
     * 检测是否需要断行,意思为超出内容则断,不超出不断
     */
    private static final int CONSIDER_BREAK_LINE_FLAG = 0x08;
    /**
     * 当前元素独占一行
     */
    private static final int SINGLE_LINE_FLAG = 0x10;

    /**
     * 跟随内容向右流动
     */
    public static final int FLOW=FLOW_FLAG;
    /**
     * 标记段落,后续内容按此段落信息流动
     */
    public static final int PARAGRAPH =PARAGRAPH_FLAG | CONSIDER_BREAK_LINE_FLAG;
    /**
     * 标记段落,并自动断行
     */
    public static final int PARAGRAPH_BREAK_LINE=PARAGRAPH_FLAG | BREAK_LINE_FLAG;
    /**
     * 遇到此控件自动换行
     */
    public static final int BREAK_LINE=BREAK_LINE_FLAG;
    /**
     * 标志span按flow摆放,但如果超出尺寸 ,则放到下一行
     */
    public static final int CONSIDER_BREAK_LINE=FLOW_FLAG | CONSIDER_BREAK_LINE_FLAG;
    /**
     * 独占一行
     */
    public static final int SINGLE_LINE=BREAK_LINE_FLAG | SINGLE_LINE_FLAG;

    /**
     * 该元素是否为流式布局
     */
    public static boolean isFlow(int layoutMode){
        return 0!=(FLOW_FLAG & layoutMode);
    }

    /**
     * 该元素是否断行
     */
    public static boolean isBreakLine(int layoutMode){
        return 0!=(BREAK_LINE_FLAG & layoutMode);
    }

    /**
     * 该元素是否断行
     */
    public static boolean isSingleLine(int layoutMode){
        return 0!=(SINGLE_LINE_FLAG & layoutMode);
    }

    /**
     * 该元素是否超出断行
     */
    public static boolean considerBreakLine(int layoutMode){
        return 0!=(CONSIDER_BREAK_LINE_FLAG & layoutMode);
    }

    /**
     * 该元素是否超出断行
     */
    public static boolean isParagraph(int layoutMode){
        return 0!=(PARAGRAPH_FLAG & layoutMode);
    }

    public abstract int getSize(Paint paint, CharSequence text,
                         int start, int end,
                         Paint.FontMetricsInt fm);
    public abstract void draw(Canvas canvas, CharSequence text,
                     int start, int end, float x,float y, Paint paint);

    /**
     * 获得元素排版模式
     * @return
     */
    public int getSpanLayoutMode(){
        return FLOW;
    }

    /**
     * This method does nothing, since ReplacementSpans are measured
     * explicitly instead of affecting Paint properties.
     */
    public void updateMeasureState(TextPaint p) { }

    /**
     * This method does nothing, since ReplacementSpans are drawn
     * explicitly instead of affecting Paint properties.
     */
    public void updateDrawState(TextPaint ds) { }
}
