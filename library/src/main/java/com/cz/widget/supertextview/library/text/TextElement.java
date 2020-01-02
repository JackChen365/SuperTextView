package com.cz.widget.supertextview.library.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Spanned;
import android.text.TextPaint;
import android.view.Gravity;

import com.cz.widget.supertextview.library.Styled;
import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.render.TextRender;
import com.cz.widget.supertextview.library.span.ViewSpan;
import com.cz.widget.supertextview.library.view.TextParent;

/**
 * 文本元素,扩展所有界面文本绘制信息
 */
public class TextElement {
    public static final TextElement[] EMPTY_ARRAY=new TextElement[0];
    /**
     * 移动偏移
     */
    private int scrollOffset;
    /**
     * 控件字符起始位置
     */
    private int lineStart;
    /**
     * 字符结束位置
     */
    private int lineEnd;
    /**
     * 排版顶部位置
     */
    private int lineTop;
    /**
     * 文本底部位置
     */
    private int lineDescent;
    /**
     * 文本起始绘制位置
     */
    private int lineLeft;
    /**
     * 文本底部位置,因为存在一行内,多行信息
     */
    private int lineBottom;
    /**
     * 文本对齐标志
     */
    private int lineAlign;
    /**
     * 当前操作矩阵
     */
    private Rect decorateRect =new Rect();
    /**
     * 所在段落
     */
    private int paragraph;
    /**
     * 处在段落内
     */
    private int paragraphLine;
    /**
     * 行装饰器
     */
    private LineDecoration lineDecoration;
    /**
     * 缓存的textLine对象
     */
    private static final TextElement[] cached = new TextElement[10];

    /**
     * 获取一个直接使用的textLine对象
     * @return
     */
    public static TextElement obtain() {
        TextElement textElement;
        synchronized (cached) {
            for (int i = cached.length; --i >= 0;) {
                if (cached[i] != null) {
                    textElement = cached[i];
                    cached[i] = null;
                    return textElement;
                }
            }
        }
        return new TextElement();
    }

    /**
     * 回收一个textElement对象
     * @param textElement
     * @return
     */
    public static void recycle(TextElement textElement) {
        //todo 回收段落
//        textElement.recycler();
    }

    /**
     * 置空信息
     */
    public void recycler() {
        lineStart = 0;
        lineEnd = 0;
        lineTop = 0;
        lineDescent = 0;
        lineLeft = 0;
        lineBottom = 0;
        lineAlign = 0;
        scrollOffset=0;
        paragraph=0;
        paragraphLine=0;
        decorateRect.setEmpty();
        lineDecoration=null;
        synchronized(cached) {
            for (int i = 0; i < cached.length; ++i) {
                if (cached[i] == null) {
                    cached[i] = this;
                    break;
                }
            }
        }
    }

    public void setScrollOffset(int scrollOffset) {
        this.scrollOffset = scrollOffset;
    }

    public void setLineDecoration(LineDecoration lineDecoration) {
        this.lineDecoration = lineDecoration;
    }

    public void setLineStart(int lineStart) {
        this.lineStart = lineStart;
    }

    public void setLineEnd(int lineEnd) {
        this.lineEnd = lineEnd;
    }

    public void setLineTop(int lineTop) {
        this.lineTop = lineTop;
    }

    public void setLineDescent(int lineDescent) {
        this.lineDescent = lineDescent;
    }

    public void setLineLeft(int lineLeft) {
        this.lineLeft = lineLeft;
    }

    public void setLineBottom(int lineBottom) {
        this.lineBottom = lineBottom;
    }

    public void setLineAlign(int lineAlign) {
        this.lineAlign = lineAlign;
    }

    public void setParagraph(int paragraph) {
        this.paragraph = paragraph;
    }

