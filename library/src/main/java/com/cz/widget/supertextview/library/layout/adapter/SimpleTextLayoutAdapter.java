package com.cz.widget.supertextview.library.layout.adapter;

import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.layout.measurer.TextMeasurerInfo;
import com.cz.widget.supertextview.library.layout.measurer.TextStyleLineMeasurer;
import com.cz.widget.supertextview.library.text.TextElement;
import com.cz.widget.supertextview.library.view.TextParent;

public class SimpleTextLayoutAdapter implements TextLayoutAdapter {
    private final TextStyleLineMeasurer textStyleMeasurer;
    public SimpleTextLayoutAdapter(LineDecoration lineDecoration){
        textStyleMeasurer=new TextStyleLineMeasurer(lineDecoration);
    }

    public void setMeasureSpecs(int wSpec, int hSpec) {
        textStyleMeasurer.setMeasureSpecs(wSpec,hSpec);
    }

    @Override
    public int measureText(TextParent textParent, TextMeasurerInfo textMeasurerInfo, int paragraph, int line) {
        TextElement[] textElements = textStyleMeasurer.measureTextElement(textParent, textMeasurerInfo, paragraph, line);
        int lineCount = textStyleMeasurer.getLineCount();
        TextElement textElement = textElements[lineCount - 1];
        int start = textMeasurerInfo.start;
        int end = textElement.getLineEnd();
        //获取使用的字符数量
        return end-start;
    }
}
