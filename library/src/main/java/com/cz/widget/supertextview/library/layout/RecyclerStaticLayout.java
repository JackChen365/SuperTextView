package com.cz.widget.supertextview.library.layout;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Spanned;
import android.text.TextPaint;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;

import com.cz.widget.supertextview.library.Styled;
import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.paragraph.ParagraphLayoutInfo;
import com.cz.widget.supertextview.library.render.TextRender;
import com.cz.widget.supertextview.library.span.ViewSpan;
import com.cz.widget.supertextview.library.style.MetricAffectingSpan;
import com.cz.widget.supertextview.library.style.ReplacementSpan;
import com.cz.widget.supertextview.library.text.TextLine;
import com.cz.widget.supertextview.library.text.TextParagraph;
import com.cz.widget.supertextview.library.utils.ArrayUtils;
import com.cz.widget.supertextview.library.utils.TextUtilsCompat;

import java.util.Arrays;

/**
 * 自定义静态文本排版对象
 * 1. 支持TextView span设计
 * 2. 支持View以span形式存在
 * 3. 支持文本对元素的流式排版
 *
 * 强化功能
 * 1. 优化排版运算(基于段落的运算信息
 * 2. 优化渲染
 */
public class RecyclerStaticLayout extends Layout {
    private static final String TAG="RecyclerStaticLayout";
    /**
     * 滚动向前方向
     */
    private static final int  DIRECTION_START = -1;
    /**
     * 滚动向后方向
     */
    private static final int  DIRECTION_END = 1;
    private static final Rect decorationTmpRect =new Rect();
    //段落信息
    private int lineCount;
    private CharSequence source;
    private TextPaint paint;
    private int width;
    private int height;
    private float spacingAdd;
    private int textAlign;

    //当前展示行信息
    private TextLine[] textLines;
    //段落回收信息
    private ParagraphRecyclerPool recyclerPool=new ParagraphRecyclerPool();
    private char[] charArrays;
    private float[] widths;
    private Paint.FontMetricsInt fontMetricsInt;
    //行装饰器
    private LineDecoration lineDecoration;
    private TextRender textRender;
    /**
     * 动态排版状态对象
     */
    private LayoutState layoutState=new LayoutState();
    /**
     * 纵向滚动偏移位置
     */
    private int scrollY;
    /**
     * @param source      操作文本
     * @param paint      绘制paint
     * @param width      排版宽
     * @param spacingAdd 行额外添加空间
     */
    public RecyclerStaticLayout(CharSequence source, TextPaint paint, LineDecoration lineDecoration,
                                TextRender textRender,int width, int height,float spacingAdd, int textAlign) {
        super(source, paint,textRender, width, spacingAdd);
        this.lineCount = 0;
        this.source=source;
        this.paint=paint;
        this.width=width;
        this.height=height;
        this.spacingAdd=spacingAdd;
        this.textAlign = textAlign;
        this.textRender=textRender;
        this.lineDecoration=lineDecoration;
        this.fontMetricsInt = new Paint.FontMetricsInt();
        this.textLines = new TextLine[3];
        int end = TextUtilsCompat.indexOf(source, '\n', 0, source.length());
        int bufferSize = end >= 0 ? end: source.length() - end;
        if (charArrays == null) {
            charArrays = new char[ArrayUtils.idealCharArraySize(bufferSize + 1)];
            widths = new float[ArrayUtils.idealIntArraySize((bufferSize + 1) * 2)];
        }
        //初始化LayoutState状态
        updateLayoutStateFromEnd(height);
        //填空文本
        fillText(height);
    }

