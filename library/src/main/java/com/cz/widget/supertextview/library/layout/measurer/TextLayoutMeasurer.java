package com.cz.widget.supertextview.library.layout.measurer;

import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Spanned;
import android.text.TextPaint;

import com.cz.widget.supertextview.library.Styled;
import com.cz.widget.supertextview.library.decoration.LineDecoration;
import com.cz.widget.supertextview.library.layout.TextLayoutCallback;
import com.cz.widget.supertextview.library.style.ReplacementSpan;
import com.cz.widget.supertextview.library.text.TextElement;
import com.cz.widget.supertextview.library.utils.ArrayUtils;
import com.cz.widget.supertextview.library.utils.TextUtilsCompat;
import com.cz.widget.supertextview.library.view.TextParent;

/**
 * 为控件设计的文本排版对象
 */
public class TextLayoutMeasurer extends TextStyleLineMeasurer {
    /**
     * 测量文本回调对象
     */
    private TextLayoutCallback textLayoutCallback;

    public TextLayoutMeasurer(LineDecoration lineDecoration) {
        super(lineDecoration);
    }

    /**
     * 设置文本布局回调
     * @param textLayoutCallback
     */
    public void setTextLayoutCallback(TextLayoutCallback textLayoutCallback) {
        this.textLayoutCallback = textLayoutCallback;
    }

    @Override
    public TextElement[] measureTextElement(TextParent textParent, TextMeasurerInfo textMeasurerInfo, int paragraph, int line) {
        TextElement textElement = textLayoutCallback.onMeasureText(textParent,this, textMeasurerInfo, paragraph, line);
        return new TextElement[]{ textElement };
    }

    @Override
    public boolean onGenerateTextElement(TextElement textElement) {
        return textLayoutCallback.onGenerateTextElement(textElement);
    }
}
