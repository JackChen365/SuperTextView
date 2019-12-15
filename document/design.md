## 设计

* 在Span之外,重新设计了元素自身定位属性

```
/**
 * 跟随内容向右流动
 */
public static final int FLOW=FLOW_FLAG;
/**
 * 标记段落,后续内容按此段落信息流动
 */
public static final int PARAGRAPH =PARAGRAPH_FLAG | CONSIDER_BREAK_LINE_FLAG;
/**
 * 标记段落,并自动断行
 */
public static final int PARAGRAPH_BREAK_LINE=PARAGRAPH_FLAG | BREAK_LINE_FLAG;
/**
 * 当前控件自动换行
 */
public static final int BREAK_LINE=BREAK_LINE_FLAG;
/**
 * 标志span按flow摆放,但如果超出尺寸 ,则放到下一行
 */
public static final int CONSIDER_BREAK_LINE=FLOW_FLAG | CONSIDER_BREAK_LINE_FLAG;
/**
 * 跟随内容,排版后换行
 */
public static final int FLOW_SINGLE_LINE=FLOW_FLAG | BREAK_LINE_FLAG | SINGLE_LINE_FLAG;
/**
 * 独占一行
 */
public static final int SINGLE_LINE=BREAK_LINE_FLAG | SINGLE_LINE_FLAG;
```
* 在当前的属性集中,添加了三个额外参数,用于确定元素的绘制起始位置，结束位置（因为存在同行，多条信息）

```
private static final int START = 0;
private static final int TOP = 1;
private static final int DESCENT = 2;
private static final int LEFT = 3;//文本起始绘制位置
private static final int BOTTOM=4;//文本底部位置,因为存在一行内,多行信息
private static final int ALIGN = 5;//文本对齐标志
```


* 元素排版影响

1. 会影响尺寸变化的是 MetricAffectingSpan
2. 根据单词元素做切词操作的动态排版影响
3. 根据强行设置的标志,检测的换行操作影响
4. 段落的影响,以及段落内,再附加一个段落,可能的情况(暂不考虑)
5. 元素行的靠顶部,居中,以及底部对齐
6. 强行断行,对下一行的绘制影响




 