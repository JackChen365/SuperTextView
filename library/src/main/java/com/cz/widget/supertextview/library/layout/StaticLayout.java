package com.cz.widget.supertextview.library.layout;

import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Spanned;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.cz.widget.supertextview.library.Styled;
import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.paragraph.ParagraphLayoutInfo;
import com.cz.widget.supertextview.library.render.TextRender;
import com.cz.widget.supertextview.library.span.ViewSpan;
import com.cz.widget.supertextview.library.style.MetricAffectingSpan;
import com.cz.widget.supertextview.library.style.ReplacementSpan;
import com.cz.widget.supertextview.library.text.TextParagraph;
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
    private static final Rect decorationTmpRect =new Rect();

    private int lineCount;
    private int columns;

    private int[] lines;
    private char[] charArrays;
    private float[] widths;
    private Paint.FontMetricsInt fontMetricsInt;
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
        columns = COLUMNS_NORMAL;
        fontMetricsInt = new Paint.FontMetricsInt();
        lines = new int[ArrayUtils.idealIntArraySize(2 * columns)];
        generate(source, 0,  source.length(), paint, width, spacingAdd,align);
        charArrays = null;
        widths = null;
        fontMetricsInt = null;
    }

    void generate(CharSequence source, int bufferStart, int bufferEnd,
                  TextPaint paint, int outerWidth,float spacingAdd,int align) {
        lineCount = 0;
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
        int paragraph=0;
        int paragraphLine=0;
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
            //        setReplacementSpanWord(source,start,end,chs);
            CharSequence sub = source;

            int width = firstWidth;
            int lineLayoutMode = ReplacementSpan.FLOW;
            //段落排版信息
            ParagraphLayoutInfo paragraphLayoutInfo = new ParagraphLayoutInfo();
            //获得行偏移
            lineDecoration.getLineOffsets(paragraph, paragraphLine, decorationTmpRect);
            float w = decorationTmpRect.left;
            int v = 0;
            int here = start;

            int ok = start;
            int okascent = 0, okdescent = 0, oktop = 0, okbottom = 0;

            int fit = start;
            int fitascent = 0, fitdescent = 0, fittop = 0, fitbottom = 0;
            int next = 0;
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
                    if (null != replacementSpan) {
                        lineLayoutMode = replacementSpan.getSpanLayoutMode();
                        // 如果当前大小排版超出范围,则换到下一行
                        int replacementSpanWidth = replacementSpan.getSize(paint, source.subSequence(i, next), i, next, paragraphLayoutInfo.paragraphFontMetrics);
                        int replacementSpanHeight = paragraphLayoutInfo.paragraphFontMetrics.bottom - paragraphLayoutInfo.paragraphFontMetrics.top;
                        int decorationHeight = decorationTmpRect.top + decorationTmpRect.bottom;
                        //换行条件
                        //1. 元素设置为换行
                        //2. 元素设置为超出宽度换行
                        //3. 段落内,且长度高于段落换行
                        if (ReplacementSpan.isBreakLine(lineLayoutMode) ||
                                ReplacementSpan.considerBreakLine(lineLayoutMode) && (w + replacementSpanWidth) > outerWidth ||
                                paragraphLayoutInfo.inParagraph && (v + decorationHeight + replacementSpanHeight) > paragraphLayoutInfo.paragraphHeight) {
                            if (paragraphLayoutInfo.inParagraph) {
                                w = decorationTmpRect.left + paragraphLayoutInfo.paragraphLeftOffset;
                            } else {
                                w = decorationTmpRect.left;
                            }
                            //输出上一行信息
                            v += decorationTmpRect.top;
                            out(paragraphLayoutInfo,source, here, i, fitascent, fitdescent, w, v, spacingAdd, align, decorationTmpRect);
                            v += (fitdescent - fitascent  + decorationTmpRect.bottom);
                            //段落宽度/高度超出,切换到章节段落下
                            if (paragraphLayoutInfo.inParagraph && (v + decorationHeight + replacementSpanHeight > paragraphLayoutInfo.paragraphHeight) ||
                                    ReplacementSpan.considerBreakLine(lineLayoutMode) && (w + replacementSpanWidth) > outerWidth) {
                                w = decorationTmpRect.left;
                                v += (paragraphLayoutInfo.paragraphHeight - v) + decorationTmpRect.bottom;
                                paragraphLayoutInfo.reset();
                                decorationTmpRect.setEmpty();
                            }
                            fitascent = fitdescent = fittop = fitbottom = 0;
                            okascent = okdescent = oktop = okbottom = 0;
                            here = i;
                            ok = here;
                        }
                        //排版控件span
                        if (ReplacementSpan.isParagraph(lineLayoutMode)) {
                            //如果当前行信息，加上控件宽度超出，输出上一行信息
                            if (here != fit && w + replacementSpanWidth + decorationTmpRect.left + decorationTmpRect.right > outerWidth) {
                                float left = decorationTmpRect.left + paragraphLayoutInfo.paragraphLeftOffset;
                                v += decorationTmpRect.top;
                                out(paragraphLayoutInfo,source, here, i, fitascent, fitdescent, left, v, spacingAdd, align, decorationTmpRect);
                                v += (fitdescent - fitascent  + decorationTmpRect.bottom);
                                lineDecoration.getLineOffsets(paragraph, paragraphLine, decorationTmpRect);
                                w = decorationTmpRect.left;
                                here = i;
                            }
                            //计算段落信息的FontMetrics对象
                            Styled.getTextWidths(paint, workPaint, spanned, i, next, widths, paragraphLayoutInfo.paragraphFontMetrics);
                            Paint.FontMetricsInt paragraphFontMetrics = paragraphLayoutInfo.paragraphFontMetrics;
                            //如果不为空代表嵌套段落
                            if (!paragraphLayoutInfo.inParagraph) {
                                lineDecoration.getLineOffsets(paragraph, paragraphLine, decorationTmpRect);
                                w += decorationTmpRect.left;
                                v += decorationTmpRect.top;
                            }
                            //运算段落边距
                            //如果当前行信息，加上控件宽度超出没有超出，将此行输出为段落内
                            if (here != fit) {
                                float left = decorationTmpRect.left + paragraphLayoutInfo.paragraphLeftOffset;
                                out(paragraphLayoutInfo,source, here, i, fitascent, fitdescent, left, v, spacingAdd, align, decorationTmpRect);
                                here = i;
                            }
                            //输出行信息
                            out(paragraphLayoutInfo,source, here, next, paragraphFontMetrics.ascent, paragraphFontMetrics.descent, w, v, spacingAdd, align, decorationTmpRect);
                            layoutViewSpan(replacementSpan, w, v);
                            here = next;
                            ok = here;
                        } else {
                            layoutViewSpan(replacementSpan, w, v);
                            Styled.getTextWidths(paint, workPaint, spanned, i, next, widths, fm);
                        }
                    } else {
                        Styled.getTextWidths(paint, workPaint, spanned, i, next, widths, fm);
                        //检测段落内常规元素超出
                        int decorationHeight = decorationTmpRect.top + decorationTmpRect.bottom;
                        if (paragraphLayoutInfo.inParagraph && (v + decorationHeight + (fm.descent - fm.ascent) > paragraphLayoutInfo.paragraphHeight)) {
                            //这里最后一行,某一个元素高度超出,所以回退
                            w -= paragraphLayoutInfo.paragraphLeftOffset;
                            v += (paragraphLayoutInfo.paragraphHeight - v) + decorationTmpRect.bottom;
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
                    if (w <= width - decorationTmpRect.right) {
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
                        float x = decorationTmpRect.left + paragraphLayoutInfo.paragraphLeftOffset;
                        v += decorationTmpRect.top;
                        if (ok != here) {
                            //这里是一行
                            out(paragraphLayoutInfo,source, here, ok, okascent, okdescent, x, v, spacingAdd, align, decorationTmpRect);
                            v += (okdescent - okascent + decorationTmpRect.bottom);
                            if (paragraphLayoutInfo.inParagraph) {
//                                lineDecoration.getParagraphLineOffsets(paragraph.textParagraph, paragraph.lineCount, decorationTmpRect);
                            } else {
                                lineDecoration.getLineOffsets(paragraph, paragraphLine, decorationTmpRect);
                            }
                            here = ok;
                        } else if (fit != here) {
                            out(paragraphLayoutInfo,source, here, fit, fitascent, fitdescent, x, v, spacingAdd, align, decorationTmpRect);
                            v += (fitdescent - fitascent + spacingAdd + decorationTmpRect.bottom);
                            if (paragraphLayoutInfo.inParagraph) {
//                                lineDecoration.getParagraphLineOffsets(paragraph.textParagraph, paragraph.lineCount, decorationTmpRect);
                            } else {
                                lineDecoration.getLineOffsets(paragraph, paragraphLine, decorationTmpRect);
                            }
                            here = fit;
                        } else {
                            //跳过此行,当前空间不够
                            if (paragraphLayoutInfo.inParagraph) {
                                v += (paragraphLayoutInfo.paragraphHeight - v);
                            } else {
                                v += (fitdescent - fitascent + spacingAdd + decorationTmpRect.bottom);
                            }
                            here = here + 1;
                        }
                        if (here < i) {
                            j = next = here; // must remeasure
                        } else {
                            j = here - 1;    // continue looping
                        }
                        fitascent = fitdescent = fittop = fitbottom = 0;
                        okascent = okdescent = oktop = okbottom = 0;
                        ok = here;
                        w = decorationTmpRect.left;
                        if (paragraphLayoutInfo.inParagraph) {
                            //段落结束
                            w = decorationTmpRect.left + paragraphLayoutInfo.paragraphLeftOffset;
                            int decorationHeight = decorationTmpRect.top + decorationTmpRect.bottom;
                            if (v + decorationHeight + (fmDescent - fmAscent) + spacingAdd >= paragraphLayoutInfo.paragraphHeight) {
                                //段落清除，重新运算行信息
                                v += (paragraphLayoutInfo.paragraphHeight - v) + decorationTmpRect.bottom;
                                lineDecoration.getLineOffsets(paragraph, paragraphLine, decorationTmpRect);
                                //清除数据
                                w = decorationTmpRect.left;
                                paragraphLayoutInfo.reset();
                            }
                        }
                    }
                }
                //设置段落信息
                if (ReplacementSpan.isParagraph(lineLayoutMode)) {
                    //初始化段落信息
                    paragraphLayoutInfo.inParagraph = true;
                    paragraphLayoutInfo.paragraphLeftOffset = w;
                    paragraphLayoutInfo.paragraphHeight = v + (paragraphLayoutInfo.paragraphFontMetrics.descent - paragraphLayoutInfo.paragraphFontMetrics.ascent);
                    lineLayoutMode = ReplacementSpan.FLOW;
                }
            }
            if (end != here) {
                //处理断行内容
                float x = !paragraphLayoutInfo.inParagraph
                        ? 0 + decorationTmpRect.left : paragraphLayoutInfo.paragraphLeftOffset + decorationTmpRect.left;
                v += decorationTmpRect.top;
                out(paragraphLayoutInfo,source, here, end, fitascent, fitdescent, x, v, spacingAdd, align, decorationTmpRect);
            } else if (start == end && '\n' == source.charAt(end - 1)) {
                //最后以换行结尾
                v += decorationTmpRect.top;
                out(paragraphLayoutInfo,source, here, end, fm.ascent, fm.descent, 0, v, spacingAdd, align, decorationTmpRect);
            }
            if (end == bufferEnd)
                break;
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
    private void layoutViewSpan(ReplacementSpan replacementSpan,float left,float top) {
        //排版view
        if(null!=replacementSpan&&replacementSpan instanceof ViewSpan){
            ViewSpan viewSpan = (ViewSpan) replacementSpan;
            viewSpan.setLayoutLeft((int) left);
            viewSpan.setLayoutTop((int) top);
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


    private void out(ParagraphLayoutInfo paragraphLayoutInfo,CharSequence source, int start, int end, int above, int below, float x, float v, float extra, int align,Rect tmpRect) {
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
        //检测该范围内是否有子控件,有的话装载
        attachViewSpan(source,start,end,lines[off + TOP],lines[off + BOTTOM],align);
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
