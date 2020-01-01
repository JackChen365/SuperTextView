package com.cz.widget.supertextview.library.layout.measurer;

import android.graphics.Rect;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;

import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.span.ViewSpan;
import com.cz.widget.supertextview.library.style.MetricAffectingSpan;
import com.cz.widget.supertextview.library.style.ReplacementSpan;
import com.cz.widget.supertextview.library.text.TextElement;
import com.cz.widget.supertextview.library.text.TextLayoutElement;
import com.cz.widget.supertextview.library.utils.ArrayUtils;
import com.cz.widget.supertextview.library.view.TextParent;

/**
 * 文本元素测量器
 */
public abstract class TextElementMeasurer {
    private static final int MAX_TEXT_COUNT = 20;
    /**
     * 行装饰器
     */
    LineDecoration lineDecoration;
    private int widthMeasuredMode= View.MeasureSpec.EXACTLY;
    private int heightMeasuredMode= View.MeasureSpec.EXACTLY;

    public TextElementMeasurer(LineDecoration lineDecoration) {
        this.lineDecoration = lineDecoration;
    }
    /**
     * 行空出
     */
    public void getLineOffsets(int paragraph, int line, Rect decorationRect){
        lineDecoration.getLineOffsets(paragraph,line,decorationRect);
    }

    /**
     * 行空出
     */
    public void getParagraphLineOffsets(TextLayoutElement textLayoutElement, int line, Rect decorationRect){
        lineDecoration.getParagraphLineOffsets(textLayoutElement,line,decorationRect);
    }
    /**
     * 测量元素信息
     * @param textParent 测量文本父容抽象对象
     * @param textMeasurerInfo
     * @param paragraph
     * @param line
     * @return
     */
    public abstract TextElement[] measureTextElement(TextParent textParent, TextMeasurerInfo textMeasurerInfo, int paragraph, int line);

    /**
     * 检测当前数据容器数据是否合理,如果超出,返回一个合适尺寸的数组
     * @param textElements
     * @param off
     * @return
     */
    public TextElement[] idealTextElementArray(TextElement[] textElements, int off) {
        int want = off + 1;
        if (null == textElements) {
            int size = ArrayUtils.idealIntArraySize(want + 1);
            textElements = new TextElement[size];
        } else if (want >= textElements.length) {
            int size = ArrayUtils.idealIntArraySize(want + 1);
            TextElement[] grow = new TextElement[size];
            System.arraycopy(textElements, 0, grow, 0, textElements.length);
            textElements = grow;
        }
        return textElements;
    }


    public void setMeasureSpecs(int wSpec, int hSpec) {
        widthMeasuredMode = View.MeasureSpec.getMode(wSpec);
        heightMeasuredMode = View.MeasureSpec.getMode(hSpec);
    }


    /**
     * 测量viewSpan
     * @param replacementSpan
     */
    public void measureViewSpan(TextParent textParent,ReplacementSpan replacementSpan) {
        if(null!=replacementSpan&&replacementSpan instanceof ViewSpan){
            ViewSpan viewSpan = (ViewSpan) replacementSpan;
            View view = viewSpan.getView();
            //动态测量控件
            measureChild(textParent,view,0,0);
        }
    }

    /**
     * 排版ViewSpan
     * @param replacementSpan
     * @param left
     * @param top
     */
    public void layoutViewSpan(ReplacementSpan replacementSpan, float left, float top) {
        //排版view
        if (null != replacementSpan && replacementSpan instanceof ViewSpan) {
            float layoutTop = 0;
            ViewSpan viewSpan = (ViewSpan) replacementSpan;
            viewSpan.setLayoutLeft((int) left);
            viewSpan.setLayoutTop((int) layoutTop);
        }
    }

    public void measureChild(TextParent textParent,View child, int widthUsed, int heightUsed) {
        final ViewGroup.LayoutParams lp = child.getLayoutParams();
        final int widthSpec = getChildMeasureSpec(textParent.getWidth(), widthMeasuredMode, widthUsed, lp.width, false);
        final int heightSpec = getChildMeasureSpec(textParent.getHeight(), heightMeasuredMode, heightUsed, lp.height, true);
        child.measure(widthSpec, heightSpec);
    }

