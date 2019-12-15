package com.cz.widget.supertextview.library.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.cz.widget.supertextview.library.style.ReplacementSpan;
import com.cz.widget.supertextview.library.view.FlowLayoutParams;

/**
 * 包装View的span对象
 */
public class ViewSpan extends ReplacementSpan {
    /**
     * 记录添加的父容器
     */
    private ViewGroup parentView;
    /**
     * 当前操作子控件
     */
    private final View view;
    /**
     * 排版左侧位置,左侧位置在初次排版计算时决定
     */
    private int layoutLeft;
    /**
     * 排版顶部位置,顶部位置,是被添加到缓冲区时决定
     */
    private int layoutTop;
    /**
     * 排版布局位置
     */
    private int lineLayoutTop;
    private int topOffset;

    public ViewSpan(View view) {
        this.view = view;
    }

    @Override
    public int getSpanLayoutMode() {
        int layoutMode=FLOW;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if(layoutParams instanceof FlowLayoutParams){
            FlowLayoutParams flowLayoutParams = (FlowLayoutParams) layoutParams;
            layoutMode=flowLayoutParams.layoutMode;
        }
        return layoutMode;
    }

    /**
     * 获得操作view
     * @return
     */
    public View getView(){
        return view;
    }

    public int getWidth(){
        return view.getMeasuredWidth();
    }

    public int getHeight(){
        return view.getMeasuredHeight();
    }

    public int getLayoutTop() {
        return layoutTop;
    }

    /**
     * 将子控件添加到父控件
     */
    public void attachToView(){
        if(null==view.getParent()){
            //待添加容器
            parentView.addView(view);
        }
        View parentView = (View)view.getParent();
        int paddingLeft = parentView.getPaddingLeft();
        int paddingTop = parentView.getPaddingTop();
        int offsetTop=paddingTop+layoutTop+lineLayoutTop+ topOffset;
        view.layout(paddingLeft+layoutLeft, offsetTop,
                paddingLeft+layoutLeft+view.getMeasuredWidth(),
                offsetTop+view.getMeasuredHeight());
    }

    /**
     * 控件是否装载
     * @return
     */
    public boolean isAttached(){
        return null!=view.getParent();
    }

    /**
     * 将子控件添加到父控件
     */
    public void detachFromParent(){
        ViewParent parent = view.getParent();
        if(null!=parent){
            ViewGroup parentView=(ViewGroup)parent;
            parentView.removeView(view);
            this.parentView=parentView;
        }
    }

    public void setLayoutLeft(int layoutLeft){
        this.layoutLeft=layoutLeft;
    }

    public void setLayoutTop(int layoutTop){
        this.layoutTop=layoutTop;
    }

    public void setTopOffset(int offset){
        this.topOffset =offset;
    }

    public void setLineLayoutTop(int lineLayoutTop) {
        this.lineLayoutTop = lineLayoutTop;
    }


    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        int measuredHeight = view.getMeasuredHeight();
        if (fm != null) {
            fm.ascent = - measuredHeight;
            fm.descent = 0;

            fm.top = fm.ascent;
            fm.bottom = 0;
        }
        return view.getMeasuredWidth();
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        //暂不需要操作
    }
}
