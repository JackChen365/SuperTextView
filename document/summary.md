性能测试记录

> 1000字,单字运算与数据取块运算性能差
```
val text = assets.open("chapter1").bufferedReader().readText()
val measureText=text.subSequence(0,1000)
val paint=Paint(Paint.ANTI_ALIAS_FLAG)
paint.textSize=24f
val times=10
val timeCount=1000/10
val widthArray= FloatArray(timeCount)
val time1=measureTimeMillis {
    for(i in 0 until times){
        paint.getTextWidths(measureText,i*timeCount,(i+1)*timeCount,widthArray)
    }
}
val time2=measureTimeMillis {
    for(i in 0 until 1000){
        paint.measureText(measureText,i,i+1)
    }
}
println("time1:$time1 time2:$time2")

2019-11-24 16:31:15.157 18440-18440/com.cz.widget.supertextview.sample I/System.out: time1:2 time2:9
```


性能测试
文本-Little Prince小王子.txt: 
字数:246521

```
2019-12-01 20:44:58.478 15992-15992/com.cz.widget.supertextview.sample I/RecyclerTextLayout: onMeasure:4
2019-12-01 20:44:58.479 15992-15992/com.cz.widget.supertextview.sample I/RecyclerTextLayout: onMeasure:0
2019-12-01 20:44:58.485 15992-15992/com.cz.widget.supertextview.sample I/RecyclerTextLayout: onMeasure:0
2019-12-01 20:44:58.486 15992-15992/com.cz.widget.supertextview.sample I/RecyclerTextLayout: onMeasure:1
2019-12-01 20:44:58.508 15992-15992/com.cz.widget.supertextview.sample I/RecyclerTextLayout: time:2 lineCount:21
2019-12-01 20:44:58.523 15992-15992/com.cz.widget.supertextview.sample I/RecyclerTextLayout: time:1 lineCount:21
2019-12-01 20:45:13.595 15992-15992/com.cz.widget.supertextview.sample I/RecyclerTextLayout: time:1 lineCount:24
2019-12-01 20:45:13.611 15992-15992/com.cz.widget.supertextview.sample I/RecyclerTextLayout: time:0 lineCount:24
2019-12-01 20:45:13.629 15992-15992/com.cz.widget.supertextview.sample I/RecyclerTextLayout: time:1 lineCount:24
2019-12-01 20:45:13.645 15992-15992/com.cz.widget.supertextview.sample I/RecyclerTextLayout: time:1 lineCount:24
2019-12-01 20:45:13.661 15992-15992/com.cz.widget.supertextview.sample I/RecyclerTextLayout: time:0 lineCount:24
2019-12-01 20:45:13.678 15992-15992/com.cz.widget.supertextview.sample I/RecyclerTextLayout: time:1 lineCount:24
```


### 文本分段计算
正常TextView是以换行分段运算,这是一级缓冲去处.但是有些极端情况,如单个段落超长.这时候再按此方式运算,就非常吃力
而且不利于后续复杂文本的运态运算.即滚到哪一处,加载到哪一处.而不必提前运算好.

采取的策略是,分隔文本运算逻辑
单次运算,最大运算数值200,或者多少.使用一个段落加载信息类.来运算管理每一个段落已经获取的运算信息

