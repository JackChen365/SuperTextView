package com.cz.widget.supertextview.sample.linedecoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.text.TextLine;
import com.cz.widget.supertextview.library.text.TextParagraph;
import com.cz.widget.supertextview.sample.R;

/**
 * 行高亮装饰器
 */
public class HighlightLineDecoration extends LineDecoration {
    private final Paint linePaint =new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint lineParagraphPaint =new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Context context;
    private final int lineParagraphPadding;
    private final int linePadding;
    public HighlightLineDecoration(Context context) {
        this.context = context.getApplicationContext();
        this.lineParagraphPadding=(int)context.getResources().getDimension(R.dimen.lineParagraphPadding);
        this.linePadding=(int)context.getResources().getDimension(R.dimen.linePadding);

        linePaint.setStrokeWidth(1f);
        linePaint.setColor(Color.RED);
        linePaint.setStyle(Paint.Style.STROKE);

        lineParagraphPaint.setStrokeWidth(1f);
        lineParagraphPaint.setColor(Color.GREEN);
        lineParagraphPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void getLineOffsets(int paragraph, int line, Rect outRect) {
        outRect.set(linePadding,linePadding,linePadding,linePadding);
    }

    @Override
    public void getParagraphLineOffsets(TextParagraph textParagraph, int line, Rect outRect) {
        outRect.set(0,0,0,0);
//        outRect.set(lineParagraphPadding,lineParagraphPadding,lineParagraphPadding,lineParagraphPadding);
    }

    @Override
    public void onLineDraw(Canvas canvas, TextLine textLine,int width) {
        int decoratedScrollLineBottom = textLine.getDecoratedScrollLineBottom();
        canvas.drawLine(0f,decoratedScrollLineBottom*1f, width,decoratedScrollLineBottom*1f, linePaint);
    }

    @Override
    public void onParagraphLineDraw(Canvas canvas, TextLine textLine,int width) {
        if(textLine.isBreakLine()){
            //中间分隔线
            int lineLeft = textLine.getDecoratedLeft();
            int decoratedScrollLineBottom = textLine.getDecoratedScrollLineBottom();
            canvas.drawLine(lineLeft,decoratedScrollLineBottom*1f, width,decoratedScrollLineBottom*1f, lineParagraphPaint);
        }
        //文本底部分隔线
//        Rect decoratedRect = textLine.getDecoratedRect();
//        int scrollOffset = textLine.getScrollOffset();
//        int lineBottom = textLine.getLineBottom();
//        canvas.drawLine(decoratedRect.left,scrollOffset+lineBottom*1f, width-decoratedRect.right,scrollOffset+lineBottom*1f,linePaint);
    }



}
