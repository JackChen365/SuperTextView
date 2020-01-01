package com.cz.widget.supertextview.library.layout.measurer;

import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Spanned;
import android.text.TextPaint;

import com.cz.widget.supertextview.library.Styled;
import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.style.ReplacementSpan;
import com.cz.widget.supertextview.library.text.TextElement;
import com.cz.widget.supertextview.library.view.TextParent;

/**
 * 兼容Replacement各种换行标志的文本样式测量器
 * @see
 */
public class TextStyleLineMeasurer extends TextLineMeasurer {
    /**
     * 本次检测过程中Replacement的元素换行标志
     */
    int lineLayoutMode=ReplacementSpan.FLOW;

    public TextStyleLineMeasurer(LineDecoration lineDecoration) {
        super(lineDecoration);
    }

    @Override
    public boolean measureText(TextMeasurerInfo textMeasurerInfo, TextElement[] textElementArray, float[] widths, int paragraph, int line,int start, int next) {
        Paint.FontMetricsInt fm = textMeasurerInfo.fontMetricsInt;
        CharSequence source = textMeasurerInfo.source;
        textMeasurerInfo.paint.getTextWidths(source, start, next, widths);
        textMeasurerInfo.paint.getFontMetricsInt(fm);
        return false;
    }

    @Override
    public boolean measureStyled(TextParent textParent,TextMeasurerInfo textMeasurerInfo,TextElement[] textElementArray, float[] widths, int paragraph, int line, int i, int next) {
        boolean stop=false;
        Paint.FontMetricsInt fm = textMeasurerInfo.fontMetricsInt;
        CharSequence source = textMeasurerInfo.source;
        Spanned spanned = textMeasurerInfo.spanned;
        TextPaint paint = textMeasurerInfo.paint;
        TextPaint workPaint = textMeasurerInfo.workPaint;
        int outerWidth = textMeasurerInfo.outerWidth;
        ReplacementSpan replacementSpan = findReplacementSpan(spanned,i, next);
        if(null!=replacementSpan){
            lineLayoutMode=replacementSpan.getSpanLayoutMode();
            //测量viewSpan
            measureViewSpan(textParent,replacementSpan);
            // 如果当前大小排版超出范围,则换到下一行
            int replacementSpanWidth = replacementSpan.getSize(paint, source.subSequence(i,next), i, next, fm);
            //换行条件
            //1. 元素设置为换行
            //2. 元素设置为超出宽度换行
            //3. 段落内,且长度高于段落换行
            if(ReplacementSpan.isBreakLine(lineLayoutMode)||
                    ReplacementSpan.considerBreakLine(lineLayoutMode)&&(w+replacementSpanWidth)>outerWidth){
                //输出上一行信息
                v+=decorationRect.top;
                stop=generateTextElement(textMeasurerInfo,textElementArray,paragraph,line,here, i, fitAscent, fitDescent,w, v,decorationRect);
                v += (fitDescent - fitAscent + decorationRect.bottom);
                //段落宽度/高度超出,切换到章节段落下
                if(ReplacementSpan.considerBreakLine(lineLayoutMode)&&(w+replacementSpanWidth)>outerWidth){
                    w = decorationRect.left;
                    v += decorationRect.bottom;
                    decorationRect.setEmpty();
                }
                fitAscent = fitDescent = fitTop = fitBottom = 0;
                okAscent = okDescent = okTop = okBottom = 0;
                here = i;
                ok = here;
            }
            //排版控件span
            if(ReplacementSpan.isSingleLine(lineLayoutMode)){
                //单行标志,输出行信息
                Styled.getTextWidths(paint, workPaint, spanned, i, next, widths, fm);
                getLineOffsets(paragraph,line, decorationRect);
                v+=decorationRect.top;
                stop=generateTextElement(textMeasurerInfo,textElementArray,paragraph,line,here,next,fm.ascent, fm.descent, w, v,decorationRect);
                layoutViewSpan(replacementSpan, w,v);
            } else {
                layoutViewSpan(replacementSpan, w,v);
                Styled.getTextWidths(paint, workPaint, spanned, i, next, widths, fm);
            }
        } else {
            Styled.getTextWidths(paint, workPaint, spanned, i, next, widths, fm);
        }
        return stop;
    }

    @Override
    public boolean generateTextElement(TextMeasurerInfo textMeasurerInfo,TextElement[] textElementArray,  int paragraph, int line, int start, int end, int above, int below, float x, float y,Rect decorationRect) {
        int off = line;
        textElementArray = idealTextElementArray(textElementArray, off);
        TextElement textElement = TextElement.obtain();
        textElementArray[off]=textElement;
        //检测文本行长度
        outTextElement(textElement, paragraph,line,start, end, above, below, (int) x,y,textMeasurerInfo.align,decorationRect);
        //生成段落时处理，可以决定是否中断整个运算
        return onGenerateTextElement(textElement);
    }

    /**
     * 生成段落信息
     * @param textElement
     * @return
     */
    public boolean onGenerateTextElement(TextElement textElement){
        return false;
    }

    protected void outTextElement(TextElement textLine,int paragraph,int line, int start, int end, int above, int below, int x, float v, int align, Rect decorationRect) {
        //根据不同模式,确定位置
        textLine.setLineStart(start);
        textLine.setLineEnd(end);
        textLine.setLineTop((int) v);
        textLine.setLineDescent(below);

        float lineHeight= (below - above);
        textLine.setLineLeft(x);
        textLine.setLineBottom((int) ((int) v+lineHeight));
        textLine.setLineAlign(align);
        textLine.setParagraph(paragraph);
        textLine.setParagraphLine(line);
        textLine.setLineDecoration(lineDecoration);
        //设置排版矩阵
        textLine.setDecorateRect(decorationRect.left,decorationRect.top,decorationRect.right,decorationRect.bottom);
    }

}
