package com.cz.widget.supertextview.sample.linedecoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.text.TextElement;
import com.cz.widget.supertextview.library.text.TextLayoutElement;
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
    public void getParagraphLineOffsets(TextLayoutElement textLayoutElement, int line, Rect outRect) {
        outRect.set(0,0,0,0);
//        outRect.set(lineParagraphPadding,lineParagraphPadding,lineParagraphPadding,lineParagraphPadding);
    }

    @Override
    public void onLineDraw(Canvas canvas, TextElement textElement, int width) {
        int decoratedScrollLineBottom = textElement.getDecoratedScrollLineBottom();
        canvas.drawLine(0f,decoratedScrollLineBottom*1f, width,decoratedScrollLineBottom*1f, linePaint);
    }

    @Override
    public void onParagraphLineDraw(Canvas canvas, TextElement textElement,int width) {
        //中间分隔线
        int lineLeft = textElement.getDecoratedLeft();
        int decoratedScrollLineBottom = textElement.getDecoratedScrollLineBottom();
        canvas.drawLine(lineLeft,decoratedScrollLineBottom*1f, width,decoratedScrollLineBottom*1f, lineParagraphPaint);
    }
}
