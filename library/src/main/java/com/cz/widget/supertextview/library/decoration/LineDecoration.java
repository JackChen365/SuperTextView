package com.cz.widget.supertextview.library.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.cz.widget.supertextview.library.text.TextLine;
import com.cz.widget.supertextview.library.text.TextParagraph;

/**
 * 代码行装饰器
 * 装饰器暂时不允许添加多个,因为扩大了单行计算的范围,多个复合叠加,会使运算变得更加复杂.
 */
public abstract class LineDecoration {

    /**
     * 获得行信息偏移
     * @param paragraph 段落
     * @param line 段落所在行
     * @param outRect
     */
    public abstract void getLineOffsets(int paragraph, int line, Rect outRect);

    /**
     * 获得段落内条件偏移
     * @param outRect
     */
    public abstract void getParagraphLineOffsets(TextParagraph textParagraph, int line, Rect outRect);

    /**
     * 行绘制信息
     * @param canvas
     */
    public abstract void onLineDraw(Canvas canvas, TextLine textLine,int width);

    /**
     * 段落行绘制信息
     * @param canvas
     */
    public abstract void onParagraphLineDraw(Canvas canvas, TextLine textLine,int width);


}
