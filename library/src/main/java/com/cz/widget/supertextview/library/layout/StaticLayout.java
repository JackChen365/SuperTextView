package com.cz.widget.supertextview.library.layout;

import android.graphics.Paint;
import android.text.Spanned;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;

import com.cz.widget.supertextview.library.Styled;
import com.cz.widget.supertextview.library.paragraph.ParagraphLayoutInfo;
import com.cz.widget.supertextview.library.render.TextRender;
import com.cz.widget.supertextview.library.span.ViewSpan;
import com.cz.widget.supertextview.library.style.MetricAffectingSpan;
import com.cz.widget.supertextview.library.style.ReplacementSpan;
import com.cz.widget.supertextview.library.utils.ArrayUtils;
import com.cz.widget.supertextview.library.utils.TextUtilsCompat;

/**
 * 自定义静态文本排版对象
 * 1. 支持TextView span设计
 * 2. 支持View以span形式存在
 * 3. 支持文本对元素的流式排版
 */
public class StaticLayout extends Layout {
    private static final char FIRST_CJK = '\u2E80';
    private static final String TAG="StaticLayout";
    private static final int COLUMNS_NORMAL = 6;
    private static final int START = 0;
    private static final int TOP = 1;
    private static final int DESCENT = 2;
    private static final int LEFT = 3;//文本起始绘制位置
    private static final int BOTTOM=4;//文本底部位置,因为存在一行内,多行信息
    private static final int ALIGN = 5;//文本对齐标志
    private static final int START_MASK = 0x1FFFFFFF;

    private int lineCount;
    private int columns;

    private int[] lines;
    private char[] charArrays;
    private float[] widths;
    private Paint.FontMetricsInt fontMetricsInt;
    /**
     * @param source      操作文本
     * @param paint      绘制paint
     * @param width      排版宽
     * @param spacingAdd 行额外添加空间
     */
    public StaticLayout(CharSequence source, TextPaint paint, TextRender textRender, int width, float spacingAdd, int align) {
        super(source,paint,textRender, width, spacingAdd);

        columns = COLUMNS_NORMAL;
        fontMetricsInt = new Paint.FontMetricsInt();
        lines = new int[ArrayUtils.idealIntArraySize(2 * columns)];
        generate(source, 0,  source.length(), paint, width, spacingAdd,align);
        charArrays = null;
        widths = null;
        fontMetricsInt = null;
    }

