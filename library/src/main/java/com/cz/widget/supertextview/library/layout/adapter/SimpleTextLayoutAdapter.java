package com.cz.widget.supertextview.library.layout.adapter;

import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.layout.measurer.TextMeasurerInfo;
import com.cz.widget.supertextview.library.layout.measurer.TextStyleLineMeasurer;
import com.cz.widget.supertextview.library.text.TextElement;
import com.cz.widget.supertextview.library.view.TextParent;

public class SimpleTextLayoutAdapter extends TextLayoutAdapter {
    private final TextStyleLineMeasurer textStyleMeasurer;
    public SimpleTextLayoutAdapter(LineDecoration lineDecoration){
        textStyleMeasurer=new TextStyleLineMeasurer(lineDecoration);
    }

    public void setMeasureSpecs(int wSpec, int hSpec) {
        textStyleMeasurer.setMeasureSpecs(wSpec,hSpec);
    }

    @Override
    public TextElement[] measureText(TextParent textParent, TextMeasurerInfo textMeasurerInfo, int paragraph, int line) {
        return textStyleMeasurer.measureTextElement(textParent, textMeasurerInfo, paragraph, line);
    }

    @Override
    public int nextMeasureTransition(int start) {
        return 0;
    }
}
