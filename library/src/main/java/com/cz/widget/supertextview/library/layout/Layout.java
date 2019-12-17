package com.cz.widget.supertextview.library.layout;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Spanned;
import android.text.TextPaint;

import com.cz.widget.supertextview.library.Styled;
import com.cz.widget.supertextview.library.render.DefaultTextRender;
import com.cz.widget.supertextview.library.render.TextRender;


/**
 * 文本排版layout
 * 当前实现功能
 * 1. 实现多行文本排版
 * 2. 实现断行策略抽离
 * 3. 实现span
 */
public abstract class Layout {
    private static final String TAG="Layout";
    private CharSequence text;
    private TextPaint paint;
    TextPaint workPaint;
    private int width;
    private float spacingAdd;
    private TextRender textRender;
    private Spanned spanned;
    private boolean spannedText;

    /**
     *
     * @param text 操作文本
     * @param paint 绘制paint
     * @param width 排版宽
     * @param spacingAdd  行额外添加空间
     */
    protected Layout(CharSequence text, TextPaint paint,
                     TextRender textRender,int width, float spacingAdd) {
        if (width < 0)
            throw new IllegalArgumentException("Layout: " + width + " < 0");
        this.text = text;
        this.paint = paint;
        this.workPaint = new TextPaint();
        this.textRender=textRender;
        this.width = width;
        this.spacingAdd = spacingAdd;
        if(text instanceof Spanned)
            spanned = (Spanned) text;
        spannedText = text instanceof Spanned;
    }

    /**
     * 获得指定类型的span对象
     * @param start 查找文本起始位置
     * @param end 查找文本结束位置
     * @param type 查找对象字节码
     * @param <T>
     * @return 查找元素数组
     */
    public <T> T[] getSpans(int start, int end, Class<T> type) {
        return spanned.getSpans(start, end, type);
    }

    /**
     * 返回行附加空间
     * @return
     */
    public final float getSpacingAdd() {
        return spacingAdd;
    }

    /**
     * 返回行个数
     * @return
     */
    public abstract int getLineCount();

    /**
     * 返回文本
     * @return
     */
    public final CharSequence getText() {
        return text;
    }

    /**
     * 返回操作Paint对象,也是关联操作View的Paint对象
     * @return
     */
    public final TextPaint getPaint() {
        return paint;
    }

    /**
     * 当前操作尺寸宽度
     * @return
     */
    public final int getWidth() {
        return width;
    }

    /**
     * 返回当前所占高度
     * @return
     */
    public int getHeight() {
        int height=0;
        int lineCount = getLineCount();
        if(0<lineCount){
            height=getDecoratedScrollLineBottom(lineCount-1);
        }
        return height;
    }

    public int getLayoutWidth(){
        return 0;
    }

    public int getLayoutHeight(){
        return 0;
    }

    /**
     * 获得行所占宽
     * @param line
     * @return
     */
    public int getLineHeight(int line){
        return getDecoratedScrollLineBottom(line)- getDecoratedScrollLineTop(line);
    }

    /**
     * 返回指定行起始高
     * @param line
     * @return
     */
    public abstract int getDecoratedScrollLineTop(int line);

    /**
     * 获得行结束高度,此处暂不存在行间距运算
     * @param line
     * @return
     */
    public abstract int getDecoratedScrollLineBottom(int line);

    /**
     * 返回指定行文本Descent位置
     * @param line
     * @return
     */
    public abstract int getDecoratedDescent(int line);

    /**
     * 获得指定行绘制位置
     * @param line
     * @return
     */
    public abstract int getLineStart(int line);

    /**
     * 获得文本纵向排队方位
     * @param line
     * @return
     */
    public abstract int getLineGravity(int line);

    /**
     * 返回指定行起始字符
     * @param line
     * @return
     */
    public abstract int getLineLatterStart(int line);

    /**
     * 返回行结束字符
     * @param line
     * @return
     */
    public abstract int getLineLatterEnd(int line);

    /**
     * 返回指定y轨位置所在行
     * @param vertical 纵轨位置
     * @return
     */
    public int getLineForVertical(int vertical) {
        int high = getLineCount(), low = -1, guess;
        while (high - low > 1) {
            guess = (high + low) / 2;
            if (getDecoratedScrollLineTop(guess) > vertical)
                high = guess;
            else
                low = guess;
        }
        if (low < 0)
            return 0;
        else
            return low;
    }

    /**
     * 返回可见的行位置
     * @param line
     * @param start
     * @param end
     * @return
     */
    private int getLineVisibleEnd(int line, int start, int end) {
        CharSequence text = this.text;
        char ch;
        if (line == getLineCount() - 1) {
            return end;
        }

        for (; end > start; end--) {
            ch = text.charAt(end - 1);

            if (ch == '\n') {
                return end - 1;
            }

            if (ch != ' ' && ch != '\t') {
                break;
            }

        }

        return end;
    }

    /**
     * Get the line number on which the specified text offset appears.
     * If you ask for a position before 0, you get 0; if you ask for a position
     * beyond the end of the text, you get the last line.
     */
    public int getLineForOffset(int offset) {
        int high = getLineCount(), low = -1, guess;

        while (high - low > 1) {
            guess = (high + low) / 2;

            if (getLineStart(guess) > offset)
                high = guess;
            else
                low = guess;
        }

        if (low < 0)
            return 0;
        else
            return low;
    }

