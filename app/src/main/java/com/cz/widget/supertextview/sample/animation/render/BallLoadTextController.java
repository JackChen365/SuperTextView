package com.cz.widget.supertextview.sample.animation.render;

import android.animation.*;
import android.graphics.*;
import android.text.TextUtils;
import android.widget.TextView;

import com.cz.widget.supertextview.library.animation.AbsAnimationTextRender;
import com.cz.widget.supertextview.library.animation.AnimationLetter;
import com.cz.widget.supertextview.sample.animation.render.drawable.BallDrawable;

import java.util.List;

/**
 * @author :Created by cz
 * @date 2019-05-16 13:54
 * @email bingo110@126.com
 * 一个球形加载效果
 */
public class BallLoadTextController extends AbsAnimationTextRender {
    /**
     * 附加drawable对象
     */
    private final BallDrawable ballDrawable=new BallDrawable();
    /**
     * 动画更新重绘监听对象
     */
    private final ValueAnimator.AnimatorUpdateListener updateListener=new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            invalidate();
        }
    };

    public BallLoadTextController() {
        ballDrawable.setColor(Color.WHITE);
        ballDrawable.setBounds(0,0,8,8);
    }

    @Override
    protected Animator getEnterAnimator(List<AnimationLetter> animationLetters) {
        Animator lastAnimator=null;
        final AnimatorSet animatorSet=new AnimatorSet();
        AnimationLetter lastElement=null;
        for(AnimationLetter element:animationLetters){
            int leftPadding = 0;
            int topPadding = 0;
            Rect bounds = element.getBounds();
            float offsetX=null==lastElement?bounds.left-bounds.width()/2:lastElement.getBounds().centerX();
            RectF rectF = new RectF(leftPadding + offsetX, topPadding + bounds.top - bounds.height(), leftPadding + bounds.centerX(), topPadding + bounds.top);
            ArcAnimator arcAnimator = getArcAnimator(rectF);
            //第二个动画
            ValueAnimator animator1= ObjectAnimator.ofFloat(element,"y",bounds.top,bounds.top+10);
            ValueAnimator animator2= ObjectAnimator.ofFloat(element,"y",bounds.top+10, bounds.top);
            animator1.addUpdateListener(updateListener);
            animator2.addUpdateListener(updateListener);
            animatorSet.playSequentially(arcAnimator,animator1,animator2);
            //组合动画
            if(null!=lastAnimator){
                animatorSet.playSequentially(lastAnimator,arcAnimator);
            }
            lastElement=element;
            lastAnimator=arcAnimator;
        }
        //最后一个
        if(!animationLetters.isEmpty()){
            //重新播放
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animatorSet.start();
                }
            });
        }
        return animatorSet;
    }

    @Override
    protected Animator getExitAnimator(List<AnimationLetter> animationLetters) {
        return null;
    }

    @Override
    public void startExitAnimator() {
    }

    @Override
    public void onDraw(Canvas canvas) {
        //测试测试信息
        debugDraw(canvas);
        // 绘白色小球
        ballDrawable.draw(canvas);
    }

    /**
     * 测试绘制信息
     * @param canvas
     */
    private void debugDraw(Canvas canvas) {
        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(1f);
        paint.setStyle(Paint.Style.STROKE);
        AnimationLetter lastElement=null;
        List<AnimationLetter> animationLetters = getAnimationLetterList();
        for(AnimationLetter element:animationLetters) {
            int leftPadding = 0;
            int topPadding = 0;
            Rect bounds = element.getBounds();
            float offsetX=null==lastElement?bounds.left-bounds.width()/2:lastElement.getBounds().centerX();
            RectF rectF = new RectF(leftPadding + offsetX, topPadding + bounds.top - bounds.height(), leftPadding + bounds.centerX(), topPadding + bounds.top);
            canvas.drawRect(rectF,paint);
            lastElement=element;

        }
    }

    /**
     * 获得一个弧形动画对象
     * @param rectF
     * @return
     */
    private ArcAnimator getArcAnimator(RectF rectF) {
        final ArcAnimator arcAnimator = new ArcAnimator(rectF, 180f, 360f);
        arcAnimator.setDuration(600);
        arcAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ballDrawable.setX((int) (arcAnimator.getX() - ballDrawable.getBounds().width() / 2));
                ballDrawable.setY((int) arcAnimator.getY());
                invalidate();
            }
        });
        return arcAnimator;
    }

    public class ArcAnimator extends ValueAnimator {
        private final float start,end;
        private Path path= new Path();
        private PathMeasure pathMeasure=new PathMeasure();
        private float[] pos = new float[2];
        private float[] tan = new float[2];

        public ArcAnimator(RectF rect, float start, float end) {
            this.start = start;
            this.end = end;
            setFloatValues(1f);
            addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setFraction(animation.getAnimatedFraction());
                }
            });
            path.addOval(rect,Path.Direction.CW);
            pathMeasure.setPath(path,false);
        }

        public float getX() {
            return pos[0];
        }

        public float getY() {
            return pos[1];
        }

        public void setFraction(float fraction) {
            //当前行走进度
            float startFraction=start/360f;
            float endFraction=end/360f;
            float fractionValue=startFraction+(endFraction-startFraction)*fraction;
            float distance=pathMeasure.getLength() * fractionValue;
            pathMeasure.getPosTan(distance,pos,tan);
            //设置旋转角度
            float degrees= (float) (Math.atan2(tan[1]*1.0, tan[0]*1.0) * 180 / Math.PI);
        }
    }
}
