package com.cz.widget.supertextview.library.layout.measurer;

import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Spanned;
import android.text.TextPaint;

import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.style.MetricAffectingSpan;
import com.cz.widget.supertextview.library.text.TextElement;
import com.cz.widget.supertextview.library.utils.ArrayUtils;
import com.cz.widget.supertextview.library.utils.TextUtilsCompat;
import com.cz.widget.supertextview.library.view.TextParent;

/**
 * 文本测量器,支持行装饰器的最基本文本元素测量
 * 此对象,所有运算都是一次性的.每次调用 {@link TextLineMeasurer#measureTextElement} 都会重新获得一批文本元素
 * 动态设定:每次运算字符个数,不能太长.当前已经设定动态
 */
public abstract class TextLineMeasurer extends TextElementMeasurer{
    final Rect decorationRect = new Rect();
    private char[] charArrays;
    private float[] textWidths;
    int fmTop, fmBottom, fmAscent, fmDescent;
    int okAscent, okDescent, okTop, okBottom;
    int fitAscent, fitDescent, fitTop, fitBottom;
    int here, ok, fit;
    float w, v;
    int lineCount;

    public TextLineMeasurer(LineDecoration lineDecoration) {
        super(lineDecoration);
    }
    /**
     * 计算样式的字体规格
     */
    public abstract boolean measureText(TextMeasurerInfo textMeasurerInfo, TextElement[] textElementArray, float[] widths, int paragraph, int line,int start,int next);
    /**
     * 计算样式的字体规格
     */
    public abstract boolean measureStyled(TextParent textParent,TextMeasurerInfo textMeasurerInfo, TextElement[] textElementArray, float[] widths, int paragraph, int line, int i, int next);
    /**
     * 生成行元素信息
     */
    public abstract boolean generateTextElement(TextMeasurerInfo textMeasurerInfo,TextElement[] textElementArray,int paragraph,int line,int start, int end, int above, int below,float x,float y,Rect decorationRect);

    @Override
    public TextElement[] measureTextElement(TextParent textParent, TextMeasurerInfo textMeasurerInfo, int paragraph, int line){
        return measureTextElement(textParent,textMeasurerInfo,paragraph,line,0,textMeasurerInfo.outerWidth);
    }

