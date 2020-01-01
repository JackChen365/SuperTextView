package com.cz.widget.supertextview.library.layout;

import android.graphics.Rect;

import com.cz.widget.supertextview.library.layout.measurer.TextLayoutMeasurer;
import com.cz.widget.supertextview.library.layout.measurer.TextMeasurerInfo;
import com.cz.widget.supertextview.library.layout.view.TextParagraphLayout;
import com.cz.widget.supertextview.library.text.TextElement;
import com.cz.widget.supertextview.library.view.TextParent;

/**
 * 文本测量回调对象
 * @see TextParagraphLayout 文本段落布局.用以支持段落形式的文本排版
 */
public interface TextLayoutCallback {
    /**
     * 当文本测量时
     * @param textParent
     * @param textMeasurerInfo
     * @param paragraph
     * @param line
     */
    TextElement onMeasureText(TextParent textParent, TextLayoutMeasurer textLayoutMeasurer, TextMeasurerInfo textMeasurerInfo, int paragraph, int line);

    /**
     * 当生成TextElement元素时回调
     * @param textElement
     * @return 返回true代表中断整个计算
     */
    boolean onGenerateTextElement(TextElement textElement);
}
