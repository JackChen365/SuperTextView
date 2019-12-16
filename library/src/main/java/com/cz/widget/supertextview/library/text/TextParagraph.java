package com.cz.widget.supertextview.library.text;

import android.graphics.Canvas;
import android.text.Spanned;
import android.text.TextPaint;
import android.view.Gravity;

import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.render.TextRender;
import com.cz.widget.supertextview.library.span.ViewSpan;

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
    public void layoutViewSpan(CharSequence source,int left,int top) {
        int lineTop = getLineTop();
        for(int i=0;i<lineCount;i++){
            TextLine textLine = textLines[i];
            textLine.layoutViewSpan(source,0,lineTop);
        }
    }

    @Override
    public void draw(Canvas canvas, TextRender textRender,CharSequence text, TextPaint paint, TextPaint workPaint, int outerWidth, int outerHeight, boolean lineDecorate) {
        if(0 < lineCount){
            canvas.save();
            int lineTop = getLineTop();
            canvas.translate(0,lineTop);
            LineDecoration lineDecoration = getLineDecoration();
            int scrollLineTop = getDecoratedScrollLineTop();
            for(int i=0;i<lineCount;i++){
                TextLine textLine = textLines[i];
                int textLineTop = textLine.getLineTop();
                int textLineBottom = textLine.getLineBottom();
                //顶部/底部 超出的不用绘制
                if(0 <= scrollLineTop+textLineBottom && outerHeight >= scrollLineTop+textLineTop){
                    //绘装饰器
                    lineDecoration.onParagraphLineDraw(canvas,textLine,outerWidth,outerHeight);
                    //绘文本行
                    textLine.draw(canvas,textRender,text,paint,workPaint,outerWidth,outerHeight,false);
                }
            }
            canvas.restore();
        }
    }
}