    void generate(CharSequence source, int bufferStart, int bufferEnd,
                  TextPaint paint, int outerWidth,float spacingadd,int align) {
        lineCount = 0;

        float v = 0;

        Paint.FontMetricsInt fm = fontMetricsInt;
        int end = TextUtilsCompat.indexOf(source, '\n', bufferStart, bufferEnd);
        int bufsiz = end >= 0 ? end - bufferStart : bufferEnd - bufferStart;
        boolean first = true;

        if (charArrays == null) {
            charArrays = new char[ArrayUtils.idealCharArraySize(bufsiz + 1)];
            widths = new float[ArrayUtils.idealIntArraySize((bufsiz + 1) * 2)];
        }
        char[] chs = charArrays;
        float[] widths = this.widths;

        Spanned spanned = null;
        if (source instanceof Spanned)
            spanned = (Spanned) source;
        for (int start = bufferStart; start <= bufferEnd; start = end) {
            if (first)
                first = false;
            else
                end = TextUtilsCompat.indexOf(source, '\n', start, bufferEnd);

            if (end < 0)
                end = bufferEnd;
            else
                end++;
            int firstWidth = outerWidth;

            if (end - start > chs.length) {
                chs = new char[ArrayUtils.idealCharArraySize(end - start)];
                charArrays = chs;
            }
            if ((end - start) * 2 > widths.length) {
                widths = new float[ArrayUtils.idealIntArraySize((end - start) * 2)];
                this.widths = widths;
            }

            TextUtilsCompat.getChars(source, start, end, chs, 0);

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
            CharSequence sub = source;

            int width = firstWidth;

            float w = 0;
            int here = start;

            int ok = start;
            int okascent = 0, okdescent = 0, oktop = 0, okbottom = 0;

            int fit = start;
            int fitascent = 0, fitdescent = 0, fittop = 0, fitbottom = 0;
            int next=0;

            int lineLayoutMode=ReplacementSpan.FLOW;
            //段落信息
            ParagraphLayoutInfo paragraphLayoutInfo =new ParagraphLayoutInfo();
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
                        // 如果当前大小排版超出范围,则换到下一行
                        int replacementSpanWidth = replacementSpan.getSize(paint, source.subSequence(i,next), i, next, paragraphLayoutInfo.paragraphFontMetrics);
                        int replacementSpanHeight = paragraphLayoutInfo.paragraphFontMetrics.bottom - paragraphLayoutInfo.paragraphFontMetrics.top;
                        //换行条件
                        //1. 元素设置为换行
                        //2. 元素设置为超出宽度换行
                        //3. 段落内,且长度高于段落换行
                        if(ReplacementSpan.isBreakLine(lineLayoutMode)||
                                ReplacementSpan.considerBreakLine(lineLayoutMode)&&(w+replacementSpanWidth)>outerWidth||
                                paragraphLayoutInfo.inParagraph&&(v+replacementSpanHeight)> paragraphLayoutInfo.paragraphHeight){
                            //检测新的元素是否为段落
                            if(paragraphLayoutInfo.inParagraph){
                                w = paragraphLayoutInfo.paragraphLeftOffset;
                            } else {
                                w = 0;
                            }
                            //输出上一行信息
                            out(paragraphLayoutInfo,here, i-1, fitascent, fitdescent,w, v,spacingadd,align);
                            v += (fitdescent - fitascent + spacingadd);
                            //段落宽度/高度超出,切换到章节段落下
                            if(paragraphLayoutInfo.inParagraph && (v+replacementSpanHeight> paragraphLayoutInfo.paragraphHeight) ||
                                    ReplacementSpan.considerBreakLine(lineLayoutMode)&&(w+replacementSpanWidth)>outerWidth){
                                w = 0;
                                v += (paragraphLayoutInfo.paragraphHeight-v);
                                paragraphLayoutInfo.reset();
                            }
                            fitascent = fitdescent = fittop = fitbottom = 0;
                            okascent = okdescent = oktop = okbottom = 0;
                            here = next-1;
                            ok = here;
                        }
                        layoutViewSpan(replacementSpan, w,v);
                        if(ReplacementSpan.isParagraph(lineLayoutMode)){
                            //计算段落信息的FontMetrics对象
                            Styled.getTextWidths(paint, workPaint, spanned, i, next, widths, paragraphLayoutInfo.paragraphFontMetrics);
                            //输出行信息
                            out(paragraphLayoutInfo,here, i, fitascent, fitdescent,0, v,spacingadd,align);
                            here = next;
                            ok = here;
                        } else {
                            Styled.getTextWidths(paint, workPaint, spanned, i, next, widths, fm);
                        }
                    } else {
                        Styled.getTextWidths(paint, workPaint, spanned, i, next, widths, fm);
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
//                    &&!ReplacementSpan.isBreakLine(lineLayoutMode)
                    if (w <= width) {
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
                        float x= paragraphLayoutInfo.paragraphLeftOffset;
                        if (ok != here) {
                            //这里是一行
                            out(paragraphLayoutInfo,here, ok, okascent, okdescent,x,v,spacingadd,align);
                            v += (okdescent - okascent + spacingadd);
                            here = ok;
                        } else if (fit != here) {
                            out(paragraphLayoutInfo,here, fit, fitascent, fitdescent,x,v,spacingadd,align);
                            v += (fitdescent - fitascent + spacingadd);
                            here = fit;
                        } else {
                            //跳过此行,当前空间不够
                            if(paragraphLayoutInfo.inParagraph){
                                v += (paragraphLayoutInfo.paragraphHeight-v);
                            } else {
                                v += (fitdescent - fitascent + spacingadd);
                            }
                            here+=1;
                        }
                        if (here < i) {
                            j = next = here; // must remeasure
                        } else {
                            j = here - 1;    // continue looping
                        }
                        ok = here;
                        w = 0;
                        fitascent = fitdescent = fittop = fitbottom = 0;
                        okascent = okdescent = oktop = okbottom = 0;
                        if(paragraphLayoutInfo.inParagraph){
                            //段落结束
                            w = paragraphLayoutInfo.paragraphLeftOffset;
                            if(v+(fmDescent-fmAscent)+spacingadd>= paragraphLayoutInfo.paragraphHeight){
                                w=0;
                                v+=(paragraphLayoutInfo.paragraphHeight-v);
                                paragraphLayoutInfo.reset();
                            }
                        }
                    }
                }
                //设置段落信息
                if(ReplacementSpan.isParagraph(lineLayoutMode)){
                    paragraphLayoutInfo.inParagraph=true;
                    paragraphLayoutInfo.paragraphLeftOffset=w;
                    paragraphLayoutInfo.paragraphHeight=v+(paragraphLayoutInfo.paragraphFontMetrics.descent- paragraphLayoutInfo.paragraphFontMetrics.ascent);
                    lineLayoutMode = ReplacementSpan.FLOW;
                }
            }
            //处理最后一行元素信息
            if (end != here) {
                float x = !paragraphLayoutInfo.inParagraph ? 0 : paragraphLayoutInfo.paragraphLeftOffset;
                out(paragraphLayoutInfo,here, end, fitascent, fitdescent,x, v,spacingadd,align);
                v += (fitdescent - fitascent + spacingadd);
            }
            if (end == bufferEnd)
                break;
        }
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
    private void layoutViewSpan(ReplacementSpan replacementSpan,float left,float top) {
        //排版view
        if(replacementSpan instanceof ViewSpan){
            ViewSpan viewSpan = (ViewSpan) replacementSpan;
            View view = viewSpan.getView();
            View parentView = (View)view.getParent();
            int paddingLeft = parentView.getPaddingLeft();
            int paddingTop = parentView.getPaddingTop();
            view.layout((int) (paddingLeft+left),
                    (int)(paddingTop+top),
                    (int) (paddingLeft+left+view.getMeasuredWidth()),
                    (int) (paddingTop+top+view.getMeasuredHeight()));
        }
    }


    private void out(ParagraphLayoutInfo paragraphLayoutInfo, int start, int end, int above, int below, float x, float v, float extra, int align) {
        int j = lineCount;
        int off = j * columns;
        int want = off + columns + TOP;
        int[] lines = this.lines;

        if (want >= lines.length) {
            int nlen = ArrayUtils.idealIntArraySize(want + 1);
            int[] grow = new int[nlen];
            System.arraycopy(lines, 0, grow, 0, lines.length);
            this.lines = grow;
            lines = grow;
        }
        //根据不同模式,确定位置
        lines[off + START] = start;
        lines[off + TOP] = (int)v;
        lines[off + DESCENT] = (int) (below + extra);

        float lineHeight= (below - above) + extra;
        lines[off + LEFT] = (int) x;
        lines[off + BOTTOM] = (int) (v+lineHeight);
        lines[off + ALIGN] = align;

        lines[off + columns + START] = end;
        if(paragraphLayoutInfo.inParagraph){
            lines[off + columns + TOP] = (int) paragraphLayoutInfo.paragraphHeight;
        } else {
            lines[off + columns + TOP] = (int)(v+lineHeight);
        }
        int lineLatterStart = getLineLatterStart(lineCount);
        int lineLatterEnd = getLineLatterEnd(lineCount);
        int lineStart = getLineStart(lineCount);
        int lineTop = getDecoratedScrollLineTop(lineCount);
        int lineBottom = getDecoratedScrollLineBottom(lineCount);
        Log.e(TAG,"lineCount:"+lineCount+" lineLatterStart:"+lineLatterStart+" lineLatterEnd:"+lineLatterEnd+" lineStart:"+lineStart+" lineTop:"+lineTop+" lineBottom:"+(lineBottom));
        lineCount++;
    }


    public int getLineCount() {
        return lineCount;
    }

    public int getDecoratedScrollLineTop(int line) {
        return lines[columns * line + TOP];
    }

    @Override
    public int getDecoratedScrollLineBottom(int line) {
        return lines[columns * line + BOTTOM];
    }

    public int getDecoratedDescent(int line) {
        return lines[columns * line + DESCENT];
    }

    @Override
    public int getLineStart(int line) {
        return lines[columns * line + LEFT];
    }

    @Override
    public int getLineGravity(int line) {
        return lines[columns * line + ALIGN];
    }

    public int getLineLatterStart(int line) {
        return lines[columns * line + START] & START_MASK;
    }

    @Override
    public int getLineLatterEnd(int line) {
        return getLineLatterStart(line+1);
    }

}
