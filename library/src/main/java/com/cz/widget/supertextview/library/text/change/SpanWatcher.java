package com.cz.widget.supertextview.library.text.change;

import android.text.NoCopySpan;
import android.text.Spannable;

/**
 * When an object of this type is attached to a Spannable, its methods
 * will be called to notify it that other markup objects have been
 * added, changed, or removed.
 */
public interface SpanWatcher extends NoCopySpan {
    /**
     * This method is called to notify you that the specified object
     * has been attached to the specified range of the text.
     */
    public void onSpanAdded(Spannable text, Object what, int start, int end);
    /**
     * This method is called to notify you that the specified object
     * has been detached from the specified range of the text.
     */
    public void onSpanRemoved(Spannable text, Object what, int start, int end); 
    /**
     * This method is called to notify you that the specified object
     * has been relocated from the range <code>ostart&hellip;oend</code>
     * to the new range <code>nstart&hellip;nend</code> of the text.
     */
    public void onSpanChanged(Spannable text, Object what, int ostart, int oend,
                              int nstart, int nend);
}
