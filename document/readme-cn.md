## TextLayout

> 关于这个项目<br>
8月份时,做了一个文本,与控件的复合处理控件,花了一周左右时间,是公司项目需要.但是在处理大量文本以及控件后,性能很差. 在我改用textView处理文本后,意外发现,差别竟然有10多倍.让我开始思考从TextView开始,扩展一个库,从ViewGroup继承.完成文本样式,以及控件的原生支持. 这就是写这个库的原因.本库大概开发了二个多月,对TextView做了大量的研究.都是利用业余时间开发.所以进度很慢.当前只是初步版本.仍然有很多bug.但有时间会持续改进.


### Sample 下载
[apk file](https://github.com/momodae/SuperTextView/blob/master/apk/app-debug.apk?raw=true)
    
### Pictures

![Image1](https://github.com/momodae/SuperTextView/blob/master/image/image1.gif?raw=true)<br>
![Image2](https://github.com/momodae/SuperTextView/blob/master/image/image2.gif?raw=true)<br>
![Image3](https://github.com/momodae/SuperTextView/blob/master/image/image3.gif?raw=true)<br>

### 如何使用

1. 静态文本
```
 <com.cz.widget.supertextview.library.view.TextLayout
    android:id="@+id/textLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```
  
2. 自带滚动视图的复用文本,对于少量静态文本也可以,但是设计上是为了复用,并展示大量文本信息

```
<com.cz.widget.supertextview.library.view.RecyclerTextLayout
    android:id="@+id/textLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_marginBottom="64dp"/>
```

3. 添加文本样式,或者控件,类似于使用系统TextView,但控件不允许直接添加,回为可能会导致排版,与管理上的问题

```
// Here you add style span like when you use textView
val backgroundColorSpan=BackgroundColorSpan(Color.RED)
spannableString.setSpan(backgroundColorSpan, index, Math.min(text.length,index+5), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

// For a view, you should do this. I was plan for a recycler text layout I should have a delayed load function like the ViewStub or ViewAdapter
// I put this on my agenda

val view=layoutInflater.inflate(R.layout.image_layout1, textLayout, false)
spannableString.setSpan(ViewSpan(view), index, Math.min(text.length,index+5), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
```

4. 行装饰器,用于扩展每一行信息渲染时的额外附加信息,类似于RecyclerView的ItemDecoration.

```
// set up a lineDecoration for text layout
textLayout.setLineDecoration(HighlightLineDecoration())

Here is an easy sample
public class HighlightLineDecoration extends LineDecoration {
   @Override
    public void getLineOffsets(int paragraph, int line, Rect outRect) {
        //set line offsets to let the layout instance know how much space you want to keep
        outRect.set(linePadding,linePadding,linePadding,linePadding);
    }

    @Override
    public void getParagraphLineOffsets(TextParagraph textParagraph, int line, Rect outRect) {
    //Here set offset for paragraph. If your line in the text pagraph. Since we support paragraph we have TextParagraph, It's more like View and ViewGroup 
        outRect.set(0,0,0,0);
//        outRect.set(lineParagraphPadding,lineParagraphPadding,lineParagraphPadding,lineParagraphPadding);
    }

    @Override
    public void onLineDraw(Canvas canvas, TextLine textLine,int width) {
        //When we draw text line, Here we call this method.
        int decoratedScrollLineBottom = textLine.getDecoratedScrollLineBottom();
        canvas.drawLine(0f,decoratedScrollLineBottom*1f, width,decoratedScrollLineBottom*1f, linePaint);
    }

    @Override
    public void onParagraphLineDraw(Canvas canvas, TextLine textLine,int width) {
    //When we draw text paragraph line, Here we call this method.
        if(textLine.isBreakLine()){
            //Here we could be able to draw a divider for each line
            int lineLeft = textLine.getDecoratedLeft();
            int decoratedScrollLineBottom = textLine.getDecoratedScrollLineBottom();
            canvas.drawLine(lineLeft,decoratedScrollLineBottom*1f, width,decoratedScrollLineBottom*1f, lineParagraphPaint);
        }
        //Here we could be able to draw a text underline
//        Rect decoratedRect = textLine.getDecoratedRect();
//        int scrollOffset = textLine.getScrollOffset();
//        int lineBottom = textLine.getLineBottom();
//        canvas.drawLine(decoratedRect.left,scrollOffset+lineBottom*1f, width-decoratedRect.right,scrollOffset+lineBottom*1f,linePaint);
    }
}
``` 

5. 文本渲染器,为分离所有的上层渲染逻辑设计. 主要为了接管所有元素渲染.动画也是通过此处扩展而来.
注意,如果复写了文本渲染器,就代表接管了所有元素渲染,这时候如果不在drawText/drawReplacementSpan内手动将元素绘制.
界面将没有任何绘制元素

See [DefaultTextRender](library/src/main/java/com/cz/widget/supertextview/library/render/DefaultTextRender)
See [SimpleTextAnimation](library/src/main/java/com/cz/widget/supertextview/library/animation/SimpleTextAnimation)


### Problems
本项目为一个业余项目,时间有限. 花费了好几个周末,对TextView对了大量的研究,但仍然有很多问题.希望理解.

当前我知道的问题:
*  控件尺寸高发生变化,因为会导致高度设置问题

```
//Both TextLayout/RecyclerTextLayout have this problems
@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    //测量子孩子尺寸
    measureChildren(widthMeasureSpec,heightMeasureSpec);
    int measuredWidth = getMeasuredWidth();
    int measuredHeight = getMeasuredHeight();
    final int outerWidth=measuredWidth - getPaddingLeft() - getPaddingRight();
    int outerHeight=measuredHeight-getPaddingTop()-getPaddingBottom();
    //Initcial
    if(null!=text){
        //Initialize text layout
        if(null==layout||text!=layout.getText()){
            layout = new StaticLayout(text, textPaint, lineDecoration,textRender, outerWidth, 0f, gravity);
        } else if(outerHeight!=layout.getLayoutHeight()){
            //todo When view height changed, We should reset layout height. But there have an issue
            layout.setLayoutHeight(outerHeight);
        }
    }
//        //重新设置尺寸
    if(null!=layout&&measuredHeight!=(layout.getHeight()+getPaddingTop()+getPaddingBottom())){
        final int layoutHeight=layout.getHeight();
        setMeasuredDimension(measuredWidth,getPaddingTop()+layoutHeight+getPaddingBottom());
    }
}
```

* 段落结果运算完之后,在复用池内,一直缓存着,回收逻辑没有完善

* 手势这块存在问题.
 

### TODO
* 支持文本手动编辑
* 分离文本装载逻辑,使其支持海量文本加载. 
* 修复以上bug.
