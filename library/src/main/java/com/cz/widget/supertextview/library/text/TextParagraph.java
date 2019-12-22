package com.cz.widget.supertextview.library.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.ViewGroup;

import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.render.TextRender;

/**
 * 文本段落信息,可理解为多行信息汇总
 */
public class TextParagraph extends TextLine{
    private static final String TAG="TextParagraph";
    /**
     * 缓存的textLine对象
     */
    public TextLine[] textLines;
    /**
     * 当前子段落行数
     */
    public int lineCount=0;

    public TextParagraph() {
        //段落默认为断行
        setBreakLine(true);
    }

    @Override
    public void setScrollOffset(int offset) {
        super.setScrollOffset(offset);
        //子行信息滚动偏移量
        if(0 < lineCount){
            for(int i=0;i<lineCount;i++){
                TextLine textLine = textLines[i];
                textLine.setScrollOffset(offset);
            }
        }
    }

    @Override
    public int getLineStart() {
        int start=0;
        if(0 < lineCount){
            TextLine textLine = textLines[0];
            start=textLine.getLineStart();
        }
        return start;
    }

    @Override
    public int getLineEnd() {
        int end=0;
        if(0 < lineCount){
            TextLine textLine = textLines[lineCount-1];
            end=textLine.getLineEnd();
        }
        return end;
    }


    @Override
    public void recycler() {
        //段落本身信息不回收,仅回收行信息
        if(0 < lineCount){
            for(int i=0;i<lineCount;i++){
                TextLine textLine = textLines[i];
                textLine.recycler();
            }
        }
    }

    /**
     * 排版子控件
     */
    public void layoutViewSpan(ViewGroup parentView,CharSequence source,int left,int top) {
        int lineTop = getLineTop();
        for(int i=0;i<lineCount;i++){
            TextLine textLine = textLines[i];
            textLine.layoutViewSpan(parentView,source,0,lineTop);
        }
    }
    public int getLineLeft(int line){
        return textLines[line].getLineLeft();
    }

    public int getLineLatterStart(int line){
        return textLines[line].getLineStart();
    }

    public int getLineLatterEnd(int line){
        return textLines[line].getLineEnd();
    }

    public int getDecoratedScrollLineTop(int line){
        return textLines[line].getDecoratedLineTop();
    }

    public int getDecoratedScrollLineBottom(int line){
        return textLines[line].getDecoratedLineBottom();
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
                TextLine textLine = textLines[i];
                int textLineBottom = textLine.getLineBottom();
                //顶部/底部 超出的不用绘制
                if(0 <= scrollLineTop+textLineBottom){
                    //绘装饰器
                    lineDecoration.onParagraphLineDraw(canvas,textLine,outerWidth);
                    //绘文本行
                    textLine.draw(canvas,textRender,text,paint,workPaint,fontMetricsInt,outerWidth,false);
                }
            }
            canvas.restore();
        }
    }
}
