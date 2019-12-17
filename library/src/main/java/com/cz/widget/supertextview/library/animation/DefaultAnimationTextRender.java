package com.cz.widget.supertextview.library.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;

import java.util.List;

/**
 * @author Created by cz
 * @date 2019-05-15 16:24
 * @email bingo110@126.com
 * 默认动画元素渲染器对象
 */
public class DefaultAnimationTextRender extends AbsAnimationTextRender {

    @Override
    protected Animator getEnterAnimator(List<AnimationLetter> animationLetterList) {
        AnimatorSet animatorSet = new AnimatorSet();
        ITextAnimator textAnimator = getTextAnimator();
        if(null!=textAnimator){
            //获得所有元素集
            for(int i=0;i<animationLetterList.size();i++){
                AnimationLetter animationLetter = animationLetterList.get(i);
                Animator enterAnimator = textAnimator.getEnterAnimator(animationLetter);
                if(null!=enterAnimator){
                    animatorSet.play(enterAnimator).after(i*10);
                    ValueAnimator valueAnimator = (ValueAnimator) enterAnimator;
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            invalidate();
                        }
                    });
                }
            }
        }
        return animatorSet;
    }

    @Override
    protected Animator getExitAnimator(List<AnimationLetter> animationLetterList) {
        AnimatorSet animatorSet=new AnimatorSet();
        ITextAnimator textAnimator = getTextAnimator();
        if(null!=textAnimator){
            //获得所有元素集
            for(int i=0;i<animationLetterList.size();i++){
                AnimationLetter animationLetter = animationLetterList.get(i);
                Animator exitAnimator = textAnimator.getExitAnimator(animationLetter);
                if(null!=exitAnimator){
                    animatorSet.play(exitAnimator).after(i*10);
                    ValueAnimator valueAnimator = (ValueAnimator) exitAnimator;
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            invalidate();
                        }
                    });
                }
            }
        }
        return animatorSet;
    }

    @Override
    public void onDraw(Canvas canvas) {
        //当前文本绘制区域
        Rect layoutBounds = getLayoutBounds();
        //裁切区域,保证绘图文本外的内容不显示
        canvas.clipRect(layoutBounds, Region.Op.INTERSECT);
        final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        canvas.drawRect(layoutBounds,paint);
    }
}
