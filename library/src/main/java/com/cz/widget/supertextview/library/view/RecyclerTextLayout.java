package com.cz.widget.supertextview.library.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EdgeEffect;
import android.widget.OverScroller;

import androidx.annotation.RequiresApi;
import com.cz.widget.supertextview.library.R;
import com.cz.widget.supertextview.library.decoration.DefaultLineDecoration;
import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.layout.Layout;
import com.cz.widget.supertextview.library.layout.RecyclerStaticLayout;
import com.cz.widget.supertextview.library.render.DefaultTextRender;
import com.cz.widget.supertextview.library.render.TextRender;

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
 *

 */
public class RecyclerTextLayout extends ViewGroup{
    private static final String TAG="RecyclerTextLayout";
    /**
     * 绘制文本画笔对象
     */
    private final TextPaint textPaint=new TextPaint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 绘制文本
     */
    private CharSequence text;
    /**
     * 文本排版layout对象
     */
    private RecyclerStaticLayout layout;
    /**
     * 文本渲染器
     */
    private TextRender textRender=new DefaultTextRender();
    //行装饰器
    private LineDecoration lineDecoration=new DefaultLineDecoration();
    //边缘阴影
    private EdgeEffect startEdge;
    private EdgeEffect endEdge;
    //手势拖动--------
    private boolean isBeingDragged=false;
    private int touchSlop = 0;
    private float lastMotionY = 0f;
    private VelocityTracker velocityTracker= null;
    private int minimumVelocity = 0;
    private int maximumVelocity = 0;
    //行装饰器
    private ViewFlipper viewFlipper =new ViewFlipper();

    public RecyclerTextLayout(Context context) {
        this(context,null,0);
    }

    public RecyclerTextLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RecyclerTextLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        textRender.setTarget(this);
        startEdge = new EdgeEffect(context);
        endEdge = new EdgeEffect(context);

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        touchSlop = configuration.getScaledTouchSlop();
        minimumVelocity = configuration.getScaledMinimumFlingVelocity();
        maximumVelocity = configuration.getScaledMaximumFlingVelocity();

        textPaint.setTextSize(getResources().getDimension(R.dimen.textSize));
        textPaint.setColor(Color.BLACK);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RecyclerTextLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

    public TextRender getTextRender() {
        return textRender;
    }

    /**
     * 设置文本
     */
    public void setText(CharSequence text){
        this.text=text;
        requestLayout();
    }

