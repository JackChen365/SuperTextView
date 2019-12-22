package com.cz.widget.supertextview.sample.template

import android.app.Activity
import android.content.Context
import com.cz.widget.supertextview.sample.R
import com.cz.widget.supertextview.sample.animation.AnimationText1Activity
import com.cz.widget.supertextview.sample.extension.SelectTextActivity
import com.cz.widget.supertextview.sample.extension.SelectTextWordActivity
import com.cz.widget.supertextview.sample.layout.TextLayoutSample1Activity
import com.cz.widget.supertextview.sample.layout.TextLayoutSample2Activity
import com.cz.widget.supertextview.sample.layout.TextLayoutSample3Activity
import com.cz.widget.supertextview.sample.layout.TextLayoutSample4Activity

/**
 * Created by cz on 2017/6/8.
 */
class FuncTemplate {
    private val context:Context
    private val items = mutableListOf<SampleItem<Activity>>()
    private val groupItems = mutableMapOf<Int, List<SampleItem<Activity>>>()

    companion object{
        private var funcTemplate:FuncTemplate?=null

        @Synchronized
        fun getInstance(context: Context):FuncTemplate{
            var funcTemplate=funcTemplate
            if(null==funcTemplate){
                funcTemplate=FuncTemplate(context)
            }
            return funcTemplate
        }
    }

    private constructor(context: Context){
        this.context=context.applicationContext
        item {
            id = 1
            title = context?.getString(R.string.sample1)
            desc = context?.getString(R.string.sample_desc1)
            clazz = TextLayoutSample1Activity::class.java
        }
        item {
            id = 2
            title = context?.getString(R.string.sample2)
            desc = context?.getString(R.string.sample_desc2)
            clazz = TextLayoutSample2Activity::class.java
        }
        item {
            id = 3
            title = context?.getString(R.string.sample3)
            desc = context?.getString(R.string.sample_desc3)
            clazz = TextLayoutSample3Activity::class.java
        }
        item {
            id = 4
            title = context?.getString(R.string.sample4)
            desc = context?.getString(R.string.sample_desc4)
            clazz = AnimationText1Activity::class.java
        }
        item {
            id = 5
            title = context?.getString(R.string.sample5)
            desc = context?.getString(R.string.sample_desc5)
            item {
                pid=5
                title = context?.getString(R.string.sample5_1)
                desc = context?.getString(R.string.sample_desc5_1)
                clazz = SelectTextWordActivity::class.java
            }
            item {
                pid=5
                title = context?.getString(R.string.sample5_2)
                desc = context?.getString(R.string.sample_desc5_2)
                clazz = SelectTextActivity::class.java
            }
        }
        item {
            id = 6
            title = context?.getString(R.string.sample6)
            desc = context?.getString(R.string.sample_desc6)
            clazz = TextLayoutSample4Activity::class.java
        }
        //分组
        groupItems += items.groupBy { it.pid }
    }

    fun item(closure: SampleItem<Activity>.() -> Unit) {
        val sampleItem = SampleItem<Activity>()
        sampleItem.context=context
        items.add(SampleItem<Activity>().apply(closure))
    }

    operator fun get(id: Int?) = groupItems[id]

    operator fun contains(id: Int?) = groupItems.any { it.key == id }

}