    /**
     * 填空段落信息
     * @param textMeasurerInfo
     * @return
     */
    protected TextElement[] measureTextElement(TextParent textParent, TextMeasurerInfo textMeasurerInfo, int paragraph, int line, int left, int right) {
        TextElement[] textElementArray=idealTextElementArray(null,lineCount);
        CharSequence source = textMeasurerInfo.source;
        TextPaint workPaint = textMeasurerInfo.workPaint;
        Paint.FontMetricsInt fm = textMeasurerInfo.fontMetricsInt;
        //获得行偏移
        getLineOffsets(paragraph,line, decorationRect);
        w = left +decorationRect.left;
        v = textMeasurerInfo.top +decorationRect.top;
        Spanned spanned = null;
        if (source instanceof Spanned){
            spanned = (Spanned) source;
            textMeasurerInfo.spanned=spanned;
        }
        int start = textMeasurerInfo.start;
        int end = textMeasurerInfo.end;
        char[] chs = charArrays;
        float[] widths = textWidths;
        okAscent = okDescent = okTop = okBottom = 0;
        fitAscent = fitDescent = fitTop = fitBottom = 0;
        lineCount++;
        here = start;
        ok = start;
        fit = start;
        int next;
        boolean stopMeasure=false;
        for (int i = start; i < end; i = next) {
            //获得下一个流转的位置
            next = getNextTransition(spanned, i,end);
            if (null==charArrays||end - start > chs.length) {
                chs = new char[ArrayUtils.idealCharArraySize(end - start)];
                charArrays = chs;
            }
            if (null==textWidths ||(end - start) * 2 > widths.length) {
                widths = new float[ArrayUtils.idealIntArraySize((end - start) * 2)];
                textWidths = widths;
            }
            TextUtilsCompat.getChars(source, start, next, chs, 0);
            //运算段落,检测纯文本,与样式运算
            if (spanned == null) {
                stopMeasure=measureText(textMeasurerInfo,textElementArray,widths,paragraph,line,start,next);
            } else {
                workPaint.baselineShift = 0;
                stopMeasure=measureStyled(textParent,textMeasurerInfo,textElementArray,widths,paragraph,line,start,next);
                if (workPaint.baselineShift < 0) {
                    fm.ascent += workPaint.baselineShift;
                    fm.top += workPaint.baselineShift;
                } else {
                    fm.descent += workPaint.baselineShift;
                    fm.bottom += workPaint.baselineShift;
                }
            }
            fmTop = fm.top;fmBottom = fm.bottom;fmAscent = fm.ascent;fmDescent = fm.descent;
            for (int j = i; j < next&&!stopMeasure; j++) {
                char c = chs[j - start];
                if (c != '\n') {
                    w += widths[j - start + (end - start)];
                }
                if (w <= (right-decorationRect.right)) {
                    fit = j + 1;
                    if (fmTop < fitTop)
                        fitTop = fmTop;
                    if (fmAscent < fitAscent)
                        fitAscent = fmAscent;
                    if (fmDescent > fitDescent)
                        fitDescent = fmDescent;
                    if (fmBottom > fitBottom)
                        fitBottom = fmBottom;
                    if (c == ' ' || c == '\t' || c == '\uFFFC' ||
                            ((c == '.' || c == ',' || c == ':' || c == ';') &&
                                    (j - 1 < here || !Character.isDigit(chs[j - 1 - start])) &&
                                    (j + 1 >= next || !Character.isDigit(chs[j + 1 - start]))) ||
                            ((c == '/' || c == '-') &&
                                    (j + 1 >= next || !Character.isDigit(chs[j + 1 - start])))) {
                        ok = j + 1;
                        if (fitTop < okTop)
                            okTop = fitTop;
                        if (fitAscent < okAscent)
                            okAscent = fitAscent;
                        if (fitDescent > okDescent)
                            okDescent = fitDescent;
                        if (fitBottom > okBottom)
                            okBottom = fitBottom;
                    }
                } else {
                    //如果在段落内,高度
                    int lastLine=line;
                    w=decorationRect.left;
                    v+= decorationRect.top;
                    if (ok != here) {
                        stopMeasure=generateTextElement(textMeasurerInfo,textElementArray,paragraph,line,here, ok, okAscent, okDescent,w,v,decorationRect);
                        v += (okDescent - okAscent +decorationRect.bottom);
                        here = ok;
                        lineCount++;
                        line++;
                    } else if (fit != here) {
                        stopMeasure=generateTextElement(textMeasurerInfo,textElementArray,paragraph,line,here,fit, fitAscent, fitDescent,w,v,decorationRect);
                        v += (okDescent - okAscent+decorationRect.bottom);
                        here = fit;
                        lineCount++;
                        line++;
                    } else {
                        //跳过此行,当前空间不够
                        v += (fitDescent - fitAscent);
                        here = here + 1;
                    }
                    if(lastLine!=line){
                        //行添加,获取下一行偏移
                        getLineOffsets(paragraph,line, decorationRect);
                    }
                    if (here < i) {
                        j = next = here; // must remeasure
                    } else{
                        j = here - 1;    // continue looping
                    }
                    fitAscent = fitDescent = fitTop = fitBottom = 0;
                    okAscent = okDescent = okTop = okBottom = 0;
                    ok = here;
                    w = left+decorationRect.left;
                }
                if(stopMeasure) break;
            }
            if(stopMeasure) break;
        }
        if (!stopMeasure && (end != here||start==end && '\n'==source.charAt(end-1))) {
            //处理断行内容
            v+= decorationRect.top;
            generateTextElement(textMeasurerInfo,textElementArray,paragraph,line,here, end, fitAscent, fitDescent,w,v,decorationRect);
            v += (okDescent - okAscent + decorationRect.bottom);
            lineCount++;
            line++;
        }
        return textElementArray;
    }


    /**
     * 获得当前运算行数
     * @return
     */
    public int getLineCount() {
        return lineCount;
    }
}
