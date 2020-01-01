package com.cz.widget.supertextview.library.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;

import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.render.TextRender;
import com.cz.widget.supertextview.library.view.TextParent;

/**
 * 文本布局元素
 */
public class TextLayoutElement extends TextElement {
    /**
     * 缓存的textLine对象
     */
    public TextElement[] textElements;
    /**
     * 当前子段落行数
     */
    public int lineCount=0;

    @Override
    public void setScrollOffset(int offset) {
        super.setScrollOffset(offset);
        //子行信息滚动偏移量
        if(0 < lineCount){
            for(int i=0;i<lineCount;i++){
                TextElement textElement = textElements[i];
                textElement.setScrollOffset(offset);
            }
        }
    }

    @Override
    public int getLineStart() {
        int start=0;
        if(0 < lineCount){
            TextElement textElement = textElements[0];
            start=textElement.getLineStart();
        }
        return start;
    }

    @Override
    public int getLineEnd() {
        int end=0;
        if(0 < lineCount){
            TextElement textElement = textElements[lineCount-1];
            end=textElement.getLineEnd();
        }
        return end;
    }


    @Override
    public void recycler() {
        //段落本身信息不回收,仅回收行信息
        if(0 < lineCount){
            for(int i=0;i<lineCount;i++){
                TextElement textElement = textElements[i];
                textElement.recycler();
            }
        }
    }

    /**
     * 排版子控件
     */
    public void layoutViewSpan(TextParent parentView, CharSequence source, int left, int top) {
        int lineTop = getLineTop();
        for(int i=0;i<lineCount;i++){
            TextElement textElement = textElements[i];
            textElement.layoutViewSpan(parentView,source,0,lineTop);
        }
    }
    public int getLineLeft(int line){
        return textElements[line].getLineLeft();
    }

    public int getLineLatterStart(int line){
        return textElements[line].getLineStart();
    }

    public int getLineLatterEnd(int line){
        return textElements[line].getLineEnd();
    }

    public int getDecoratedScrollLineTop(int line){
        return textElements[line].getDecoratedLineTop();
    }

    public int getDecoratedScrollLineBottom(int line){
        return textElements[line].getDecoratedLineBottom();
    }

    public int getLineCount(){
        return lineCount;
    }

    @Override
    public void draw(Canvas canvas, TextRender textRender, CharSequence text, TextPaint paint, TextPaint workPaint, Paint.FontMetricsInt fontMetricsInt, int outerWidth, boolean lineDecorate) {
        if(0 < lineCount){
            //绘装饰器
            LineDecoration lineDecoration = getLineDecoration();
            lineDecoration.onLineDraw(canvas,this,outerWidth);
            //绘制段落内文本行信息
            canvas.save();
            int lineTop = getLineTop();
            canvas.translate(0,lineTop);
            int scrollLineTop = getDecoratedScrollLineTop();
            for(int i=0;i<lineCount;i++){
                TextElement textElement = textElements[i];
                int textLineBottom = textElement.getLineBottom();
                //顶部/底部 超出的不用绘制
                if(0 <= scrollLineTop+textLineBottom){
                    //绘文本行
                    textElement.draw(canvas,textRender,text,paint,workPaint,fontMetricsInt,outerWidth,false);
                }
            }
            canvas.restore();
        }
    }
}
