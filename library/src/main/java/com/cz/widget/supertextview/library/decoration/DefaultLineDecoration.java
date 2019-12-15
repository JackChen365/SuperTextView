package com.cz.widget.supertextview.library.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.cz.widget.supertextview.library.text.TextLine;
import com.cz.widget.supertextview.library.text.TextParagraph;

/**
 * 默认的空的行装饰器
 */
public class DefaultLineDecoration extends LineDecoration {

    @Override
    public void getLineOffsets(int paragraph, int line, Rect outRect) {
    }

    @Override
    public void getParagraphLineOffsets(TextParagraph textParagraph, int line, Rect outRect) {
    }

    @Override
    public void onLineDraw(Canvas canvas, TextLine textLine, int width, int height) {
    }

    @Override
    public void onParagraphLineDraw(Canvas canvas, TextLine textLine, int width, int height) {
    }


}