    public void setParagraphLine(int paragraphLine) {
        this.paragraphLine = paragraphLine;
    }
    /**
     * 设置矩阵信息
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setDecorateRect(int left,int top,int right,int bottom){
        this.decorateRect.set(left,top,right,bottom);
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public int getLineStart() {
        return lineStart;
    }

    public int getLineEnd() {
        return lineEnd;
    }

    public int getLineTop() {
        return lineTop;
    }

    public int getLineDescent() {
        return lineDescent;
    }

    public int getLineLeft() {
        return lineLeft;
    }

    public int getLineBottom() {
        return lineBottom;
    }

    public int getLineAlign() {
        return lineAlign;
    }

    /**
     * 获得行高
     * @return
     */
    public int getLineHeight(){
        return lineBottom-lineTop;
    }


    public int getDecoratedScrollLineTop(){
        return scrollOffset+lineTop- decorateRect.top;
    }

    public int getDecoratedScrollLineBottom(){
        return scrollOffset+lineBottom+ decorateRect.bottom;
    }

    public int getDecoratedLineTop(){
        return lineTop- decorateRect.top;
    }

    public int getDecoratedLineBottom(){
        return lineBottom+ decorateRect.bottom;
    }

    public int getScrollDescent() {
        return scrollOffset+(lineBottom-lineDescent);
    }

    public int getScrollTop() {
        return scrollOffset+lineTop;
    }

    public int getScrollBottom() {
        return scrollOffset+lineBottom;
    }

    public int getDecoratedLeft() {
        return lineLeft- decorateRect.left;
    }

    public int getParagraph() {
        return paragraph;
    }

    public int getParagraphLine() {
        return paragraphLine;
    }

    public Rect getDecoratedRect(){
        return decorateRect;
    }

    public LineDecoration getLineDecoration() {
        return lineDecoration;
    }

    /**
     * 添加到视图
     */
    public void onAttachToWindow(TextParent textParent){
    }

    /**
     * 被视图移出
     */
    public void onDetachFromWindow(TextParent textParent){
    }


    /**
     * 排版子控件
     */
    public void layoutViewSpan(TextParent parentView, CharSequence source, int left, int top) {
        if(source instanceof Spanned){
            Spanned spanned = (Spanned) source;
            int newLineTop = getDecoratedScrollLineTop();
            int newLineBottom = getDecoratedScrollLineBottom();
            int newLineGravity = getLineAlign();
            int newLineLatterStart = getLineStart();
            int newLineLatterEnd = getLineEnd();
            ViewSpan[] viewSpans = spanned.getSpans(newLineLatterStart, newLineLatterEnd, ViewSpan.class);
            for(int i=0;i<viewSpans.length;i++){
                ViewSpan viewSpan = viewSpans[i];
                //根据方向重置排版top偏移
                int viewHeight = viewSpan.getHeight();
                if(Gravity.TOP==newLineGravity){
                    viewSpan.setLayoutTop(top+newLineTop);
                } else if(Gravity.CENTER==newLineGravity){
                    int lineHeight=newLineBottom-newLineTop;
                    viewSpan.setLayoutTop(top+newLineTop+(lineHeight-viewHeight)/2);
                } else if(Gravity.BOTTOM==newLineGravity) {
                    viewSpan.setLayoutTop(top + newLineBottom - viewHeight);
                }
                viewSpan.setParentView(parentView);
                viewSpan.attachToView();
            }
        }
    }


    /**
     * 绘制文本
     * @param canvas
     * @param text
     * @param paint
     * @param workPaint
     */
    public void draw(Canvas canvas, TextRender textRender, CharSequence text, TextPaint paint, TextPaint workPaint, Paint.FontMetricsInt fontMetricsInt, int outerWidth, boolean lineDecorate){
        if(lineDecorate){
            //绘制行装饰器
            lineDecoration.onLineDraw(canvas,this, outerWidth);
        }
        //绘制文本
        int baseline = getScrollDescent();
        if (!(text instanceof Spanned)) {
            canvas.drawText(text, lineStart, lineEnd, lineLeft, baseline, paint);
        } else {
            int top = getScrollTop();
            int bottom = getScrollBottom();
            Styled.drawText(canvas, textRender,text, lineStart, lineEnd, lineLeft, top, baseline, bottom,fontMetricsInt,paint, workPaint, lineAlign);
        }
    }
}
