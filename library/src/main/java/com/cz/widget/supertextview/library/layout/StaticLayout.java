package com.cz.widget.supertextview.library.layout;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;

import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.render.TextRender;
import com.cz.widget.supertextview.library.style.ReplacementSpan;
import com.cz.widget.supertextview.library.text.TextLine;
import com.cz.widget.supertextview.library.utils.ArrayUtils;
import com.cz.widget.supertextview.library.view.TextParent;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义静态文本排版对象
 * 1. 支持TextView span设计
 * 2. 支持View以span形式存在
 * 3. 支持文本对元素的流式排版
 */
public class StaticLayout extends Layout {
    private static final String TAG="StaticLayout";
    private static final Rect decorationTmpRect =new Rect();
    private int lineCount;

    //当前展示行信息
    private TextLine[] textLines;
    private char[] charArrays;
    private float[] widths;
    //行装饰器
    private LineDecoration lineDecoration;
    /**
     * @param source      操作文本
     * @param paint      绘制paint
     * @param width      排版宽
     * @param spacingAdd 行额外添加空间
     */
    public StaticLayout(CharSequence source, TextPaint paint, LineDecoration lineDecoration,
                        TextRender textRender, int width, float spacingAdd, int align) {
        super(source,paint,lineDecoration,textRender, width, spacingAdd);
        this.lineDecoration=lineDecoration;
        //填充内容
        fillText(source, 0, source.length(), paint, width, spacingAdd, align);
        charArrays = null;
        widths = null;
    }
    /**
     * 填充内容
     */
    void fillText(CharSequence source, int bufferStart, int bufferEnd,
        TextPaint paint, int outerWidth, float spacingAdd, int align){
        List<TextLine> textLineList=new ArrayList<>();
        TextParent parentView = textRender.getTarget();
        int end = TextUtils.indexOf(source, '\n', bufferStart, bufferEnd);
        int bufferSize = end >= 0 ? end - bufferStart : bufferEnd - bufferStart;
        if (charArrays == null) {
            charArrays = new char[ArrayUtils.idealCharArraySize(bufferSize + 1)];
            widths = new float[ArrayUtils.idealIntArraySize((bufferSize + 1) * 2)];
        }
        boolean first = true;
        int paragraphIndex=0;
        int v=0;
        for (int start = bufferStart; start <= bufferEnd; start = end) {
            if (first)
                first = false;
            else{
                paragraphIndex++;
                end = TextUtils.indexOf(source, '\n', start, bufferEnd);
            }
            if (end < 0)
                end = bufferEnd;
            else
                end++;
            //填充段落
//            Paragraph paragraph = fillParagraphText(paragraphIndex, source, start, end, paint, outerWidth,v, spacingAdd, align);
//            for(int i=0;i<paragraph.lineCount;i++){
//                //排版子控件
//                TextLine textLine = paragraph.textElements[i];
//                textLine.layoutViewSpan(parentView,source,0,0);
//                textLineList.add(textLine);
//            }
//            //记录最后一个行底部距离
//            if(0 < paragraph.lineCount){
//                v = paragraph.textElements[paragraph.lineCount-1].getDecoratedScrollLineBottom();
//            }
            if (end == bufferEnd)
                break;
        }
        textLines= textLineList.toArray(new TextLine[textLineList.size()]);
        lineCount=textLines.length;
    }

    /**
     * 将指定范围内replacementSpan 用空的占位字符\uFFFC替换
     * @param source
     * @param start
     * @param end
     * @param chs
     */
    private void setReplacementSpanWord(CharSequence source, int start, int end, char[] chs) {
        if (source instanceof Spanned) {
            Spanned sp = (Spanned) source;
            ReplacementSpan[] spans = sp.getSpans(start, end, ReplacementSpan.class);
            //减少ReplacementSpan运算
            for (int y = 0; y < spans.length; y++) {
                int a = sp.getSpanStart(spans[y]);
                int b = sp.getSpanEnd(spans[y]);
                for (int x = a; x < b; x++) {
                    chs[x - start] = '\uFFFC';
                }
            }
        }
    }

    /**
     * 设置行装饰器
     * @param lineDecoration
     */
    public void setLineDecoration(LineDecoration lineDecoration){
        this.lineDecoration=lineDecoration;
    }

    /**
     * 查找ReplacementSpan
     * @param start
     * @param end
     * @return
     */
    private ReplacementSpan findReplacementSpan(int start,int end){
        ReplacementSpan replacementSpan = null;
        ReplacementSpan[] replacementSpans = getSpans(start,end, ReplacementSpan.class);
        if(null != replacementSpans && 0 < replacementSpans.length){
            //检测换行规则,如果存在重叠的ReplaceSpan其实目前算法只取最后一个
            replacementSpan = replacementSpans[replacementSpans.length - 1];
        }
        return replacementSpan;
    }

    public int getLineCount() {
        return lineCount;
    }

    public int getDecoratedScrollLineTop(int line) {
        return textLines[line].getDecoratedLineTop();
    }

    @Override
    public int getDecoratedScrollLineBottom(int line) {
        return textLines[line].getDecoratedLineBottom();
    }

    @Override
    public int getScrollLineTop(int line) {
        return textLines[line].getScrollTop();
    }

    @Override
    public int getScrollLineBottom(int line) {
        return textLines[line].getScrollBottom();
    }

    public int getDecoratedDescent(int line) {
        return textLines[line].getScrollDescent();
    }

    @Override
    public int getLineStart(int line) {
        return textLines[line].getLineStart();
    }

    @Override
    public int getLineGravity(int line) {
        return textLines[line].getLineAlign();
    }

    public int getLineLatterStart(int line) {
        return textLines[line].getLineStart();
    }

    @Override
    public int getLineLatterEnd(int line) {
        return textLines[line].getLineEnd();
    }


    /**
     * 绘制元素
     * @param c
     */
    @Override
    public void draw(Canvas c) {
        if(0 < lineCount){
            TextRender textRender = getTextRender();
            for(int i=0;i<lineCount;i++){
                TextLine textLine = textLines[i];
                //绘制行信息
                textLine.draw(c,textRender,text,paint,workPaint,fontMetricsInt,width,true);
            }
        }
        //绘制其他元素
        textRender.onDraw(c);
    }

}
