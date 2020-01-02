package com.cz.widget.supertextview.library.layout.adapter;

import android.util.SparseArray;

import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.layout.TextLayoutCallback;
import com.cz.widget.supertextview.library.layout.measurer.TextLayoutMeasurer;
import com.cz.widget.supertextview.library.layout.measurer.TextMeasurerInfo;
import com.cz.widget.supertextview.library.text.TextElement;
import com.cz.widget.supertextview.library.view.TextParent;

/**
 * 动态元素文本布局适配器
 */
public class DynamicTextLayoutAdapter extends TextLayoutAdapter {
    private final SparseArray<LayoutItem> layoutArray =new SparseArray<>();
    private final TextLayoutMeasurer textLayoutMeasurer;

    public DynamicTextLayoutAdapter(LineDecoration lineDecoration){
        textLayoutMeasurer = new TextLayoutMeasurer(lineDecoration);
    }

    public void setMeasureSpecs(int wSpec, int hSpec) {
        textLayoutMeasurer.setMeasureSpecs(wSpec,hSpec);
    }

    public void addLayoutItem(LayoutItem layoutItem){
        layoutArray.put(layoutItem.index,layoutItem);
    }

    @Override
    public TextElement[] measureText(TextParent textParent, TextMeasurerInfo textMeasurerInfo, int paragraph, int line) {
        LayoutItem layoutItem = layoutArray.get(textMeasurerInfo.start);
        if(null==layoutItem){
            return TextElement.EMPTY_ARRAY;
        } else {
            textLayoutMeasurer.setTextLayoutCallback(layoutItem.callback);
            return textLayoutMeasurer.measureTextElement(textParent,textMeasurerInfo,paragraph,line);
        }
    }

    @Override
    public int nextMeasureTransition(int start) {
        for(int i=0;i<layoutArray.size();i++){
            int position = layoutArray.keyAt(i);

        }
        return 0;
    }

    public class LayoutItem{
        private final int index;
        private final TextLayoutCallback callback;
        public LayoutItem(int index, TextLayoutCallback callback) {
            this.index = index;
            this.callback = callback;
        }
    }

}
