package com.cz.widget.supertextview.library.view;

import android.view.View;
import android.view.ViewGroup;

public interface TextParent {
    int getWidth();
    int getHeight();
    int getPaddingLeft();
    int getPaddingTop();
    int getPaddingRight();
    int getPaddingBottom();
    void invalidate();
    void attachViewToParent(View child, int index, ViewGroup.LayoutParams params);
    void detachAllViewsFromParent();
    void detachViewFromParent(View child);
}
