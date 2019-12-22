## TextLayout

> About this library<br>
> This library was an android text process library but now expend from the text, by extending from the ViewGroup to make more change
A couple of months ago, I try to write a project that process text and view together. It took me one week, and I wrap it up. But I realized I just do a lot of work for the first time. Never mentioned I want to have some functions like line animation
It seems impossible for me. Then I stopped that project and start this project. Before I start, I did a ton of research about the android TextView in order to solve performance issues and try to have a powerful text style foundation. Now that is what we have.


#### [中文文档](document/readme-cn.md)

### Sample download
[apk file](https://github.com/momodae/SuperTextView/blob/master/apk/app-debug.apk?raw=true)
    
### Pictures

![Image1](https://github.com/momodae/SuperTextView/blob/master/image/image1.gif?raw=true)<br>
![Image2](https://github.com/momodae/SuperTextView/blob/master/image/image2.gif?raw=true)<br>
![Image3](https://github.com/momodae/SuperTextView/blob/master/image/image3.gif?raw=true)<br>

### Usage

1. For a static text
```
 <com.cz.widget.supertextview.library.view.TextLayout
    android:id="@+id/textLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```
  
2. For a recycler text, usually for large text, but little text as well

```
<com.cz.widget.supertextview.library.view.RecyclerTextLayout
    android:id="@+id/textLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_marginBottom="64dp"/>
```

3. Add text style just like use the Android TextView. But for a view we can't let you add view like the ViewGroup
It's a little different between ViewGroup, cause I don't know I you can add view by yourself How I manager all the elements in my Text layout.
That's a serious problems. So I was decider prohibited you add view by yourselves.

```
// Here you add style span like when you use textView
val backgroundColorSpan=BackgroundColorSpan(Color.RED)
spannableString.setSpan(backgroundColorSpan, index, Math.min(text.length,index+5), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

// For a view, you should do this. I was plan for a recycler text layout I should have a delayed load function like the ViewStub or ViewAdapter
// I put this on my agenda

val view=layoutInflater.inflate(R.layout.image_layout1, textLayout, false)
spannableString.setSpan(ViewSpan(view), index, Math.min(text.length,index+5), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
```

4. The line decoration. This function more like ItemDecoration in the RecyclerView. You can call setLineDecoration to decorate each lines.
Like add a margin for a specific line, have underline or highlight some information.

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

5. The TextRender. You know The textView wants to save more memory so all the information was in Layout. If you want to change how to render text information. You have to implement your own text layout. That's way too much work to do, I gave it up. That's why In this project I want to have a function that I could easily change the rules. 
The TextRender is works for that.

See [DefaultTextRender](library/src/main/java/com/cz/widget/supertextview/library/render/DefaultTextRender)<br>
See [SimpleTextAnimation](library/src/main/java/com/cz/widget/supertextview/library/animation/SimpleTextAnimation)<br>

*Warning:*
If you want to have your own TextRender. Make sure when the view call the method:drawText/drawReplacementSpan you or you will lost all the information 

```
public void drawReplacementSpan(Canvas canvas, TextPaint textPaint,TextPaint workPaint,ReplacementSpan replacementSpan, CharSequence text, int start, int end, float x, float y,int top,int bottom) {
    // draw ReplacementSpan
    replacementSpan.draw(canvas,text, start, end, x, y,textPaint);
}
public void drawText(Canvas canvas,TextPaint textPaint, CharSequence text, int start, int end,float x,float y,int top,int bottom) {
    //draw text background color in order to support the BackgroundColorSpan
    drawTextBackgroundColor(canvas,textPaint,text,start,end,x,y,top,bottom);
    //draw text
    canvas.drawText(text,start,end,x,y,textPaint);
}
```


### Problems
Sorry I do know a lot of problems in this project. I just do all of this by myself after work. What's more I use most of my time to learn English. I just don't have enough time to optimize this project.
Thanks for your understanding.

The problems that I knew was:

* RecyclerTextLayout When I calculate one paragraph I cached the paragraph object to a RecyclerPool. I didn't finish this function
That I have to give the chance release the cache.


* The View click event and touch event conflicts. Now I knew that when you set an click listener for child view, It does not work.
 

### TODO
* Support text change dynamically
* Separate text load strategy in order to load huge text, like a load fantasy book that has million word 
* Fixed all the problems above.
