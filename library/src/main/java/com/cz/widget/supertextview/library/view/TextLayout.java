package com.cz.widget.supertextview.library.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.Spanned;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;

import com.cz.widget.supertextview.library.R;
import com.cz.widget.supertextview.library.decoration.DefaultLineDecoration;
import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.layout.Layout;
import com.cz.widget.supertextview.library.layout.StaticLayout;
import com.cz.widget.supertextview.library.render.Callback;
import com.cz.widget.supertextview.library.render.DefaultTextRender;
import com.cz.widget.supertextview.library.render.TextRender;
import com.cz.widget.supertextview.library.span.ViewSpan;
import com.cz.widget.supertextview.library.spannable.SpannableString;

/**
 *
 * 一个文本布局
 * 1. 支持TextView span设计
 * 2. 支持View以span形式存在
 * 3. 支持文本对元素的流式排版
 *
 * 强化功能
 * 1. 优化排版运算
 * 2. 优化渲染
 * 3. 优化动态修改
 */
public class TextLayout extends ViewGroup implements Callback {
    private static final String TAG="TextLayout";
    /**
     * 绘制文本画笔对象
     */
    private final TextPaint textPaint=new TextPaint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 绘制文本
     */
    private SpannableString text;
    /**
     * 文本排版layout对象
     */
    private Layout layout;
    /**
     * 文本渲染器
     */
    private TextRender textRender=new DefaultTextRender();
    //行装饰器
    private LineDecoration lineDecoration=new DefaultLineDecoration();

    public TextLayout(Context context) {
        this(context,null,0);
    }

    public TextLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TextLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);

        textPaint.setTextSize(getResources().getDimension(R.dimen.textSize));
        textPaint.setColor(Color.BLACK);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TextLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if(0 < getChildCount()){
            throw new IllegalArgumentException("TextLayout Can't not add view in xml You should use spannableString to add ViewSpan!");
        }
    }

    /**
     * 设置文本
     */
    public void setText(SpannableString text){
        this.text=text;
        ViewSpan[] viewSpans = text.getSpans(0, text.length(), ViewSpan.class);
        for(int i=0;i<viewSpans.length;i++){
            ViewSpan viewSpan = viewSpans[i];
            View view = viewSpan.getView();
            addViewInternal(view);
        }
        requestLayout();
    }

    /**
     * 设置行装饰器
     * @param lineDecoration
     */
    public void setLineDecoration(LineDecoration lineDecoration){
        this.lineDecoration=lineDecoration;
    }

    public void setTextRender(TextRender textRender){
        textRender.setTarget(this);
        this.textRender=textRender;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量子孩子尺寸
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        //初始化Layout
        int measuredWidth = getMeasuredWidth();
        if(null!=text&&(null==layout||text!=layout.getText())){
            layout = new StaticLayout(text, textPaint,lineDecoration ,textRender,measuredWidth - getPaddingLeft() - getPaddingRight(), 0f, Gravity.CENTER);
        }
        //重新设置尺寸
        if(null!=layout){
            final int layoutHeight=layout.getHeight();
            setMeasuredDimension(measuredWidth,getPaddingTop()+layoutHeight+getPaddingBottom());
        }
    }

    /**
     * 添加一个View
     * @param child
     */
    public void addView(View child,int start,int end){
        //添加span对象
        text.setSpan(new ViewSpan(child),start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //添加view到视图
        addViewInternal(child);
    }

    /**
     * 内部添加view,不允许外部添加
     * @param child
     */
    private void addViewInternal(View child) {
        //添加view
        LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = generateDefaultLayoutParams();
            if (params == null) {
                throw new IllegalArgumentException("generateDefaultLayoutParams() cannot return null");
            }
        }
        super.addView(child, -1, params);
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        throw new IllegalArgumentException("TextLayout Can't not add view in code! You should use spannableString to add ViewSpan!");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    /**
     * 获得子控件描述信息
     * @param childView
     * @return
     */
    private CharSequence getChildViewContentDescription(View childView){
        CharSequence contentDescription = childView.getContentDescription();
        if(null==contentDescription){
            contentDescription=" ";
        }
        return contentDescription;
    }

    public CharSequence getText(){
        return text;
    }

    /**
     * 清除文本
     */
    public void clear(){
        this.text=null;
        removeAllViews();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制选中
        if(null!=layout){
            canvas.save();
            canvas.translate(getPaddingLeft()*1f,getPaddingTop()*1f);
            //绘制行装饰器
//            lineDecoration.onLineDraw(canvas,this, width, height);
            layout.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new FlowLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FlowLayoutParams(getContext(),attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new FlowLayoutParams(new MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

}
