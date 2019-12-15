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
            if(text instanceof Spannable&&null!=selectForegroundColorSpan){
                Spannable spannable = (Spannable) text;
                spannable.removeSpan(selectForegroundColorSpan);
            }
            int line = layout.getLineForVertical((int)y);
            //记录选中起始位置
            selectStart = layout.getOffsetForHorizontal(line, x);
            //单字
//            layout.getSelectionPath(selectStart, selectStart + 1, selectPath)
            //重新设置选中span
            if(text instanceof Spannable){
                //设置背景
                Spannable spannable = (Spannable) text;
                selectForegroundColorSpan=new ForegroundColorSpan(Color.WHITE);
                spannable.setSpan(selectForegroundColorSpan, selectStart, selectStart+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            invalidate();
        }
    }

    /**
     * 更新选中区域
     */
    private void updateSelection(float x,float y) {
        Layout layout = getLayout();
        if (null != layout) {
            CharSequence text=layout.getText();
            if(text instanceof Spannable&&null!=selectForegroundColorSpan){
                Spannable spannable = (Spannable) text;
                spannable.removeSpan(selectForegroundColorSpan);
            }
            int line = layout.getLineForVertical((int)y);
            //记录选中起始位置
            int off = layout.getOffsetForHorizontal(line, x);
//            layout.getSelectionPath(selectStart, off + 1, selectPath);
            //重新设置选中span
            if(text instanceof Spannable){
                //设置背景
                selectForegroundColorSpan=new ForegroundColorSpan(Color.WHITE);
                Spannable spannable = (Spannable) text;
                spannable.setSpan(selectForegroundColorSpan, selectStart, off+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            invalidate();
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