    public Layout getLayout() {
        return layout;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //初始化Layout
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        final int outerWidth=measuredWidth - getPaddingLeft() - getPaddingRight();
        int outerHeight=measuredHeight-getPaddingTop()-getPaddingBottom();
        if(outerHeight<0){
            outerHeight=1;
        }
        if(null!=text){
            //文本不同重新初始化
            if(null==layout||text!=layout.getText()){
                layout = new RecyclerStaticLayout(text, textPaint, lineDecoration,textRender, outerWidth,outerHeight, 0f, Gravity.CENTER);
                layout.setMeasureSpecs(widthMeasureSpec,heightMeasureSpec);
            } else if(outerHeight!=layout.getLayoutHeight()){
                //todo 外部高度变化,重新加载
//                layout.setLayoutHeight(outerHeight);
            }
        }
//        //重新设置尺寸
        if(null!=layout&&measuredHeight!=(layout.getHeight()+getPaddingTop()+getPaddingBottom())){
            final int layoutHeight=layout.getHeight();
            setMeasuredDimension(measuredWidth,getPaddingTop()+layoutHeight+getPaddingBottom());
        }
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        FlowLayoutParams flowLayoutParams = (FlowLayoutParams) params;
        if(null==flowLayoutParams.token){
            throw new IllegalArgumentException("TextLayout Can't not add view in code! You should use spannableString to add ViewSpan!");
        }
        super.addView(child,index,params);
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
            //绘制越界边缘阴影
//            drawEdgeAffect(canvas);
            canvas.save();
            canvas.translate(getPaddingLeft()*1f,getPaddingTop()*1f);
            layout.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * 绘制越界边缘阴影
     * @param canvas
     */
    private void drawEdgeAffect(Canvas canvas) {
        int paddingLeft = getPaddingLeft();
        int width = getWidth() - paddingLeft- getPaddingRight();
        int height = getHeight();
        int scrollY = layout.getScrollY();
        if (!startEdge.isFinished()) {//动画是否已经结束
            int restoreCount = canvas.save();
            //画布向右平移，如果View有向下超过0的偏移量就要再向上偏移，超过上边界的平移量
            canvas.translate(paddingLeft * 1f, Math.min(0, -scrollY) * 1f);
            //设置效果的展示范围（内容的宽度，和View的高度）
            startEdge.setSize(width, height);
            //绘制边缘效果图，如果绘制需要进行动画效果返回true
            if (startEdge.draw(canvas)) {
                postInvalidateOnAnimation(this);//进行动画
            }
            canvas.restoreToCount(restoreCount);
        }
        if (!endEdge.isFinished()) {
            int restoreCount = canvas.save();
            //下面两行代码的作用就是把画布平移旋转到底部展示，并让效果向上显示
            canvas.translate((paddingLeft - width), (Math.max(height, -scrollY) + height));
            canvas.rotate(180f, width * 1f, 0f);
            endEdge.setSize(width, height);
            if (endEdge.draw(canvas)) {
                postInvalidateOnAnimation(this);
            }
            canvas.restoreToCount(restoreCount);
        }
    }

    void postInvalidateOnAnimation(View view) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.postInvalidateOnAnimation();
        } else {
            view.postInvalidate();
        }
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new FlowLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FlowLayoutParams(getContext(),attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new FlowLayoutParams(new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    //拖动相关-------------------------------------------------------------------------------------------------------

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(null==layout){
            return false;
        }
        int action = ev.getActionMasked();
        if (super.onInterceptTouchEvent(ev)) {
            return true;
        }
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            releaseDrag();
            return false;
        }
        if (action != MotionEvent.ACTION_DOWN && isBeingDragged) {
            return true;
        }
        switch (action) {
            case MotionEvent.ACTION_MOVE:{
                float x = ev.getX();
                float y = ev.getY();
                float dy = y - lastMotionY;
                boolean canScroll = this.childCanScroll(this, (int)dy);
                if (dy != 0.0f && canScroll) {
                    this.lastMotionY = y;
                    return false;
                }
                if (Math.abs(dy) > touchSlop/2) {
                    isBeingDragged = true;
                    ViewParent parent = getParent();
                    if(null!=parent){
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_DOWN:{
                viewFlipper.abortAnimation();
                lastMotionY = ev.getY();
                break;
            }
            case MotionEvent.ACTION_UP: case MotionEvent.ACTION_CANCEL:{
                releaseDrag();
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(null==layout){
            return false;
        }
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
        int action = event.getActionMasked();
        if(MotionEvent.ACTION_DOWN==action) {
            lastMotionY = event.getY();
//            ViewParent parent = getParent();
//            if(null!=parent) {
//                parent.requestDisallowInterceptTouchEvent(true);
//            }
        } else if(MotionEvent.ACTION_MOVE==action) {
            float x = event.getX();
            float y = event.getY();
            float yDiff = y - lastMotionY;
            if (!isBeingDragged && Math.abs(yDiff) > touchSlop) {
                isBeingDragged = true;
                lastMotionY = y;
                ViewParent parent = getParent();
                if (null != parent) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
            }
            if (isBeingDragged) {
                lastMotionY = y;
                //滚动偏移距离
                int height = getHeight();
                viewFlipper.scrollVerticallyBy(-yDiff);
                //检测边缘阴影
                int scrollY = layout.getScrollY();
                if (-scrollY <= 0) {
                    //在顶部
//                    startEdge.onPull((int)(-yDiff / height), 1f-(int)(x / getWidth()));
//                    if (!endEdge.isFinished()) {
//                        endEdge.onRelease();
//                    }
//                    invalidate();
                } else if (!layout.canVerticalScroll()) {
//                    //在底部
//                    endEdge.onPull((int)(-yDiff / height), 1f-(int)(x / getWidth()));
//                    if (!startEdge.isFinished()) {
//                        startEdge.onRelease();
//                    }
//                    invalidate();
                }
            }
        } else if(MotionEvent.ACTION_UP==action){
            if (isBeingDragged) {
                velocityTracker.computeCurrentVelocity(1000,maximumVelocity);
                float initialVelocityY = velocityTracker.getYVelocity(0);
                viewFlipper.fling(0f,-initialVelocityY);
                releaseDrag();
            }
        } else if(MotionEvent.ACTION_CANCEL==action){
            releaseDrag();
        }
        return true;
    }

    /**
     * 检测子控件是否可以滚动
     */
    private boolean childCanScroll(View v, int dy){
        boolean canScroll=this==v ? false:v.canScrollVertically(-dy);
        if (v instanceof ViewGroup) {
            ViewGroup parent=(ViewGroup)v;
            int count = parent.getChildCount();
            for (int i=count-1;i>=0;i--) {
                //遍历子控件检测是否有控件可以遍历
                View child = parent.getChildAt(i);
                if (childCanScroll(child,dy)){
                    return true;
                }
            }
        }
        return canScroll;
    }

    /**
     * 释放拖动
     */
    private void releaseDrag(){
        lastMotionY=0f;
        isBeingDragged = false;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }


    //---------------------------------------------------------------------------------------
    //控件回收逻辑
    //---------------------------------------------------------------------------------------

    @Override
    public void scrollBy(int x, int y) {
        super.scrollBy(x, y);
        //纵向滚动,并检测是否需要铺满
//        scrollVerticallyBy(y,recyclerBin);
    }

    class ViewFlipper implements Runnable{
        private int lastFlingY = 0;
        private final OverScroller scroller = new OverScroller(getContext());
        @Override
        public void run() {
            if(!scroller.isFinished()&&scroller.computeScrollOffset()){
                int y = scroller.getCurrY();
                int dy = y - lastFlingY;
                lastFlingY = y;
                //滚动视图
                scrollVerticallyBy(dy);
                if(!layout.canVerticalScroll()){
                    //中断滚动
                    scroller.abortAnimation();
                    invalidate();
                } else {
                    invalidate();
                    //持续发送事件
                    postOnAnimation(RecyclerTextLayout.this, this);
                }
            }
        }

        void startScroll(int startX,int  startY,int  dx,int  dy) {
            lastFlingY=0;
            scroller.startScroll(startX, startY, dx, dy);
            postOnAnimation();
        }

        /**
         * 终止滚动
         */
        void abortAnimation(){
            if(!scroller.isFinished()){
                scroller.abortAnimation();
                postInvalidate();
            }
        }

        /**
         * 纵向移动偏移量
         * @param dy
         */
        private void scrollVerticallyBy(float dy){
            if(null!=layout){
                layout.scrollBy(dy);
                //到达边缘不可滚动,吸收速率
                if(!layout.canVerticalScroll()){
                    startEdge.onAbsorb((int) scroller.getCurrVelocity());
                }
                invalidate();
            }
        }

        void fling(float velocityX, float velocityY) {
            lastFlingY=0;
            //停止执行动画
            abortAnimation();
            //启动
            scroller.fling(
                    0, 0, (int)velocityX, (int)velocityY,
                    Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE
            );
            postOnAnimation();
        }

        void postOnAnimation() {
            removeCallbacks(this);
            postOnAnimation(RecyclerTextLayout.this, this);
        }

        void postOnAnimation(View view, Runnable action) {
            if (Build.VERSION.SDK_INT >= 16) {
                view.postOnAnimation(action);
            } else {
                view.postDelayed(action, ValueAnimator.getFrameDelay());
            }
        }
    }

}