    /**
     * 设置行装饰器
     * @param lineDecoration
     */
    public void setLineDecoration(LineDecoration lineDecoration){
        this.lineDecoration=lineDecoration;
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
                           TextPaint paint, int outerWidth, float spacingAdd, int align) {
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
        int lineLayoutMode=ReplacementSpan.FLOW;
        //段落计算信息
        Paragraph paragraph=new Paragraph(index);
        //段落排版信息
        ParagraphLayoutInfo paragraphLayoutInfo =new ParagraphLayoutInfo();
        //获得行偏移
        lineDecoration.getLineOffsets(paragraph.index,paragraph.lineCount, decorationTmpRect);
        float w = decorationTmpRect.left;
        int v = 0;
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
                            textParagraph.setParagraphLine(paragraph.lineCount);
                            textParagraph.setParagraph(paragraph.index);
                            textParagraph.setRect(decorationTmpRect.left,decorationTmpRect.top,decorationTmpRect.right,decorationTmpRect.bottom);
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
                        lineDecoration.getParagraphLineOffsets(paragraph.textParagraph,paragraph.lineCount, decorationTmpRect);
                        outTextLine(paragraph,here, next, paragraphFontMetrics.ascent, paragraphFontMetrics.descent, w, v,spacingAdd,align, decorationTmpRect,false);
                        layoutViewSpan(paragraph,replacementSpan, w,v);
                        here = next;
                        ok = here;
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
    private void outTextLine(Paragraph paragraph,int start, int end, int above, int below,
                             float x, float v,float extra, int align,Rect tmpRect, boolean isBreakLine) {
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

    private TextLine[] out(Paragraph paragraph, TextLine[] textLines, int index, int start, int end, int above, int below, int x, float v, float extra, int align,Rect rect, boolean isBreakLine) {
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

    @Override
    public int getLayoutWidth() {
        return width;
    }

    @Override
    public int getLayoutHeight() {
        return height;
    }

    /**
     * 布局滚动指定位置
     * @param dy
     */
    @Override
    public void scrollBy(float dy) {
        super.scrollBy(dy);
        //根据滚动填空文本
        scrollVerticallyBy(dy);
    }

    /**
     * 复写此方法,用于让外界检测是否可以滚动
     * @return
     */
    @Override
    public boolean canVerticalScroll() {
        //检测文本是否滑动边缘
        boolean canVerticalScroll=hasMore(layoutState);
        if(!canVerticalScroll){
            //检测是否滚到边缘
            if(DIRECTION_START==layoutState.itemDirection){
                //向上,检测起始位置是否0
                int scrollLineTop = getDecoratedScrollLineTop(0);
                canVerticalScroll=0!=scrollLineTop;
            } else if(DIRECTION_END==layoutState.itemDirection){
                //向下,检测结束位置是否为屏幕尺寸
                int line = getLineCount()-1;
                int scrollLineBottom = getDecoratedScrollLineBottom(line);
                canVerticalScroll=height!= scrollLineBottom;
            }
        }
        return canVerticalScroll;
    }

    @Override
    public int getScrollY() {
        return scrollY;
    }

    @Override
    public void setLayoutHeight(int outerHeight) {
        super.setLayoutHeight(outerHeight);
        this.height=outerHeight;
        //清除当前信息,但保存数组
        lineCount=0;
        Arrays.fill(textLines,0, textLines.length,null);
        //填空文本信息
        updateLayoutStateFromEnd(outerHeight);
        fillText(outerHeight);
    }

    /**
     * 此处实现纵向滚动代码
     */
    private float scrollVerticallyBy(float dy) {
        if (null==source || dy == 0) {
            return 0;
        }
        int layoutDirection = (dy > 0) ? DIRECTION_END : DIRECTION_START;
        float absDy = Math.abs(dy);
        //动态更新布局状态
        updateLayoutState(layoutDirection,absDy);
        //填充当前布局
        float fillSpace = fillText(height);
        float consumed=layoutState.scrollingOffset + fillSpace;
        //做边界处理,absDy > consumed时,consumed可能为0,也可以为滚过新条目值,此时做滚动越界约束
        float scrolled = 0;
        if (absDy <= consumed) {
            //正常滚动
            scrolled=dy;
        } else if(0!=consumed){
            //越界,可以手动控制超出等操作
            if(DIRECTION_START==layoutDirection&&0<consumed){
                //向上
                scrolled=-consumed;
            } else if(DIRECTION_END==layoutDirection&&0<consumed){
                //向上
                scrolled=consumed;
            }
        }
        //偏移行的绘制信息
        offsetTextLineVertical(-scrolled);
        return scrolled;
    }


    /**
     * 偏移行的绘制信息
     * @param v
     */
    private void offsetTextLineVertical(float v) {
        //直接以float转int会损失精度,导致向上排版偏差
        int offset=Math.round(v);
        //添加位移量
        scrollY+=offset;
        int lineCount = getLineCount();
        for(int line=0;line<lineCount;line++){
            textLines[line].setScrollOffset(scrollY);
        }
        //查找所有的view,偏移控件
        if(source instanceof Spanned){
            Spanned spanned = (Spanned) this.source;
            int latterStart = getLineLatterStart(0);
            int latterEnd = getLineLatterEnd(lineCount - 1);
            ViewSpan[] viewSpans = spanned.getSpans(latterStart, latterEnd, ViewSpan.class);
            for(int i=0;i<viewSpans.length;i++){
                ViewSpan viewSpan = viewSpans[i];
                View view = viewSpan.getView();
                view.offsetTopAndBottom(offset);
            }
        }
    }


    /**
     * 根据滚动偏移量,更新布局状态值
     */
    protected void updateLayoutState(int layoutDirection,float requiredSpace) {
        int scrollingOffset;
        if(layoutDirection== DIRECTION_END){
            int currentLine = getLineCount()-1;
            layoutState.itemDirection= DIRECTION_END;
            layoutState.paragraph=getParagraphIndex(currentLine);
            layoutState.paragraphLine=layoutState.itemDirection+getParagraphLine(currentLine);
            int lineBottom = getDecoratedScrollLineBottom(currentLine);
            layoutState.layoutOffset = lineBottom;
            //当前RecyclerView底部-最后一个控件底部,因为最后一个控件会超出底部
            scrollingOffset=lineBottom-height;
        } else {
            int currentLine=0;
            layoutState.itemDirection= DIRECTION_START;
            layoutState.paragraph=getParagraphIndex(currentLine);
            layoutState.paragraphLine=layoutState.itemDirection+getParagraphLine(currentLine);
            layoutState.layoutOffset = getDecoratedScrollLineTop(currentLine);
            //当前RecyclerView-控件顶点 因为控件可能超出顶端
            scrollingOffset = -getDecoratedScrollLineTop(currentLine);
        }
        //记录控件,缺多少值可以排版.也可以理解为,可排版空余值,默认不可排版时,一般为-1数
        //比如往下拉,requiredSpace为10 scrollOffset为105 则当前再滑动95下一个条目才开始加载,反之亦然
        layoutState.available= requiredSpace -scrollingOffset;
        layoutState.scrollingOffset =scrollingOffset;
    }

    /**
     * 填充文本内容
     */
    float fillText(int outerHeight) {
        //当前可填充空间
        float start=layoutState.available;
        //为避免回收时,scrollingOffset异常
        if(0>layoutState.available){
            layoutState.scrollingOffset+=layoutState.available;
        }
        //铺满过程中,检测并回收控件
        recycleByLayoutState(outerHeight);
        float remainingSpace=layoutState.available;
        while(0<remainingSpace&&hasMore(layoutState)){
            //填充指定行信息
            //从段落信息内检测,如果有直接使用.没有需要初始化
            float consumed=0;
            if(DIRECTION_START==layoutState.itemDirection){
                Paragraph paragraph = getParagraphFromDirection(layoutState,layoutState.paragraph);
                //从顶部开始填空文本
                consumed=fillLineFromStart(paragraph,layoutState.paragraphLine);
                //排版完成后,根据方向移动
                layoutState.paragraphLine+=layoutState.itemDirection;
            } else if(DIRECTION_END==layoutState.itemDirection){
                //根据当前段落位置,行信息,获得段落
                Paragraph paragraph = getParagraphFromDirection(layoutState,layoutState.paragraph);
                //从底部开始填空文本
                consumed=fillLineFromEnd(paragraph,layoutState.paragraphLine);
                //排版完成后,根据方向移动
                layoutState.paragraphLine+=layoutState.itemDirection;
            }
            layoutState.layoutOffset +=consumed*layoutState.itemDirection;
            layoutState.available-=consumed;
            remainingSpace-=consumed;
        }
        //返回排版后,所占用空间
        return start-layoutState.available;
    }

    /**
     * 获得段落信息
     * @param layoutState
     * @return
     */
    private Paragraph getParagraphFromDirection(LayoutState layoutState,int currentParagraphIndex) {
        int paragraphIndex = layoutState.paragraph;
        int paragraphLine = layoutState.paragraphLine;
        int itemDirection = layoutState.itemDirection;
        Paragraph paragraph = recyclerPool.getParagraph(paragraphIndex);
        //起始检测段落是否超出
        if(null!=paragraph&&layoutState.paragraph==currentParagraphIndex){
            if(DIRECTION_END==itemDirection){
                if(paragraph.lineCount-1<paragraphLine){
                    //如果行超出,段落前移
                    layoutState.paragraph=paragraphIndex+itemDirection;
                    //递归获取新的段落信息
                    paragraph= getParagraphFromDirection(layoutState,currentParagraphIndex);
                }
            } else if(DIRECTION_START==itemDirection){
                if(0 > paragraphLine){
                    //如果行超出,段落前移
                    layoutState.paragraph=paragraphIndex+itemDirection;
                    //递归获取新的段落信息
                    paragraph= getParagraphFromDirection(layoutState,currentParagraphIndex);
                }
            }
        }
        if(null==paragraph){
            //填充并记录段落信息
            Paragraph previousParagraph = recyclerPool.getParagraph(paragraphIndex-1);
            int bufferStart=null == previousParagraph ? 0:previousParagraph.getLineLatterEnd(previousParagraph.lineCount-1)+1;
            if(DIRECTION_END==itemDirection){
                paragraph = fillParagraphText(paragraphIndex, source, bufferStart, source.length(), paint, width, spacingAdd, textAlign);
            } else if(DIRECTION_START==itemDirection){
                paragraph = fillParagraphText(paragraphIndex, source, 0, bufferStart, paint, width, spacingAdd, textAlign);
            }
            recyclerPool.putParagraph(paragraphIndex,paragraph);
        }
        //检测是否切换段落,如果切换,重置位置
        if(DIRECTION_END==itemDirection){
            if(layoutState.paragraph!=currentParagraphIndex){
                //向下段落前进
                layoutState.paragraphLine=0;
            }
        } else if(DIRECTION_START==itemDirection){
            if(layoutState.paragraph!=currentParagraphIndex){
                //向上段落前进
                layoutState.paragraphLine=paragraph.lineCount-1;
            }
        }
        return paragraph;
    }

    /**
     * 将段落内指定行信息,填充到外围行信息
     * @param paragraph
     */
    int fillLineFromStart(Paragraph paragraph, int paragraphLine){
        //检测当前文本行空间,不够则扩充
        ensureTextLineArray();
        //向上填空行信息
        int off = 0;
        TextLine[] lines = this.textLines;
        int firstLineTop = lines[off].getDecoratedLineTop();
        //1. 先将数组拷贝,所有元素向后移动一个单元-columns
        System.arraycopy(lines,off,lines,1,lines.length-1);
        //2. 将段落信息拷贝到当前行信息内
         System.arraycopy(paragraph.textLines,paragraphLine,lines,0,1);
        //累加段落排版高度,放在此处累加是为了保证fillParagraph代码内计算逻辑简化,否则需要加入往上,或者向下的逻辑判断
        Rect decoratedRect = lines[off].getDecoratedRect();
        int lineHeight = lines[off].getLineHeight();
        lines[off].setLineTop(firstLineTop-decoratedRect.bottom-lineHeight);
        lines[off].setLineBottom(firstLineTop-decoratedRect.bottom);
        lines[off].setLineDecoration(lineDecoration);
        lines[off].setScrollOffset(scrollY);
        //填充指定行信息
        onNewTextLineFilled(lines[off]);
        int consumed=0;
        if(lines[off].isBreakLine()){
            consumed=getLineHeight(off);
        }
        //检测不是结束行,不计算高度消费
        lineCount++;
        return consumed;
    }

    private int getLineBottomFromEnd(int line){
        int lineBottom=0;
        int index=0 > line-1 ? 0 : line-1;
        if(index<lineCount){
            //移除不是换行的信息位置
            if(!textLines[index].isBreakLine()){
                index--;
            }
            //根据位置获得移动底部位置
            if(0 <= index){
                lineBottom=textLines[index].getDecoratedLineBottom();
            }
        }
        return lineBottom;
    }
    /**
     * 将段落内指定行信息,填充到外围行信息
     * @param paragraph
     */
    int fillLineFromEnd(Paragraph paragraph, int paragraphLine){
        //检测当前文本行空间,不够则扩充
        ensureTextLineArray();
        //向下填空行信息
        int line = lineCount;
        TextLine[] lines = this.textLines;
        //获得上一行底部底部排版位置,累加
        int lineTop = getLineBottomFromEnd(line);
        System.arraycopy(paragraph.textLines,paragraphLine,lines,line,1);

        //积加段落排版高度,放在此处累加是为了保证fillParagraph代码内计算逻辑简化,否则需要加入往上,或者向下的逻辑判断
        int lineHeight = lines[line].getLineHeight();
        Rect decoratedRect = lines[line].getDecoratedRect();
        lines[line].setLineTop(lineTop+decoratedRect.top);
        lines[line].setLineBottom(lineTop+decoratedRect.top+lineHeight);
        lines[line].setScrollOffset(scrollY);
        lines[line].setLineDecoration(lineDecoration);
        //填充指定行信息
        onNewTextLineFilled(lines[line]);
        int consumed=0;
        if(lines[line].isBreakLine()){
            consumed=getLineHeight(line);
        }
        lineCount++;
        return consumed;
    }

    /**
     * 填空指定行信息
     */
    protected void onNewTextLineFilled(TextLine textLine){
        //排版子控件，其他操作也可以在这里进行
        textLine.layoutViewSpan(source,0,0);
    }

    /**
     * 检测当前文本行空间,不够则扩充
     */
    private void ensureTextLineArray(){
        int line = lineCount;
        int want = line + 1;
        TextLine[] textLines = this.textLines;
        if (want >= textLines.length) {
            int nlen = ArrayUtils.idealIntArraySize(want + 1);
            TextLine[] grow = new TextLine[nlen];
            System.arraycopy(textLines, 0, grow, 0, textLines.length);
            this.textLines = grow;
        }
    }

    /**
     * 根据滑动状态,回收控件
     */
    protected void recycleByLayoutState(int outerHeight) {
        if(layoutState.itemDirection== DIRECTION_START){
            //回收底部控件
            recycleFromEnd(outerHeight,layoutState.scrollingOffset);
        } else if(layoutState.itemDirection== DIRECTION_END){
            //回收顶部控件
            recycleFromStart(outerHeight,layoutState.scrollingOffset);
        }
    }

    /**
     * 从顶部开始检测回收
     * @param dt
     */
    private void recycleFromStart(int outerHeight, float dt) {
        if (dt < 0) {
            return;
        }
        int lineCount = getLineCount();
        for (int i=0;i<lineCount;i++) {
            int lineBottom = getDecoratedScrollLineBottom(i);
            if (lineBottom > dt) {// stop here
                for (int line=0;line < i;line++) {
                    removeTextLineFromStart(0);
                }
                break;
            }
        }
    }

    /**
     * 从底部开始检测回收
     * @param dt
     */
    private void recycleFromEnd(int outerHeight, float dt) {
        if (dt < 0) {
            return;
        }
        int lineCount = getLineCount();
        float limit = outerHeight - dt;
        for (int i=lineCount-1;i>=0;i--) {
            int lineTop = getDecoratedScrollLineTop(i);
            if (lineTop < limit) {// stop here
                for (int line=lineCount - 1;line>i;line--) {
                    removeTextLineFromEnd(getLineCount()-1);
                }
                break;
            }
        }
    }

    /**
     * Remove a specific line information from line origin array
     */
    void removeTextLineFromStart(int index) {
        removeTextLineAt(index);
        //回收行信息
        TextLine textLine = textLines[index];
        //todo 回收段落
        TextLine.recycle(textLine);
        textLines[index]=null;
        System.arraycopy(textLines,1, textLines,index, textLines.length-1);
        lineCount--;
    }

    /**
     * Remove a specific line information from line origin array
     */
    void removeTextLineFromEnd(int index) {
        removeTextLineAt(index);
        TextLine textLine = textLines[index];
        TextLine.recycle(textLine);
        textLines[index]=null;
        lineCount--;
    }

    /**
     * 移除指定行特殊span位置
     * @param index
     */
    private void removeTextLineAt(int index) {
        //渲染通知停止
        TextLine textLine = textLines[index];
        int start = textLine.getLineStart();
        int end = textLine.getLineEnd();
        textRender.removeTextLine(source,start,end);
        //移除viewSpan
        if(source instanceof Spanned){
            Spanned spanned = (Spanned) this.source;
            int lineLatterStart = getLineLatterStart(index);
            int lineLatterEnd = getLineLatterEnd(index);
            ViewSpan[] viewSpans = spanned.getSpans(lineLatterStart, lineLatterEnd, ViewSpan.class);
            for(int i=0;i<viewSpans.length;i++){
                //从父容器移除
                ViewSpan viewSpan = viewSpans[i];
                viewSpan.detachFromParent();
            }
        }
    }

    /**
     * 是否有更多数据,主要以当前操作行信息,检测数据是否超出范围,此处可被复写,用于检测检测是否需要继续排版
     */
    protected boolean hasMore(LayoutState layoutState){
        boolean hasMore=false;
        if(DIRECTION_START==layoutState.itemDirection){
            //向上,检测起始字符是否为0
            int line=0;
            hasMore=0!=getLineLatterStart(line);
        } else if(DIRECTION_END==layoutState.itemDirection){
            //向下,检测字符数是否超过文本长度
            CharSequence text = getText();
            int line = getLineCount()-1;
            int latterEnd=line < 0 ? 0 : getLineLatterEnd(line);
            hasMore=text.length()!=latterEnd;
        }
        return hasMore;
    }

    /**
     * 初始化起始Layout布局状态
     */
    private void updateLayoutStateFromEnd(int outerHeight) {
        layoutState.layoutOffset = 0;
        layoutState.paragraph = 0;
        layoutState.paragraphLine=0;
        layoutState.available = outerHeight;
        layoutState.itemDirection = DIRECTION_END;
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

    public int getLineCount() {
        return lineCount;
    }

    @Override
    public int getDecoratedScrollLineTop(int line) {
        return textLines[line].getDecoratedScrollLineTop();
    }

    @Override
    public int getDecoratedScrollLineBottom(int line) {
        return textLines[line].getDecoratedScrollLineBottom();
    }

    private int getLineTop(int line) {
        return textLines[line].getLineTop();
    }

    private int getLineBottom(int line) {
        return textLines[line].getLineBottom();
    }

    public int getDecoratedDescent(int line) {
        return textLines[line].getDecoratedScrollDescent();
    }

    @Override
    public int getLineStart(int line) {
        return textLines[line].getLineLeft();
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
     * 获得所处段落位置
     * @param line
     */
    private int getParagraphIndex(int line) {
        return textLines[line].getParagraph();
    }

    /**
     * 获得所处位置段落行位置
     * @param line
     */
    private int getParagraphLine(int line) {
        return textLines[line].getParagraphLine();
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
                textLine.draw(c,textRender,source,paint,workPaint,width,height,true);
            }
        }
    }

    /**
     * 布局排版状态对象
     */
    public static class LayoutState{
        /**
         * 所在段落
         */
        int paragraph=0;
        /**
         * 段落所在行
         */
        int paragraphLine=0;
        /**
         * 当前有效空间
         */
        float available=0f;
        /**
         * 当前排版位置
         */
        float layoutOffset =0f;
        /**
         * 当前滚动位置
         */
        float scrollingOffset =0f;
        /**
         * 当前操作条目方向
         */
        int itemDirection= DIRECTION_END;
    }

    /**
     * 段落信息
     */
    public class Paragraph{
        /**
         * 当前段落
         */
        private final int index;
        /**
         * 当前段落行总数
         */
        private int lineCount=0;
        /**
         * 段落内行信息
         */
        private TextLine[] textLines;

        private TextParagraph textParagraph;

        public Paragraph(int index) {
            this.index = index;
        }

        /**
         * 返回指定行起始字符
         * @param line
         * @return
         */
        public int getLineLatterStart(int line) {
            return textLines[line].getLineStart();
        }

        /**
         * 返回行结束字符
         * @param line
         * @return
         */
        public final int getLineLatterEnd(int line) {
            return textLines[line].getLineEnd();
        }

        /**
         * 获得行所占宽
         * @param line
         * @return
         */
        public int getLineHeight(int line){
            return textLines[line].getLineHeight();
        }
    }

    /**
     * 段落回收池对象
     */
    public static class ParagraphRecyclerPool{
        /**
         * 段落存放集合
         */
        private final SparseArray<Paragraph> paragraphArray;
        /**
         * 设置保存最大数
         */
        private int maxCacheSize=2;

        public ParagraphRecyclerPool() {
            this.paragraphArray =new SparseArray<>(2);
        }

        /**
         * 放入段落
         * @param index
         * @param paragraph
         */
        public void putParagraph(int index,Paragraph paragraph){
            this.paragraphArray.put(index,paragraph);
            //清除外围多余的段落信息
            int offset=1;
            int itemCount=1;
//            while(maxCacheSize>paragraphArray.size()){
//                Paragraph paragraph1 = paragraphArray.get(index - offset);
//                Paragraph paragraph2 = paragraphArray.get(index + offset);
//            }
        }

        /**
         * 根据段落信息获得指定段落
         * @param index
         * @return
         */
        public Paragraph getParagraph(int index){
            return this.paragraphArray.get(index);
        }

        /**
         * 设置缓存最大数,超出会移除邻近位置的段落信息,非最少使用,也不是放入顺序
         * @param maxCacheSize
         */
        public void setMaxCacheSize(int maxCacheSize) {
            this.maxCacheSize = maxCacheSize;
        }
    }

}
