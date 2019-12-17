package com.cz.widget.supertextview.sample

import android.app.Application
import com.cz.widget.supertextview.sample.animation.AnimationText1Activity
import com.cz.widget.supertextview.sample.animation.AnimationText2Activity
import com.cz.widget.supertextview.sample.extension.SelectTextActivity
import com.cz.widget.supertextview.sample.version1.TextLayoutSample1Activity
import com.cz.widget.supertextview.sample.version1.TextLayoutSample2Activity
import com.cz.widget.supertextview.sample.version1.TextLayoutSample3Activity
import com.okay.sampletamplate.configurtion.TemplateConfiguration


/**
 * @author Created by cz
 * @date 2019-05-14 10:07
 * @email bingo110@126.com
 *
 */
class SampleApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        TemplateConfiguration.init(this){
            item {
                title = "演示TextLayout基本功能1"
                desc = "演示TextLayout文本排版,以及控件支持等功能"
                clazz= TextLayoutSample1Activity::class.java
            }
            item {
                title = "演示TextLayout基本功能2"
                desc = "演示TextLayout动态添加各种元素"
                clazz= TextLayoutSample2Activity::class.java
            }
            item {
                title = "演示TextLayout"
                desc = "演示TextLayout动态优化运处及渲染"
                clazz= TextLayoutSample3Activity::class.java
            }
            category {
                title="动画扩展"
                desc = "演示一些动画扩展功能"
                item{
                    title="基本行动画"
                    desc = "演示为每一行文本添加动画"
                    clazz= AnimationText1Activity::class.java
                }
                item{
                    title="文本动画扩展"
                    desc = "演示为每一个文本添加动画"
                    clazz= AnimationText2Activity::class.java
                }
            }
            category {
                title="功能扩展"
                desc = "演示一些其他扩展功能"
                item{
                    title="选中文本"
                    desc = "演示点击选中单词"
                    clazz=SelectTextActivity::class.java
                }
            }
        }
    }
}