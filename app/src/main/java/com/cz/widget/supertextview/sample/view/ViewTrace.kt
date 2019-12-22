package com.cz.widget.supertextview.sample.view

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.cz.widget.supertextview.sample.R

/**
 * @author Created by cz
 * @date 2019-12-11 16:41
 * @email bingo110@126.com
 *
 */
object ViewTrace {
    // 常规事件----------------------
    const val MEASURE=0x01
    const val LAYOUT=0x02
    const val DRAW=0x04
    const val ON_TOUCH=0x08
    //输出所有
    const val ALL:Int=0xFFFFFFF

    fun trace(view: View){
        val parentView=view.parent
        if(null!=parentView&&parentView is ViewGroup){
            replaceView(view,parentView)
        }
    }

    /**
     * 设置显示事件事件
     */
    fun setMessageFlag(activity: Activity, flag:Int){
        activity.findViewById<ViewTraceLayout>(R.id.sampleTraceLayout)?.setMessageFlag(flag)
    }

    /**
     * 替换控件并添加一个父级
     */
    private fun replaceView(view: View, parent: ViewGroup) {
        val index = parent.indexOfChild(view)
        parent.removeViewInLayout(view)

        //包装控件
        val traceLayout=ViewTraceLayout(view.context)
        traceLayout.id=R.id.sampleTraceLayout
        traceLayout.addView(view,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        //添加控件到视图
        val layoutParams = view.layoutParams
        if (layoutParams != null) {
            parent.addView(traceLayout, index, layoutParams)
        } else {
            parent.addView(traceLayout, index)
        }
    }
}