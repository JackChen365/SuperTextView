package com.cz.widget.supertextview.sample.animation.render.drawable;

import android.graphics.*;
import android.graphics.drawable.Drawable;

/**
 * @author :Created by cz
 * @date 2019-05-16 14:09
 * @email bingo110@126.com
 * 一个圆形drawable对象
 */
public class BallDrawable extends Drawable {
    private final Paint paint= new Paint(Paint.ANTI_ALIAS_FLAG);

    public void setColor(int color){
        paint.setColor(color);
    }

    public void setX(int x) {
        Rect bounds = getBounds();
        bounds.set(x,bounds.top,x+bounds.width(),bounds.top+bounds.height());
    }

    public void setY(int y) {
        Rect bounds = getBounds();
        bounds.set(bounds.left,y,bounds.left+bounds.width(),y+bounds.height());
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        Rect bounds = getBounds();
        canvas.translate(bounds.left,bounds.top);
        canvas.drawCircle(bounds.width()/2f,bounds.height()/2f,Math.min(bounds.width(),bounds.height())/2f,paint);
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    @Override
    public void invalidateSelf() {
        //nothing
    }
}