    public float getHorizontal(int offset, int line) {
        int start = getLineLatterStart(line);
        int end = getLineLatterEnd(line);
        float wid = measureText(paint, workPaint, text, start, offset, end);
        if (offset > end) {
            wid += measureText(paint, workPaint, text, end, offset,null);
        }
        return wid;
    }

    private static float measureText(TextPaint paint,
                                     TextPaint workPaint,
                                     CharSequence text,
                                     int start, int offset, int end) {
        float h = 0;

        int here = 0;
        int there = here + 1;
        if (there > end - start)
            there = end - start;

        int segstart = here;
        for (int j = there; j <= there; j++) {
            if (j == there) {
                float segw;
                if (offset < start + j || (offset <= start + j)) {
                    h += Styled.measureText(paint, workPaint, text,
                            start + segstart, offset,
                            null);
                    return h;
                }
                segw = Styled.measureText(paint, workPaint, text,
                        start + segstart, start + j,
                        null);
                if (offset < start + j || (offset <= start + j)) {
                    h += segw - Styled.measureText(paint, workPaint,
                            text,
                            start + segstart,
                            offset, null);
                    return h;
                }
                h += segw;
                if (j != there) {
                    if (offset == start + j)
                        return h;
                }
                segstart = j + 1;
            }
        }
        return h;
    }

    /**
     * Measure width of a run of text on a single line that is known to all be
     * in the same direction as the paragraph base direction. Returns the width,
     * and the line metrics in fm if fm is not null.
     *
     * @param paint the paint for the text; will not be modified
     * @param workPaint paint available for modification
     * @param text text
     * @param start start of the line
     * @param end limit of the line
     * @param fm object to return integer metrics in, can be null
     * @return the width of the text from start to end
     */
    /* package */ static float measureText(TextPaint paint,
                                           TextPaint workPaint,
                                           CharSequence text,
                                           int start, int end,
                                           Paint.FontMetricsInt fm) {
        int len = end - start;

        int lastPos = 0;
        float width = 0;
        int ascent = 0, descent = 0, top = 0, bottom = 0;

        if (fm != null) {
            fm.ascent = 0;
            fm.descent = 0;
        }

        for (int pos = len; pos <= len; pos++) {
            if (pos == len) {
                workPaint.baselineShift = 0;

                width += Styled.measureText(paint, workPaint, text,
                        start + lastPos, start + pos,
                        fm);

                if (fm != null) {
                    if (workPaint.baselineShift < 0) {
                        fm.ascent += workPaint.baselineShift;
                        fm.top += workPaint.baselineShift;
                    } else {
                        fm.descent += workPaint.baselineShift;
                        fm.bottom += workPaint.baselineShift;
                    }
                }

                if (fm != null) {
                    if (fm.ascent < ascent) {
                        ascent = fm.ascent;
                    }
                    if (fm.descent > descent) {
                        descent = fm.descent;
                    }

                    if (fm.top < top) {
                        top = fm.top;
                    }
                    if (fm.bottom > bottom) {
                        bottom = fm.bottom;
                    }

                    // No need to take bitmap height into account here,
                    // since it is scaled to match the text height.
                }

                lastPos = pos + 1;
            }
        }

        if (fm != null) {
            fm.ascent = ascent;
            fm.descent = descent;
            fm.top = top;
            fm.bottom = bottom;
        }
        return width;
    }

    /**
     * Get the character offset on the specfied line whose position is
     * closest to the specified horizontal position.
     */
    public int getOffsetForHorizontal(int line, float horiz) {
        return line;
    }


    /**
     * 滚动指定位置
     * @param dy
     */
    public void scrollBy(float dy){
    }

    /**
     * 是否可以滚动
     * @return
     */
    public boolean canVerticalScroll(){
        return false;
    }

    /**
     * 获得滚动纵轨偏移位置
     * @return
     */
    public int getScrollY(){
        return 0;
    }

    /**
     * 外部重设布局高度
     * @param outerHeight
     */
    public void setLayoutHeight(int outerHeight) {
    }

    public void setTextRender(TextRender textRender) {
        this.textRender = textRender;
    }

    public TextRender getTextRender() {
        return textRender;
    }

    /**
     * 获得当前屏幕展示行的对应行数
     * @param line
     * @return
     */
    public int getLineNumber(int line){
        return line;
    }

    public int getSpanStart(Object tag) {
        return spanned.getSpanStart(tag);
    }

    public int getSpanEnd(Object tag) {
        return spanned.getSpanEnd(tag);
    }

    public int getSpanFlags(Object tag) {
        return spanned.getSpanFlags(tag);
    }

    public int nextSpanTransition(int start, int limit, Class type) {
        return spanned.nextSpanTransition(start, limit, type);
    }

    /**
     * Draw this Layout on the specified Canvas.
     */
    public void draw(Canvas c) {
        TextPaint paint = this.paint;
        CharSequence buf = text;
        boolean spannedText = this.spannedText;
        int lineCount = getLineCount();
        for (int i = 0; i < lineCount; i++) {
            int left = getLineStart(i);
            int lineTop = getDecoratedScrollLineTop(i);
            int lineBottom = getDecoratedScrollLineBottom(i);
            int start = getLineLatterStart(i);
            int end = getLineLatterEnd(i);
            int baseline = lineBottom - getDecoratedDescent(i);
            int x= left;
            if (!spannedText) {
                c.drawText(buf, start, end, x, baseline, paint);
            } else {
                int lineGravity = getLineGravity(i);
                Styled.drawText(c,textRender, buf, start, end, x, lineTop, baseline, lineBottom, paint, workPaint, lineGravity,false);
            }
        }
    }
}
