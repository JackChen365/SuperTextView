package com.cz.widget.supertextview.library.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.cz.widget.supertextview.library.text.TextElement;
import com.cz.widget.supertextview.library.text.TextLayoutElement;

/**
 * 默认的空的行装饰器
 */
public class DefaultLineDecoration extends LineDecoration {

    @Override
    public void getLineOffsets(int paragraph, int line, Rect outRect) {
    }

    @Override
    public void getParagraphLineOffsets(TextLayoutElement textLayoutElement, int line, Rect outRect) {
    }

    @Override
    public void onLineDraw(Canvas canvas, TextElement textElement, int width) {
    }

    @Override
    public void onParagraphLineDraw(Canvas canvas, TextElement textElement, int width) {
    }


}
