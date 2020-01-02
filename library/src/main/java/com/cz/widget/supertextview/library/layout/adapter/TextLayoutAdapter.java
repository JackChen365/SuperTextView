package com.cz.widget.supertextview.library.layout.adapter;

import com.cz.widget.supertextview.library.layout.measurer.TextMeasurerInfo;
import com.cz.widget.supertextview.library.text.TextElement;
import com.cz.widget.supertextview.library.view.TextParent;

/**
 * 文本布局适配器
 * 1. 检测文本计算逻辑介入时机
 * 2. 负责上层文本运算逻辑
 * 3. 输出渲染对象:TextElement
 */
public abstract class TextLayoutAdapter {
    /**
     * 测量文本
     * @return
     */
    public abstract TextElement[] measureText(TextParent textParent, TextMeasurerInfo textMeasurerInfo, int paragraph, int line);
    /**
     * 返回下次需要接手测量位置,如内部有7个布局,插入位置分别为:14,59,75,85.92 当前排版位置为24,而下一个位置为59
     * 这个类似于Span的检索机制,但是不同处在于.span是修饰样式,而本类的作用是插入自定义布局体.
     * @param start
     * @return
     */
    public abstract int nextMeasureTransition(int start);
}