    /**
     * 查找ReplacementSpan
     * @param start
     * @param end
     * @return
     */
    public ReplacementSpan findReplacementSpan(Spanned source, int start, int end){
        ReplacementSpan replacementSpan = null;
        ReplacementSpan[] replacementSpans = source.getSpans(start, end, ReplacementSpan.class);
        if(null != replacementSpans && 0 < replacementSpans.length){
            //检测换行规则,如果存在重叠的ReplaceSpan其实目前算法只取最后一个
            replacementSpan = replacementSpans[replacementSpans.length - 1];
        }
        return replacementSpan;
    }



    /**
     * Calculate a MeasureSpec value for measuring a child view in one dimension.
     *
     * @param parentSize Size of the parent view where the child will be placed
     * @param parentMode The measurement spec mode of the parent
     * @param padding Total space currently consumed by other elements of parent
     * @param childDimension Desired size of the child view, or MATCH_PARENT/WRAP_CONTENT.
     *                       Generally obtained from the child view's LayoutParams
     * @param canScroll true if the parent RecyclerView can scroll in this dimension
     *
     * @return a MeasureSpec value for the child view
     */
    public static int getChildMeasureSpec(int parentSize, int parentMode, int padding,
                                          int childDimension, boolean canScroll) {
        int size = Math.max(0, parentSize - padding);
        int resultSize = 0;
        int resultMode = 0;
        if (canScroll) {
            if (childDimension >= 0) {
                resultSize = childDimension;
                resultMode = View.MeasureSpec.EXACTLY;
            } else if (childDimension == ViewGroup.LayoutParams.MATCH_PARENT) {
                switch (parentMode) {
                    case View.MeasureSpec.AT_MOST:
                    case View.MeasureSpec.EXACTLY:
                        resultSize = size;
                        resultMode = parentMode;
                        break;
                    case View.MeasureSpec.UNSPECIFIED:
                        resultSize = 0;
                        resultMode = View.MeasureSpec.UNSPECIFIED;
                        break;
                }
            } else if (childDimension == ViewGroup.LayoutParams.WRAP_CONTENT) {
                resultSize = 0;
                resultMode = View.MeasureSpec.UNSPECIFIED;
            }
        } else {
            if (childDimension >= 0) {
                resultSize = childDimension;
                resultMode = View.MeasureSpec.EXACTLY;
            } else if (childDimension == ViewGroup.LayoutParams.MATCH_PARENT) {
                resultSize = size;
                resultMode = parentMode;
            } else if (childDimension == ViewGroup.LayoutParams.WRAP_CONTENT) {
                resultSize = size;
                if (parentMode == View.MeasureSpec.AT_MOST || parentMode == View.MeasureSpec.EXACTLY) {
                    resultMode = View.MeasureSpec.AT_MOST;
                } else {
                    resultMode = View.MeasureSpec.UNSPECIFIED;
                }

            }
        }
        //noinspection WrongConstant
        return View.MeasureSpec.makeMeasureSpec(resultSize, resultMode);
    }

    /**
     * 获得下一个流转的位置
     * @param spanned
     * @param i
     * @param end
     * @return
     */
    public int getNextTransition(Spanned spanned, int i,int end) {
        int next=0;
        if (spanned == null) {
            int textCount=end-i;
            if (textCount < MAX_TEXT_COUNT) {
                next += MAX_TEXT_COUNT;
            } else {
                next = end;
            }
        } else {
            int nextSpanTransition = spanned.nextSpanTransition(i, end, MetricAffectingSpan.class);
            //todo  try to save this step, cause it hard to know next span transition was start or end;
            MetricAffectingSpan[] spans = spanned.getSpans(i, nextSpanTransition, MetricAffectingSpan.class);
            if(0 < spans.length){
                next=nextSpanTransition;
            } else {
                int textCount=nextSpanTransition-i;
                if (textCount < MAX_TEXT_COUNT) {
                    next += MAX_TEXT_COUNT;
                } else {
                    next = end;
                }
            }
        }
        return next;
    }

}
