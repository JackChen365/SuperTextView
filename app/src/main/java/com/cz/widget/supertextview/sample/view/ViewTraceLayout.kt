package com.cz.widget.supertextview.sample.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.cz.widget.supertextview.sample.message.MessageManager
import kotlin.system.measureTimeMillis

/**
 * @author Created by cz
 * @date 2019-12-10 16:36
 * @email bingo110@126.com
 * 控件性能状态监控
 */
internal class ViewTraceLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr){
    companion object{
        /**
         * 事件数组
         */
        private val ACTION_ARRAY= arrayOf("DOWN","UP","MOVE","CANCEL","OUTSIDE",
            "POINTER_DOWN","POINTER_UP","HOVER_MOVE","SCROLL","HOVER_ENTER","HOVER_EXIT","BUTTON_PRESS", "BUTTON_RELEASE")
    }

    /**
     * 过滤标志
     */
    private var messageFlag:Int=ViewTrace.ALL

    /**
     * 设置展示消息标志
     */
    fun setMessageFlag(flag:Int){
        this.messageFlag=flag
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val time = measureTimeMillis {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
        if(0 < childCount){
            printlnMessage(ViewTrace.MEASURE,"onMeasure:$time")
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val time = measureTimeMillis {
            super.onLayout(changed, left, top, right, bottom)
        }
        if(0 < childCount){
            printlnMessage(ViewTrace.LAYOUT,"onLayout:$time")
        }
    }

    override fun drawChild(canvas: Canvas?, child: View?, drawingTime: Long): Boolean {
        var drawChild = false
        val time = measureTimeMillis {
            drawChild=super.drawChild(canvas, child, drawingTime)
        }
        if(0 < childCount){
            printlnMessage(ViewTrace.DRAW,"draw:$time")
        }
        return drawChild
    }

    override fun onDraw(canvas: Canvas?) {

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val onTouchEvent = super.onTouchEvent(event)
        val action=event.actionMasked
        if(action in ACTION_ARRAY.indices){
            printlnMessage(ViewTrace.ON_TOUCH,"onTouchEvent:${ACTION_ARRAY[action]} $onTouchEvent")
        }
        return onTouchEvent
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val dispatchTouchEvent = super.dispatchTouchEvent(ev)
        val action=ev.actionMasked
        if(action in ACTION_ARRAY.indices){
            printlnMessage(ViewTrace.ON_TOUCH,"dispatchTouchEvent:${ACTION_ARRAY[action]} $dispatchTouchEvent")
        }
        return dispatchTouchEvent
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val onInterceptTouchEvent = super.onInterceptTouchEvent(ev)
        val action=ev.actionMasked
        if(action in ACTION_ARRAY.indices){
            printlnMessage(ViewTrace.ON_TOUCH,"onInterceptTouchEvent:${ACTION_ARRAY[action]} $onInterceptTouchEvent")
        }
        return onInterceptTouchEvent
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if(0 < childCount){
            printlnMessage(ViewTrace.ALL,"onAttachedToWindow")
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if(0 < childCount){
            printlnMessage(ViewTrace.ALL,"onDetachedFromWindow")
        }
    }

    /**
     * 输出消息
     */
    private fun printlnMessage(flag:Int,message:String){
        //过滤信息
        if(0!=(flag and messageFlag)){
            MessageManager.post(message)
        }
    }

}