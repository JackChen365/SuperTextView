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