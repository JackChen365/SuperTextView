package com.cz.widget.supertextview.library.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.cz.widget.supertextview.library.layout.Layout;
import com.cz.widget.supertextview.library.span.ForegroundColorSpan;
import com.cz.widget.supertextview.library.spannable.SpannableString;

public class SelectWordTextView extends RecyclerTextLayout {
    /**
     * 选中路径
     */
    private final Path selectPath= new Path();
    /**
     * 选中位置画笔
     */
    private final Paint selectPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 选中文本背景
     */
    private ForegroundColorSpan selectForegroundColorSpan=null;

    public SelectWordTextView(Context context) {
        super(context);
        selectPaint.setColor(Color.RED);
    }

    public SelectWordTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        selectPaint.setColor(Color.RED);
    }

    public SelectWordTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        selectPaint.setColor(Color.RED);
    }

    private void addSelection(float x,float y) {
        Layout layout = getLayout();
        if (null != layout) {
            boolean needInvalidate=false;
            CharSequence text = layout.getText();
            int line = layout.getLineForVertical((int)y);
            //如果选中控件,或者其他空余内容,返回-1
            int off = layout.getOffsetForHorizontal(x,y);
            if(-1 != off){
                char c = text.charAt(off);
                //移除上一个选中文本颜色
                if(text instanceof SpannableString) {
                    SpannableString spanned = (SpannableString) text;
                    if (null != selectForegroundColorSpan) {
                        needInvalidate=true;
                        spanned.removeSpan(selectForegroundColorSpan);
                    }
                }
                //置空path
                selectPath.reset();
                //如果是单词,开始检测
                if (Character.isLetter(c) || c == '-' || c == '\'') {
                    int lineStart = layout.getLineStart(line);
                    //向左偏移
                    int start = off;
                    while ((Character.isLetter(text.charAt(start - 1)) || text.charAt(start - 1) == '-' || text.charAt(start - 1) == '\'') && start >= lineStart) {
                        start--;
                    }
                    //向右偏移
                    int end = off;
                    int lineEnd = layout.getLineLatterEnd(line);
                    while ((Character.isLetter(text.charAt(end + 1)) || text.charAt(end + 1) == '-' || text.charAt(end + 1) == '\'') && end <= lineEnd) {
                        end++;
                    }
                    //单词
                    layout.getSelectionPath(start, end+1, selectPath);
                    //重新设置选中span
                    if(text instanceof SpannableString){
                        //设置背景
                        SpannableString spannable = (SpannableString) text;
                        selectForegroundColorSpan=new ForegroundColorSpan(Color.WHITE);
                        spannable.setSpan(selectForegroundColorSpan, start, end+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    needInvalidate=true;
                } else {
                    //如果字符串不为空,添加选中块
                    CharSequence selectText=text.subSequence(off,off+1);
                    if(0 < selectText.length()){
                        needInvalidate=true;
                        //单字
                        layout.getSelectionPath(off, off + 1, selectPath);
                    }
                }
            }
            if(needInvalidate){
                invalidate();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if(MotionEvent.ACTION_DOWN==action){
            float x = event.getX();
            float y = event.getY();
            addSelection(x-getPaddingLeft(), y-getPaddingTop());
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onDraw(Canvas canvas) {
        //绘制选中
        canvas.save();
        canvas.translate(getPaddingLeft()*1f,getPaddingTop()*1f);
        canvas.drawPath(selectPath,selectPaint);
        canvas.restore();
        super.onDraw(canvas);
    }
}
