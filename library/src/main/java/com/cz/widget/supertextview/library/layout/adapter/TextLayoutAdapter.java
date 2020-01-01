package com.cz.widget.supertextview.library.layout.adapter;

import com.cz.widget.supertextview.library.layout.measurer.TextMeasurerInfo;
import com.cz.widget.supertextview.library.view.TextParent;

/**
 * 文本布局适配器
 * 1. 检测文本计算逻辑介入时机
 * 2. 负责上层文本运算逻辑
 * 3. 输出渲染对象:TextElement
 */
public interface TextLayoutAdapter {
    /**
     * 测量文本
     * @return
     */
    int measureText(TextParent textParent, TextMeasurerInfo textMeasurerInfo, int paragraph, int line);
}
