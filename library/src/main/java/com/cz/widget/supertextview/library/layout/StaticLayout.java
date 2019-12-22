package com.cz.widget.supertextview.library.layout;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;

import com.cz.widget.supertextview.library.Styled;
import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.paragraph.ParagraphLayoutInfo;
import com.cz.widget.supertextview.library.render.TextRender;
import com.cz.widget.supertextview.library.span.ViewSpan;
import com.cz.widget.supertextview.library.style.MetricAffectingSpan;
import com.cz.widget.supertextview.library.style.ReplacementSpan;
import com.cz.widget.supertextview.library.text.Paragraph;
import com.cz.widget.supertextview.library.text.TextLine;
import com.cz.widget.supertextview.library.text.TextParagraph;
import com.cz.widget.supertextview.library.utils.ArrayUtils;
import com.cz.widget.supertextview.library.utils.TextUtilsCompat;

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
        super(source,paint,textRender, width, spacingAdd);
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
        ViewGroup parentView = textRender.getTarget();
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
            Paragraph paragraph = fillParagraphText(paragraphIndex, source, start, end, paint, outerWidth,v, spacingAdd, align);
            for(int i=0;i<paragraph.lineCount;i++){
                //排版子控件
                TextLine textLine = paragraph.textLines[i];
                textLine.layoutViewSpan(parentView,source,0,0);
                textLineList.add(textLine);
            }
            //记录最后一个行底部距离
            if(0 < paragraph.lineCount){
                v = paragraph.textLines[paragraph.lineCount-1].getDecoratedScrollLineBottom();
            }
            if (end == bufferEnd)
                break;
        }
        textLines= textLineList.toArray(new TextLine[textLineList.size()]);
        lineCount=textLines.length;
    }

    /**
     * 填空段落信息
     * @param source
     * @param bufferStart
     * @param bufferEnd
     * @param paint
     * @param outerWidth
     * @param spacingAdd
     * @param align
     * @return
     */
    Paragraph fillParagraphText(int index,CharSequence source, int bufferStart, int bufferEnd,
                                TextPaint paint, int outerWidth, int v,float spacingAdd, int align) {
        Paint.FontMetricsInt fm = fontMetricsInt;
        char[] chs = charArrays;
        float[] widths = this.widths;

        Spanned spanned = null;
        if (source instanceof Spanned)
            spanned = (Spanned) source;
        int start = bufferStart;
        int end = TextUtilsCompat.indexOf(source, '\n', bufferStart, bufferEnd);
        if (end < 0)
            end = bufferEnd;
        int width = outerWidth;
        if (end - start > chs.length) {
            chs = new char[ArrayUtils.idealCharArraySize(end - start)];
            charArrays = chs;
        }
        if ((end - start) * 2 > widths.length) {
            widths = new float[ArrayUtils.idealIntArraySize((end - start) * 2)];
            this.widths = widths;
        }

        TextUtilsCompat.getChars(source, start, end, chs, 0);
//        setReplacementSpanWord(source,start,end,chs);
        CharSequence sub = source;
        int lineLayoutMode=ReplacementSpan.FLOW;
        //段落计算信息
        Paragraph paragraph=new Paragraph(index);
        //段落排版信息
        ParagraphLayoutInfo paragraphLayoutInfo =new ParagraphLayoutInfo();
        //获得行偏移
        lineDecoration.getLineOffsets(paragraph.index,paragraph.lineCount, decorationTmpRect);
        float w = decorationTmpRect.left;
        int here = start;

        int ok = start;
        int okascent = 0, okdescent = 0, oktop = 0, okbottom = 0;

        int fit = start;
        int fitascent = 0, fitdescent = 0, fittop = 0, fitbottom = 0;
        int next=0;
        for (int i = start; i < end; i = next) {
            if (spanned == null)
                next = end;
            else {
                next = spanned.nextSpanTransition(i, end, MetricAffectingSpan.class);
            }
            if (spanned == null) {
                paint.getTextWidths(sub, i, next, widths);
                System.arraycopy(widths, 0, widths, end - start + (i - start), next - i);
                paint.getFontMetricsInt(fm);
            } else {
                workPaint.baselineShift = 0;
                ReplacementSpan replacementSpan = findReplacementSpan(i, next);
                if(null!=replacementSpan){
                    lineLayoutMode=replacementSpan.getSpanLayoutMode();
                    //测量viewSpan
//                    measureViewSpan(replacementSpan);
                    // 如果当前大小排版超出范围,则换到下一行
                    int replacementSpanWidth = replacementSpan.getSize(paint, source.subSequence(i,next), i, next, paragraphLayoutInfo.paragraphFontMetrics);
                    int replacementSpanHeight = paragraphLayoutInfo.paragraphFontMetrics.bottom - paragraphLayoutInfo.paragraphFontMetrics.top;
                    int decorationHeight= decorationTmpRect.top+decorationTmpRect.bottom;
                    //换行条件
                    //1. 元素设置为换行
                    //2. 元素设置为超出宽度换行
                    //3. 段落内,且长度高于段落换行
                    if(ReplacementSpan.isBreakLine(lineLayoutMode)||
                            ReplacementSpan.considerBreakLine(lineLayoutMode)&&(w+replacementSpanWidth)>outerWidth||
                            paragraphLayoutInfo.inParagraph&&(v+decorationHeight+replacementSpanHeight)> paragraphLayoutInfo.paragraphHeight){
                        if(paragraphLayoutInfo.inParagraph){
                            w = decorationTmpRect.left+paragraphLayoutInfo.paragraphLeftOffset;
                        } else {
                            w = decorationTmpRect.left;
                        }
                        //输出上一行信息
                        v+=decorationTmpRect.top;
                        outTextLine(paragraph,here, i, fitascent, fitdescent,w, v,spacingAdd,align, decorationTmpRect,true);
                        v += (fitdescent - fitascent + spacingAdd+ decorationTmpRect.bottom);
                        //段落宽度/高度超出,切换到章节段落下
                        if(paragraphLayoutInfo.inParagraph && (v+decorationHeight+replacementSpanHeight> paragraphLayoutInfo.paragraphHeight) ||
                                ReplacementSpan.considerBreakLine(lineLayoutMode)&&(w+replacementSpanWidth)>outerWidth){
                            w = decorationTmpRect.left;
                            v += (paragraphLayoutInfo.paragraphHeight-v)+decorationTmpRect.bottom;
                            paragraph.textParagraph=null;
                            paragraphLayoutInfo.reset();
                            decorationTmpRect.setEmpty();
                        }
                        fitascent = fitdescent = fittop = fitbottom = 0;
                        okascent = okdescent = oktop = okbottom = 0;
                        here = i;
                        ok = here;
                    }
                    //排版控件span
                    if(ReplacementSpan.isParagraph(lineLayoutMode)){
                        //如果当前行信息，加上控件宽度超出，输出上一行信息
                        if(here != fit && w+replacementSpanWidth+decorationTmpRect.left+decorationTmpRect.right>outerWidth){
                            float left=decorationTmpRect.left+paragraphLayoutInfo.paragraphLeftOffset;
                            v+= decorationTmpRect.top;
                            outTextLine(paragraph,here, i, fitascent, fitdescent, left, v, spacingAdd,align, decorationTmpRect,true);
                            v += (fitdescent - fitascent + spacingAdd + decorationTmpRect.bottom);
                            lineDecoration.getLineOffsets(paragraph.index,paragraph.lineCount, decorationTmpRect);
                            w = decorationTmpRect.left;
                            here = i;
                        }
                        //计算段落信息的FontMetrics对象
                        Styled.getTextWidths(paint, workPaint, spanned, i, next, widths, paragraphLayoutInfo.paragraphFontMetrics);
                        Paint.FontMetricsInt paragraphFontMetrics = paragraphLayoutInfo.paragraphFontMetrics;
                        //检测数组长度
                        paragraph.textLines = idealTextLinesArray(paragraph.textLines, paragraph.lineCount+1);
                        //如果不为空代表嵌套段落
                        if(null==paragraph.textParagraph){
                            lineDecoration.getLineOffsets(paragraph.index,paragraph.lineCount, decorationTmpRect);
                            TextParagraph textParagraph=new TextParagraph();
                            v+=decorationTmpRect.top;
                            textParagraph.setLineTop(v);
                            float lineHeight= (paragraphFontMetrics.descent - paragraphFontMetrics.ascent);
                            textParagraph.setLineDescent(paragraphFontMetrics.descent);
                            textParagraph.setLineBottom((int) (v+lineHeight));
                            textParagraph.setParagraph(paragraph.index);
                            textParagraph.setParagraphLine(paragraph.lineCount);
                            textParagraph.setRect(decorationTmpRect.left,decorationTmpRect.top,decorationTmpRect.right,decorationTmpRect.bottom);
                            //段落内部行，不需要添加外围装饰尺寸
                            v-=decorationTmpRect.top;
                            //记录初始段落信息
                            paragraph.textParagraph=textParagraph;
                            paragraph.textLines[paragraph.lineCount]=textParagraph;
                            paragraph.lineCount++;
                        }
                        //运算段落边距
                        //如果当前行信息，加上控件宽度超出没有超出，将此行输出为段落内
                        if(here != fit){
                            float left=decorationTmpRect.left+paragraphLayoutInfo.paragraphLeftOffset;
                            outTextLine(paragraph,here, i, fitascent, fitdescent, left, v, spacingAdd,align, decorationTmpRect,false);
                            here = i;
                        }
                        //输出行信息
                        outTextLine(paragraph,here, next, paragraphFontMetrics.ascent, paragraphFontMetrics.descent, w, v,spacingAdd,align, decorationTmpRect,false);
                        layoutViewSpan(paragraph,replacementSpan, w,v);
                        //开始分隔段落信息
                        v+=decorationTmpRect.top;
                        lineDecoration.getParagraphLineOffsets(paragraph.textParagraph,paragraph.lineCount, decorationTmpRect);
                        here = next;
                        ok = here;
                    } else if(ReplacementSpan.isSingleLine(lineLayoutMode)){
                        //单行标志,输出行信息
                        Styled.getTextWidths(paint, workPaint, spanned, i, next, widths, fm);
                        lineDecoration.getLineOffsets(paragraph.index,paragraph.lineCount, decorationTmpRect);
                        v+=decorationTmpRect.top;
                        outTextLine(paragraph,here, next, fm.ascent, fm.descent, w, v,spacingAdd,align, decorationTmpRect,false);
                        layoutViewSpan(paragraph,replacementSpan, w,v);
                    } else {
                        layoutViewSpan(paragraph,replacementSpan, w,v);
                        Styled.getTextWidths(paint, workPaint, spanned, i, next, widths, fm);
                    }
                } else {
                    Styled.getTextWidths(paint, workPaint, spanned, i, next, widths, fm);
                    //检测段落内常规元素超出
                    int decorationHeight= decorationTmpRect.top+decorationTmpRect.bottom;
                    if(paragraphLayoutInfo.inParagraph && (v+decorationHeight+(fm.descent-fm.ascent)> paragraphLayoutInfo.paragraphHeight)){
                        //这里最后一行,某一个元素高度超出,所以回退
                        w -= paragraphLayoutInfo.paragraphLeftOffset;
                        v += (paragraphLayoutInfo.paragraphHeight-v)+decorationTmpRect.bottom;
                        paragraph.textParagraph=null;
                        paragraphLayoutInfo.reset();
                        decorationTmpRect.setEmpty();
                    }
                }
                System.arraycopy(widths, 0, widths, end - start + (i - start), next - i);
                if (workPaint.baselineShift < 0) {
                    fm.ascent += workPaint.baselineShift;
                    fm.top += workPaint.baselineShift;
                } else {
                    fm.descent += workPaint.baselineShift;
                    fm.bottom += workPaint.baselineShift;
                }
            }
            int fmTop = fm.top;
            int fmBottom = fm.bottom;
            int fmAscent = fm.ascent;
            int fmDescent = fm.descent;
            for (int j = i; j < next; j++) {
                char c = chs[j - start];
                if (c == '\n') {
                    ;
                } else {
                    w += widths[j - start + (end - start)];
                }
                if (w <= width-decorationTmpRect.right) {
                    fit = j + 1;

                    if (fmTop < fittop)
                        fittop = fmTop;
                    if (fmAscent < fitascent)
                        fitascent = fmAscent;
                    if (fmDescent > fitdescent)
                        fitdescent = fmDescent;
                    if (fmBottom > fitbottom)
                        fitbottom = fmBottom;
                    if (c == ' ' || c == '\t' || c == '\uFFFC' ||
                            ((c == '.' || c == ',' || c == ':' || c == ';') &&
                                    (j - 1 < here || !Character.isDigit(chs[j - 1 - start])) &&
                                    (j + 1 >= next || !Character.isDigit(chs[j + 1 - start]))) ||
                            ((c == '/' || c == '-') &&
                                    (j + 1 >= next || !Character.isDigit(chs[j + 1 - start])))) {
                        ok = j + 1;

                        if (fittop < oktop)
                            oktop = fittop;
                        if (fitascent < okascent)
                            okascent = fitascent;
                        if (fitdescent > okdescent)
                            okdescent = fitdescent;
                        if (fitbottom > okbottom)
                            okbottom = fitbottom;
                    }
                } else {
                    //如果在段落内,高度
                    float x=decorationTmpRect.left+paragraphLayoutInfo.paragraphLeftOffset;
                    v+= decorationTmpRect.top;
                    if (ok != here) {
                        //这里是一行
                        outTextLine(paragraph,here, ok, okascent, okdescent,x,v,spacingAdd,align, decorationTmpRect,true);
                        v += (okdescent - okascent + spacingAdd + decorationTmpRect.bottom);
                        if(null!=paragraph.textParagraph){
                            lineDecoration.getParagraphLineOffsets(paragraph.textParagraph,paragraph.lineCount, decorationTmpRect);
                        } else {
                            lineDecoration.getLineOffsets(paragraph.index,paragraph.lineCount, decorationTmpRect);
                        }
                        here = ok;
                    } else if (fit != here) {
                        outTextLine(paragraph,here, fit, fitascent, fitdescent,x,v,spacingAdd,align, decorationTmpRect,true);
                        v += (fitdescent - fitascent + spacingAdd + decorationTmpRect.bottom);
                        if(null!=paragraph.textParagraph){
                            lineDecoration.getParagraphLineOffsets(paragraph.textParagraph,paragraph.lineCount, decorationTmpRect);
                        } else {
                            lineDecoration.getLineOffsets(paragraph.index,paragraph.lineCount, decorationTmpRect);
                        }
                        here = fit;
                    } else {
                        //跳过此行,当前空间不够
                        if(paragraphLayoutInfo.inParagraph){
                            v += (paragraphLayoutInfo.paragraphHeight-v);
                        } else {
                            v += (fitdescent - fitascent + spacingAdd + decorationTmpRect.bottom);
                        }
                        here = here + 1;
                    }
                    if (here < i) {
                        j = next = here; // must remeasure
                    } else{
                        j = here - 1;    // continue looping
                    }
                    fitascent = fitdescent = fittop = fitbottom = 0;
                    okascent = okdescent = oktop = okbottom = 0;
                    ok = here;
                    w = decorationTmpRect.left;
                    if(paragraphLayoutInfo.inParagraph){
                        //段落结束
                        w = decorationTmpRect.left+paragraphLayoutInfo.paragraphLeftOffset;
                        int decorationHeight= decorationTmpRect.top+decorationTmpRect.bottom;
                        if(v+decorationHeight+(fmDescent-fmAscent)+spacingAdd>= paragraphLayoutInfo.paragraphHeight){
                            //段落清除，重新运算行信息
                            lineDecoration.getLineOffsets(paragraph.index,paragraph.lineCount-1, decorationTmpRect);
                            v+=(paragraphLayoutInfo.paragraphHeight-v)+decorationTmpRect.bottom;
                            lineDecoration.getLineOffsets(paragraph.index,paragraph.lineCount, decorationTmpRect);
                            //清除数据
                            w=decorationTmpRect.left;
                            paragraph.textParagraph=null;
                            paragraphLayoutInfo.reset();
                        }
                    }
                }
            }
            //设置段落信息
            if(ReplacementSpan.isParagraph(lineLayoutMode)){
                //初始化段落信息
                paragraphLayoutInfo.inParagraph=true;
                paragraphLayoutInfo.paragraphLeftOffset=w;
                paragraphLayoutInfo.paragraphHeight=v+(paragraphLayoutInfo.paragraphFontMetrics.descent- paragraphLayoutInfo.paragraphFontMetrics.ascent);
                lineLayoutMode = ReplacementSpan.FLOW;
                w+=decorationTmpRect.left;
            }
            if(ReplacementSpan.isSingleLine(lineLayoutMode)){
                lineDecoration.getLineOffsets(paragraph.index,paragraph.lineCount, decorationTmpRect);
                w = decorationTmpRect.left+paragraphLayoutInfo.paragraphLeftOffset;
                v += (fm.descent - fm.ascent + spacingAdd + decorationTmpRect.bottom);
                lineLayoutMode = ReplacementSpan.FLOW;
                fitascent = fitdescent = fittop = fitbottom = 0;
                okascent = okdescent = oktop = okbottom = 0;
                here = next;
                ok = here;
            }
        }
        if (end != here) {
            //处理断行内容
            float x = !paragraphLayoutInfo.inParagraph
                    ? 0+ decorationTmpRect.left : paragraphLayoutInfo.paragraphLeftOffset+ decorationTmpRect.left;
            v+= decorationTmpRect.top;
            outTextLine(paragraph,here, end, fitascent, fitdescent,x, v,spacingAdd,align, decorationTmpRect,true);
        } else if(start==end&&'\n'==source.charAt(end-1)){
            //最后以换行结尾
            v+= decorationTmpRect.top;
            outTextLine(paragraph,here, end, fm.ascent, fm.descent,0, v,spacingAdd,align, decorationTmpRect,true);
        }
        paragraph.textParagraph=null;
        decorationTmpRect.setEmpty();
        return paragraph;
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

    /**
     * 排版ViewSpan
     * @param replacementSpan
     * @param left
     * @param top
     */
    private void layoutViewSpan(Paragraph paragraph,ReplacementSpan replacementSpan,float left,float top) {
        //排版view
        if(null!=replacementSpan&&replacementSpan instanceof ViewSpan){
            float layoutTop=0;
            if(null!=paragraph.textParagraph){
                layoutTop=top-paragraph.textParagraph.getLineTop();
            }
            ViewSpan viewSpan = (ViewSpan) replacementSpan;
            viewSpan.setLayoutLeft((int) left);
            viewSpan.setLayoutTop((int) layoutTop);
        }
    }

    /**
     *  装载ViewSpan
     */
    private void attachViewSpan(CharSequence source, int start, int end, int top, int bottom, int align) {
        //排版view
        if(source instanceof Spanned){
            Spanned spanned = (Spanned) source;
            ViewSpan[] viewSpans = spanned.getSpans(start, end, ViewSpan.class);
            for(int i=0;i<viewSpans.length;i++){
                ViewSpan viewSpan = viewSpans[i];
                //根据方向重置排版top偏移
                int viewHeight = viewSpan.getHeight();
                if(Gravity.TOP==align){
                    viewSpan.setLayoutTop(top);
                } else if(Gravity.CENTER==align){
                    int lineHeight=bottom-top;
                    viewSpan.setLayoutTop(top+(lineHeight-viewHeight)/2);
                } else if(Gravity.BOTTOM==align){
                    viewSpan.setLayoutTop(top+bottom-viewHeight);
                }
                viewSpan.attachToView();
            }
        }
    }


    /**
     * 输出行信息
     * @param paragraph
     * @param start
     * @param end
     * @param above
     * @param below
     * @param x
     * @param v
     * @param extra
     * @param align
     * @param isBreakLine
     */
    private void outTextLine(Paragraph paragraph, int start, int end, int above, int below,
                             float x, float v, float extra, int align, Rect tmpRect, boolean isBreakLine) {
        if(null!=paragraph.textParagraph){
            //处在一个段落内
            TextParagraph textParagraph = paragraph.textParagraph;
            float y=v-textParagraph.getLineTop()+decorationTmpRect.top;
            textParagraph.setLineDecoration(lineDecoration);
            textParagraph.textLines = out(paragraph,textParagraph.textLines,textParagraph.lineCount, start, end, above, below, (int) x,y, extra, align,tmpRect,isBreakLine);
            textParagraph.lineCount++;
        } else {
            //输出自由行信息
            paragraph.textParagraph=null;
            paragraph.textLines = out(paragraph,paragraph.textLines,paragraph.lineCount, start, end, above, below, (int) x,v, extra, align,tmpRect,isBreakLine);
            paragraph.lineCount++;
        }
    }

    private TextLine[] out(Paragraph paragraph, TextLine[] textLines, int index, int start, int end, int above, int below, int x, float v, float extra, int align, Rect rect, boolean isBreakLine) {
        int off = index;
        //检测文本行长度
        textLines = idealTextLinesArray(textLines, off);
        //根据不同模式,确定位置
        textLines[off]=TextLine.obtain();
        textLines[off].setLineStart(start);
        textLines[off].setLineEnd(end);
        textLines[off].setLineTop((int) v);
        textLines[off].setLineDescent((int) (below + extra));

        float lineHeight= (below - above) + extra;
        textLines[off].setLineLeft(x);
        textLines[off].setLineBottom((int) ((int) v+lineHeight));
        textLines[off].setLineAlign(align);
        textLines[off].setParagraph(paragraph.index);
        textLines[off].setParagraphLine(paragraph.lineCount);
        textLines[off].setLineDecoration(lineDecoration);
        //设置排版矩阵
        textLines[off].setRect(rect.left,rect.top,rect.right,rect.bottom);
        textLines[off].setBreakLine(isBreakLine);
        return textLines;
    }


    private TextLine[] idealTextLinesArray(TextLine[] textLines, int off) {
        int want = off+1;
        if(null==textLines){
            int nlen = ArrayUtils.idealIntArraySize(want + 1);
            textLines = new TextLine[nlen];
        } else if (want >= textLines.length) {
            int nlen = ArrayUtils.idealIntArraySize(want + 1);
            TextLine[] grow = new TextLine[nlen];
            System.arraycopy(textLines, 0, grow, 0, textLines.length);
            textLines = grow;
        }
        return textLines;
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
