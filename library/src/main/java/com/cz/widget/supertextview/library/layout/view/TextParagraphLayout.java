package com.cz.widget.supertextview.library.layout.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;

import com.cz.widget.supertextview.library.R;
import com.cz.widget.supertextview.library.layout.TextLayoutCallback;
import com.cz.widget.supertextview.library.layout.measurer.TextLayoutMeasurer;
import com.cz.widget.supertextview.library.layout.measurer.TextMeasurerInfo;
import com.cz.widget.supertextview.library.text.TextElement;
import com.cz.widget.supertextview.library.text.TextLayoutElement;
import com.cz.widget.supertextview.library.view.TextParent;

/**
 * 文本排版断段控件
 */
public class TextParagraphLayout extends ViewGroup implements TextLayoutCallback {
    public static final int LEFT=1;
    public static final int CENTER=2;
    public static final int RIGHT=3;
    /**
     * 控件排版方位
     */
    private int gravity=LEFT;
    public TextParagraphLayout(Context context) {
        this(context,null,0);
    }

    public TextParagraphLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TextParagraphLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextParagraphLayout);
        gravity = a.getInt(R.styleable.TextParagraphLayout_paragraphGravity, LEFT);
        a.recycle();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TextParagraphLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        int childCount = getChildCount();
        if(1 < childCount){
            throw new IllegalStateException("TextParagraphLayout can host only one direct child");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec,heightMeasureSpec);
    }

    public int getDecorateLeft(View childView){
        LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
        return childView.getLeft()+layoutParams.leftMargin;
    }

    public int getDecorateTop(View childView){
        LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
        return childView.getTop()+layoutParams.topMargin;
    }

    public int getDecorateRight(View childView){
        LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
        return childView.getRight()+layoutParams.rightMargin;
    }

    public int getDecorateBottom(View childView){
        LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
        return childView.getBottom()+layoutParams.bottomMargin;
    }

    public int getViewGravity(View childView){
        int childGravity=gravity;
        ViewGroup.LayoutParams layoutParams = childView.getLayoutParams();
        if(layoutParams instanceof LayoutParams){
            int layoutGravity=((LayoutParams) layoutParams).gravity;
            if(Gravity.NO_GRAVITY!=layoutGravity){
                childGravity=layoutGravity;
            }
        }
        return childGravity;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        if(0 < childCount){
            View childView = getChildAt(0);
            int width = getWidth();
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();
            int paddingRight = getPaddingRight();
            int gravity = getViewGravity(childView);
            LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
            //layout child view
            switch (gravity){
                case LEFT:
                    childView.layout(paddingLeft+layoutParams.leftMargin,paddingTop,paddingLeft+layoutParams.leftMargin+childView.getMeasuredWidth(),paddingTop+childView.getMeasuredHeight());
                    break;
                case CENTER:
                    int layoutLeft=(width-childView.getMeasuredWidth())/2+layoutParams.leftMargin-layoutParams.rightMargin;
                    childView.layout(layoutLeft,paddingTop,layoutLeft+childView.getMeasuredWidth(),paddingTop+childView.getMeasuredHeight());
                    break;
                case RIGHT:
                    int layoutRight = width-paddingRight-layoutParams.rightMargin;
                    childView.layout(layoutRight-childView.getMeasuredWidth(),paddingTop,layoutRight,paddingTop+childView.getMeasuredHeight());
                    break;
            }
        }
    }

    @Override
    public TextElement onMeasureText(TextParent textParent, TextLayoutMeasurer textLayoutMeasurer, TextMeasurerInfo textMeasurerInfo, int paragraph, int line) {
        int childCount = getChildCount();
        TextLayoutElement textLayoutElement;
        if(0 == childCount){
            throw new NullPointerException("TextParagraphLayout should have at least one child!");
        } else {
            View childView = getChildAt(0);
            int gravity = getViewGravity(childView);
            int width = getWidth();
            int left = getPaddingLeft();
            int right = width-getPaddingRight();
            int decorateLeft = getDecorateLeft(childView);
            int decorateRight = getDecorateRight(childView);
            //layout child view
            textLayoutElement=new TextLayoutElement();
            switch (gravity){
                case LEFT:
                    //text layout rectangle area was decorate right to layout right
                    // from decorateLeft to right
                    textLayoutElement.textElements=textLayoutMeasurer.measureTextElement(textParent, textMeasurerInfo, paragraph, line, decorateLeft, right,true);
                    textLayoutElement.lineCount=textLayoutElement.textElements.length;
                    break;
                case CENTER:
                    //if view in center of the layout. Therefore, It will split paragraph to two different area
                    // from left to decorateLeft, from decorateRight to right
                    TextElement[] leftTextElements = textLayoutMeasurer.measureTextElement(textParent, textMeasurerInfo, paragraph, line, left, decorateLeft,true);
                    TextElement[] rightTextElements = textLayoutMeasurer.measureTextElement(textParent, textMeasurerInfo, paragraph, line, decorateRight, right,true);
                    int textLayoutCount = leftTextElements.length + rightTextElements.length;
                    textLayoutElement.textElements=new TextElement[textLayoutCount];
                    System.arraycopy(leftTextElements,0,textLayoutElement.textElements,0,leftTextElements.length);
                    System.arraycopy(rightTextElements,0,textLayoutElement.textElements,leftTextElements.length,rightTextElements.length);
                    break;
                case RIGHT:
                    //if view in right of the layout. layout view from left to decorateLeft
                    textLayoutElement.textElements=textLayoutMeasurer.measureTextElement(textParent,textMeasurerInfo,paragraph,line,left,decorateLeft,true);
                    textLayoutElement.lineCount=textLayoutElement.textElements.length;
                    break;
            }
        }
        return textLayoutElement;
    }

    @Override
    public boolean onGenerateTextElement(TextElement textElement) {
        //when the text element near the edge and at the bottom of the view. Function return true to let outside know we are done
        boolean done=true;
        int childCount = getChildCount();
        if(0 == childCount){
            throw new NullPointerException("TextParagraphLayout should have at least one child!");
        } else {
            View childView = getChildAt(0);
            int gravity = getViewGravity(childView);
            //If the view in center of the layout. That's mean we will have two different area of text.
            //So I have to make sure that this text element is in right side of the layout
            if(CENTER==gravity){
                int decorateRight = getDecorateRight(childView);
                done=decorateRight<=textElement.getLineLeft();
            }
            int decoratedLineBottom = textElement.getDecoratedLineBottom();
            int measuredHeight = getMeasuredHeight();
            done &= decoratedLineBottom>=measuredHeight;
        }
        return done;
    }


    /**
     * 自定义LayoutParams对象,对于设置子控件的排版规则
     * @attr R.attr.layoutParagraphGravity
     */
    public class LayoutParams extends ViewGroup.MarginLayoutParams {
        public int gravity = Gravity.NO_GRAVITY;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray typedArray = c.obtainStyledAttributes(attrs, R.styleable.TextParagraphLayout);
            gravity =typedArray.getInt(R.styleable.TextParagraphLayout_layoutParagraphGravity, Gravity.NO_GRAVITY);
            typedArray.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

}
