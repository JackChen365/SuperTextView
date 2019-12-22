package com.cz.widget.supertextview.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.cz.widget.supertextview.library.R;
import com.cz.widget.supertextview.library.span.ViewSpan;
import com.cz.widget.supertextview.library.style.ReplacementSpan;

/**
 * 自定义LayoutParams对象,对于设置子控件的排版规则
 * @attr R.attr.layout_flow
 */
public class FlowLayoutParams extends ViewGroup.MarginLayoutParams {
    public int layoutMode = ReplacementSpan.FLOW;
    /**
     * 依附的span对象
     */
    public ViewSpan token;

    public FlowLayoutParams(Context c, AttributeSet attrs) {
        super(c, attrs);
        TypedArray typedArray = c.obtainStyledAttributes(attrs, new int[]{R.attr.layout_mode});
        layoutMode =typedArray.getInt(0,ReplacementSpan.FLOW);
        typedArray.recycle();
    }

    public FlowLayoutParams(int width, int height) {
        super(width, height);
    }

    public FlowLayoutParams(ViewGroup.MarginLayoutParams source) {
        super(source);
    }

    public FlowLayoutParams(ViewGroup.LayoutParams source) {
        super(source);
    }
}
