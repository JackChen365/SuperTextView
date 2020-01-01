package com.cz.widget.supertextview.library.layout;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Spanned;
import android.text.TextPaint;

import com.cz.widget.supertextview.library.Styled;
import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.layout.adapter.DynamicTextLayoutAdapter;
import com.cz.widget.supertextview.library.layout.adapter.TextLayoutAdapter;
import com.cz.widget.supertextview.library.layout.adapter.SimpleTextLayoutAdapter;
import com.cz.widget.supertextview.library.render.TextRender;

import java.util.ArrayList;
import java.util.List;


/**
 * 文本排版layout
 * 当前实现功能
 * 1. 实现多行文本排版
 * 2. 实现断行策略抽离
 * 3. 实现span
 */
public abstract class Layout {
    private static final String TAG="Layout";
    private static final Object LOCK=new Object();
    final SimpleTextLayoutAdapter textLayoutAdapter;
    List<DynamicTextLayoutAdapter> textLayoutAdapters=new ArrayList<>();
    CharSequence text;
    TextPaint paint;
    TextRender textRender;
    TextPaint workPaint;
    Paint.FontMetricsInt fontMetricsInt;
    int width;
    private float spacingAdd;
    private Spanned spanned;
    private boolean spannedText;

    /**
     *
     * @param text 操作文本
     * @param paint 绘制paint
     * @param width 排版宽
     * @param spacingAdd  行额外添加空间
     */
    protected Layout(CharSequence text, TextPaint paint, LineDecoration lineDecoration,
                     TextRender textRender, int width, float spacingAdd) {
        if (width < 0)
            throw new IllegalArgumentException("Layout: " + width + " < 0");
        this.text = text;
        this.paint = paint;
        this.workPaint = new TextPaint();
        this.fontMetricsInt = new Paint.FontMetricsInt();
        this.textLayoutAdapter=new SimpleTextLayoutAdapter(lineDecoration);
        this.textRender=textRender;
        this.width = width;
        this.spacingAdd = spacingAdd;
        if(text instanceof Spanned)
            spanned = (Spanned) text;
        spannedText = text instanceof Spanned;
    }

    /**
     * 注册文本布局适配器
     * @param textLayoutAdapter
     */
    public void registerTextLayoutAdapter(DynamicTextLayoutAdapter textLayoutAdapter){
        synchronized (LOCK){
            textLayoutAdapters.add(textLayoutAdapter);
        }
    }

    /**
     * 解除一个布局适配器绑定
     * @param textLayoutAdapter
     */
    public void unregisterTextLayoutAdapter(DynamicTextLayoutAdapter textLayoutAdapter){
        synchronized (LOCK){
            textLayoutAdapters.remove(textLayoutAdapter);
        }
    }

    /**
     * 获得所有注册的数据适配器
     * @return
     */
    public List<DynamicTextLayoutAdapter> getTextLayoutAdapters() {
        return textLayoutAdapters;
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
     * 返回指定行起始高,此处包含行间距运算
     * @param line
     * @return
     */
    public abstract int getDecoratedScrollLineTop(int line);

    /**
     * 获得行结束高度,此处包含行间距运算
     * @param line
     * @return
     */
    public abstract int getDecoratedScrollLineBottom(int line);

    /**
     * 返回指定行起始高,此处暂不存在行间距运算
     * @param line
     * @return
     */
    public abstract int getScrollLineTop(int line);

    /**
     * 获得行结束高度,此处暂不存在行间距运算
     * @param line
     * @return
     */
    public abstract int getScrollLineBottom(int line);


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
     * Get the line number on which the specified text offset appears.
     * If you ask for a position before 0, you get 0; if you ask for a position
     * beyond the end of the text, you get the last line.
     */
    public int getLineForOffset(int offset) {
        int high = getLineCount(), low = -1, guess;

        while (high - low > 1) {
            guess = (high + low) / 2;

            if (getLineLatterStart(guess) > offset)
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
     * Get the character offset on the specfied line whose position is
     * closest to the specified horizontal position.
     */
    private int getOffsetForHorizontal(int line, float offset) {
        float x=0;
        int index=-1;
        int start = getLineLatterStart(line);
        int end = getLineLatterEnd(line);
        for (int i = start; i < end; i++) {
            x = Styled.measureText(paint, workPaint, text, i, i+1,x);
            if(offset<x){
                index=i;
                break;
            }
        }
        return index;
    }

    /**
     * 根据x,y坐标获得横向位置
     * @param x
     * @param y
     * @return
     */
    public int getOffsetForHorizontal(float x,float y){
        int line = getLineForVertical((int) y);
        return getOffsetForHorizontal(line,x);
    }

    /**
     * 标记选中范围path
     * @param start
     * @param end
     * @param selectPath
     */
    public void getSelectionPath(int start, int end, Path selectPath){
        int line=getLineForOffset(start);
        int lineTop = getDecoratedScrollLineTop(line);
        int lineBottom = getDecoratedScrollLineBottom(line);
        int latterStart = getLineLatterStart(line);
        int latterEnd = getLineLatterEnd(line);
        selectPath.reset();
        //计算起始位置
        float x = Styled.measureText(paint, workPaint, text, latterStart, start,0);
        for (int i = start; i < end; i++) {
            //跳转到下一行
            if(latterEnd < i){
                line++;
                lineTop = getDecoratedScrollLineTop(line);
                lineBottom = getDecoratedScrollLineBottom(line);
                latterEnd = getLineLatterEnd(line);
            }
            float left=x;
            x = Styled.measureText(paint, workPaint, text, i, i+1,x);
            selectPath.addRect(left,lineTop,x,lineBottom, Path.Direction.CW);
        }
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
    }
}
