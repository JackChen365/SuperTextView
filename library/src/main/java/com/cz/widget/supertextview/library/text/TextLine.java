package com.cz.widget.supertextview.library.text;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.Spanned;
import android.text.TextPaint;
import android.view.Gravity;

import com.cz.widget.supertextview.library.Styled;
import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.render.TextRender;
import com.cz.widget.supertextview.library.span.ViewSpan;

/**
 * 文本行信息
 */
public class TextLine {
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
     * 所在段落
     */
    private int paragraph;
    /**
     * 处在段落内
     */
    private int paragraphLine;
    /**
     * 是否为结束行
     */
    private boolean isBreakLine;
    /**
     * 当前操作矩阵
     */
    private Rect rect=new Rect();
    /**
     * 行装饰器
     */
    private LineDecoration lineDecoration;
    /**
     * 缓存的textLine对象
     */
    private static final TextLine[] cached = new TextLine[3];

    /**
     * 获取一个直接使用的textLine对象
     * @return
     */
    public static TextLine obtain() {
        TextLine tl;
        synchronized (cached) {
            for (int i = cached.length; --i >= 0;) {
                if (cached[i] != null) {
                    tl = cached[i];
                    cached[i] = null;
                    return tl;
                }
            }
        }
        tl = new TextLine();
        return tl;
    }

    /**
     * 回收一个textLine对象
     * @param tl
     * @return
     */
    public static void recycle(TextLine tl) {
//        tl.recycler();
    }

    public void recycler() {
        lineStart = 0;
        lineEnd = 0;
        lineTop = 0;
        lineDescent = 0;
        lineLeft = 0;
        lineBottom = 0;
        lineAlign = 0;
        paragraph = 0;
        paragraphLine = 0;
        scrollOffset=0;
        rect.setEmpty();
        lineDecoration=null;
        isBreakLine=false;
        synchronized(cached) {
            for (int i = 0; i < cached.length; ++i) {
                if (cached[i] == null) {
                    cached[i] = this;
                    break;
                }
            }
        }
    }

    public void setScrollOffset(int offset) {
        this.scrollOffset = offset;
    }

    public void scrollOffset(int offset) {
        this.scrollOffset += offset;
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

    public void setLineDecoration(LineDecoration lineDecoration) {
        this.lineDecoration = lineDecoration;
    }

    public void setBreakLine(boolean breakLine) {
        isBreakLine = breakLine;
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public LineDecoration getLineDecoration() {
        return lineDecoration;
    }

    /**
     * 设置矩阵信息
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setRect(int left,int top,int right,int bottom){
        this.rect.set(left,top,right,bottom);
    }

    public Rect getRect() {
        return rect;
    }

    /**
     * 获得行高
     * @return
     */
    public int getLineHeight(){
        return lineBottom-lineTop;
    }

    public int getLineStart(){
        return lineStart;
    }

    public int getLineEnd(){
        return lineEnd;
    }

    public int getLineTop(){
        return lineTop;
    }
    public int getLineBottom(){
        return lineBottom;
    }

    public int getDecoratedScrollLineTop(){
        return scrollOffset+lineTop-rect.top;
    }

    public int getDecoratedScrollLineBottom(){
        return scrollOffset+lineBottom+rect.bottom;
    }

    public int getDecoratedLineTop(){
        return lineTop-rect.top;
    }

    public int getDecoratedLineBottom(){
        return lineBottom+rect.bottom;
    }

    public int getDecoratedScrollDescent() {
        return scrollOffset+lineDescent;
    }

    public int getDecoratedLeft() {
        return lineLeft-rect.left;
    }

    public Rect getDecoratedRect(){
        return rect;
    }

    public int getLineLeft() {
        return lineLeft;
    }


    public int getLineAlign() {
        return lineAlign;
    }

    public int getParagraph() {
        return paragraph;
    }

    public int getParagraphLine() {
        return paragraphLine;
    }

    public boolean isBreakLine() {
        return isBreakLine;
    }

    /**
     * 排版子控件
     */
    public void layoutViewSpan(CharSequence source,int left,int top) {
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
                } else if(Gravity.BOTTOM==newLineGravity){
                    viewSpan.setLayoutTop(top+newLineBottom-viewHeight);
                }
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
    public void draw(Canvas canvas, TextRender textRender,CharSequence text, TextPaint paint, TextPaint workPaint, int outerWidth, int outerHeight, boolean lineDecorate){
        if(lineDecorate){
            //绘制行装饰器
            lineDecoration.onLineDraw(canvas,this, outerWidth, outerHeight);
        }
        //绘制文本
        int decoratedLineBottom = getDecoratedScrollLineBottom();
        int baseline = decoratedLineBottom-lineDescent;
        if (!(text instanceof Spanned)) {
            canvas.drawText(text, lineStart, lineEnd, lineLeft, baseline, paint);
        } else {
            int top = getDecoratedScrollLineTop();
            int bottom = getDecoratedScrollLineBottom();
            Styled.drawText(canvas, textRender,text, lineStart, lineEnd, lineLeft, top, baseline, bottom, paint, workPaint, lineAlign,false);
        }
    }

}
