package com.cz.widget.supertextview.sample

import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    /**
     * 跟随内容
     */
    private val FLOW_FLAG = 0x01
    /**
     * 当前元素独占一行
     */
    private val PARAGRAPH_FLAG = 0x02
    /**
     * 断行标志
     */
    private val BREAK_LINE_FLAG = 0x04
    /**
     * 检测是否需要断行,意思为超出内容则断,不超出不断
     */
    private val CONSIDER_BREAK_LINE_FLAG = 0x08
    /**
     * 当前元素独占一行
     */
    private val SINGLE_LINE_FLAG = 0x10

    /**
     * 跟随内容向右流动
     */
    val FLOW = FLOW_FLAG
    /**
     * 标记段落,后续内容按此段落信息流动
     */
    val PARAGRAPH = PARAGRAPH_FLAG or CONSIDER_BREAK_LINE_FLAG
    /**
     * 标记段落,并自动断行
     */
    val PARAGRAPH_BREAK_LINE = PARAGRAPH_FLAG or BREAK_LINE_FLAG
    /**
     * 当前控件自动换行
     */
    val BREAK_LINE = BREAK_LINE_FLAG
    /**
     * 标志span按flow摆放,但如果超出尺寸 ,则放到下一行
     */
    val CONSIDER_BREAK_LINE = CONSIDER_BREAK_LINE_FLAG
    /**
     * 跟随内容,出现后占满随后空间
     */
    val FLOW_SINGLE_LINE = FLOW_FLAG or BREAK_LINE_FLAG or SINGLE_LINE_FLAG
    /**
     * 独占一行
     */
    val SINGLE_LINE = BREAK_LINE_FLAG or SINGLE_LINE_FLAG
    @Test
    fun layoutModeIsCorrect() {
        println(Integer.toHexString(FLOW))
        println(Integer.toHexString(PARAGRAPH))
        println(Integer.toHexString(PARAGRAPH_BREAK_LINE))
        println(Integer.toHexString(BREAK_LINE))
        println(Integer.toHexString(CONSIDER_BREAK_LINE))
        println(Integer.toHexString(FLOW_SINGLE_LINE))
        println(Integer.toHexString(SINGLE_LINE))
    }

    @Test
    fun arrayCopyTest(){
        val array=(0 until 30).toList().toIntArray()
        System.arraycopy(array,0,array,5,25)
        println(Arrays.toString(array))

    }
}
