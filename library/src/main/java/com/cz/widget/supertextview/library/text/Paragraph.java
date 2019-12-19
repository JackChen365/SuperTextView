package com.cz.widget.supertextview.library.text;

/**
 * 段落信息
 */
public class Paragraph{
    /**
     * 当前段落
     */
    public final int index;
    /**
     * 当前段落行总数
     */
    public int lineCount=0;
    /**
     * 段落内行信息
     */
    public TextLine[] textLines;

    public TextParagraph textParagraph;

    public Paragraph(int index) {
        this.index = index;
    }

    /**
     * 返回指定行起始字符
     * @param line
     * @return
     */
    public int getLineLatterStart(int line) {
        return textLines[line].getLineStart();
    }

    /**
     * 返回行结束字符
     * @param line
     * @return
     */
    public final int getLineLatterEnd(int line) {
        return textLines[line].getLineEnd();
    }

    /**
     * 获得行所占宽
     * @param line
     * @return
     */
    public int getLineHeight(int line){
        return textLines[line].getLineHeight();
    }
}
