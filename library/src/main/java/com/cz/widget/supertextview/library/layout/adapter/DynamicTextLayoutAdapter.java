package com.cz.widget.supertextview.library.layout.adapter;

import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.layout.measurer.TextLayoutMeasurer;

import java.util.LinkedList;

/**
 * 动态的文本布局适配器
 */
public abstract class DynamicTextLayoutAdapter implements TextLayoutAdapter {
    private static final int NO_POSITION=-1;
    private final LinkedList<Integer> positionList=new LinkedList<>();
    private final TextLayoutMeasurer textLayoutMeasurer;

    public DynamicTextLayoutAdapter(LineDecoration lineDecoration){
        textLayoutMeasurer = new TextLayoutMeasurer(lineDecoration);
    }

    public void setMeasureSpecs(int wSpec, int hSpec) {
        textLayoutMeasurer.setMeasureSpecs(wSpec,hSpec);
    }

    public void addPosition(int position){
        positionList.add(position);
    }

    public int nextPosition(){
        Integer position = positionList.pollLast();
        return null==position? NO_POSITION : position;
    }

}
