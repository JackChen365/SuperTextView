package com.cz.widget.supertextview.library.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Spannable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.cz.widget.supertextview.library.layout.Layout;
import com.cz.widget.supertextview.library.span.ForegroundColorSpan;
import com.cz.widget.supertextview.library.spannable.SpannableString;

public class SelectTextView  extends RecyclerTextLayout {
    /**
     * 选中路径
     */
    private final Path selectPath= new Path();
    /**
     * 选中起始位置
     */
    private int selectStart=0;
    /**
     * 选中位置画笔
     */
    private final Paint selectPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 选中文本背景
     */
    private ForegroundColorSpan selectForegroundColorSpan=null;

    public SelectTextView(Context context) {
        super(context);
        selectPaint.setColor(Color.RED);
    }

    public SelectTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        selectPaint.setColor(Color.RED);
    }

    public SelectTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        selectPaint.setColor(Color.RED);
    }

    private void addSelection(float x,float y) {
        Layout layout = getLayout();
        if (null != layout) {
            CharSequence text = layout.getText();
            if(text instanceof SpannableString&&null!=selectForegroundColorSpan){
                SpannableString spannable = (SpannableString) text;
                spannable.removeSpan(selectForegroundColorSpan);
            }
            //记录选中起始位置
            selectStart = layout.getOffsetForHorizontal(x,y);
            if(-1!=selectStart){
                //单字
                layout.getSelectionPath(selectStart, selectStart + 1, selectPath);
                //重新设置选中span
                if(text instanceof SpannableString){
                    //设置背景
                    SpannableString spannable = (SpannableString) text;
                    selectForegroundColorSpan=new ForegroundColorSpan(Color.WHITE);
                    spannable.setSpan(selectForegroundColorSpan, selectStart, selectStart+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                invalidate();
            }
        }
    }

    /**
     * 更新选中区域
     */
    private void updateSelection(float x,float y) {
        Layout layout = getLayout();
        if (null != layout) {
            CharSequence text=layout.getText();
            //记录选中起始位置
            int off = layout.getOffsetForHorizontal(x,y);
            if(-1!=selectStart&&-1!=off){
                int start=Math.min(selectStart,off);
                int end=Math.max(selectStart,off);
                layout.getSelectionPath(start, end, selectPath);
                //重新设置选中span
                if(text instanceof SpannableString){
                    //设置背景
                    SpannableString spannable = (SpannableString) text;
                    spannable.removeSpan(selectForegroundColorSpan);
                    //如果start与end相同,会导致span选中整行???
                    if(start != end){
                        selectForegroundColorSpan=new ForegroundColorSpan(Color.WHITE);
                        spannable.setSpan(selectForegroundColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                invalidate();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        float x = event.getX();
        float y = event.getY();
        if(MotionEvent.ACTION_DOWN==action){
            addSelection(x-getPaddingLeft(), y-getPaddingTop());
        } else if(MotionEvent.ACTION_MOVE==action){
            updateSelection(x-getPaddingLeft(), y-getPaddingTop());
        }
        return true;
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
