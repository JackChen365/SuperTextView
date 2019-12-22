package com.cz.widget.supertextview.sample.template

import android.content.Context

/**
 * Created by cz on 2017/6/8.
 */
data class SampleItem<T>(var id:Int?, var pid:Int=0, var clazz:Class<out T>?, var title:String?, var desc:String?){
    var context:Context?=null

    constructor():this(null,0,null,null,null)
}