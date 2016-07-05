package com.chzan.bezier.stick;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.CycleInterpolator;

import com.chzan.bezier.R;

/**
 * stick view
 * Created by chenzan on 2016/7/4.
 */
public class StickView extends View {

    private Paint mPaint;
    private PointF dragViewCirle;
    private PointF staticViewCirle;
    private int staticRadius = 10;
    private int dragRadius = 20;
    private PointF[] staticViewPoint;
    private PointF[] dragViewPoint;
    private PointF contronlPoint;
    private float maxOffset = 100;
    private boolean isBreak;//是否断开
    private boolean disAppear = false;//不显示
    private int mStatusBarHeight;
    private String text = "";
    private Paint mTextPaint;

    public StickView(Context context) {
        this(context, null);
    }

    public StickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(dragRadius * 1.0f);
    }

    public interface OnDragEventListener {
        void onDisappear(PointF dragViewCenter);

        void onReset(boolean isToReset);
    }

    public OnDragEventListener mOnDragEventListener;

    public OnDragEventListener getmOnDragEventListener() {
        return mOnDragEventListener;
    }

    public void setmOnDragEventListener(OnDragEventListener mOnDragEventListener) {
        this.mOnDragEventListener = mOnDragEventListener;
    }

    public int getDragRadius() {
        return dragRadius;
    }

    public void setDragRadius(int dragRadius) {
        this.dragRadius = dragRadius;
    }

    public void setStaticRadius(int staticRadius) {
        this.staticRadius = staticRadius;
    }

    public float getMaxOffset() {
        return maxOffset;
    }

    public void setMaxOffset(float maxOffset) {
        this.maxOffset = maxOffset;
    }

    public void setNumber(int num) {
        text = String.valueOf(num);
    }

    /**
     * 初始化圆的圆心坐标
     *
     * @param x
     * @param y
     */
    public void initCenter(float x, float y) {
        dragViewCirle = new PointF(x, y);
        staticViewCirle = new PointF(x, y);
        invalidate();
    }

    private void computePoint(float tempStaticRadius) {
        float ditanceX = dragViewCirle.x - staticViewCirle.x;
        float ditanceY = dragViewCirle.y - staticViewCirle.y;
        double radins = Math.atan(ditanceY / ditanceX);//弧度
        //停留圆
        staticViewPoint = new PointF[]{
                new PointF(staticViewCirle.x - tempStaticRadius * (float) Math.sin(radins),
                        staticViewCirle.y + tempStaticRadius * (float) Math.cos(radins)),
                new PointF(staticViewCirle.x + tempStaticRadius * (float) Math.sin(radins),
                        staticViewCirle.y - tempStaticRadius * (float) Math.cos(radins))
        };
        //拖拽圆
        dragViewPoint = new PointF[]{
                new PointF(dragViewCirle.x - dragRadius * (float) Math.sin(radins),
                        dragViewCirle.y + dragRadius * (float) Math.cos(radins)),
                new PointF(dragViewCirle.x + dragRadius * (float) Math.sin(radins),
                        dragViewCirle.y - dragRadius * (float) Math.cos(radins))
        };
        //控制点
        float contronlY = (float) (staticViewCirle.y + Math.sin(radins) *
                (Math.sqrt(ditanceX * ditanceX + ditanceY * ditanceY) * 0.382));
        float contronlX = (float) (staticViewCirle.x + Math.cos(radins) *
                (Math.sqrt(ditanceX * ditanceX + ditanceY * ditanceY) * 0.382));
        contronlPoint = new PointF(contronlX, contronlY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(0, -mStatusBarHeight);
        if (!disAppear) {
            mPaint.setStyle(Paint.Style.FILL);
            Path mPath = new Path();
            float tempStaticRadius = getStaticRadius();
            computePoint(tempStaticRadius);
            if (!isBreak) {
                canvas.drawCircle(staticViewCirle.x, staticViewCirle.y, tempStaticRadius, mPaint);
                mPath.moveTo(staticViewPoint[0].x, staticViewPoint[0].y);
                mPath.quadTo(contronlPoint.x, contronlPoint.y, dragViewPoint[0].x, dragViewPoint[0].y);
                mPath.lineTo(dragViewPoint[1].x, dragViewPoint[1].y);
                mPath.quadTo(contronlPoint.x, contronlPoint.y, staticViewPoint[1].x, staticViewPoint[1].y);
            }
            canvas.drawCircle(dragViewCirle.x, dragViewCirle.y, dragRadius, mPaint);
            canvas.drawPath(mPath, mPaint);
            canvas.drawText(text, dragViewCirle.x, dragViewCirle.y + dragRadius / 2, mTextPaint);
        }
        canvas.restore();
    }

    private float getStaticRadius() {
        double distance = getDistanceByPoints(dragViewCirle, staticViewCirle);
        double min = Math.min(distance, maxOffset);
        float percent = (float) (min / maxOffset);
        Float evaluate = evaluate(percent, 1f, 0.2f);
        return evaluate * staticRadius;
    }

    private double getDistanceByPoints(PointF dragViewCirle, PointF staticViewCirle) {
        float x = dragViewCirle.x - staticViewCirle.x;
        float y = dragViewCirle.y - staticViewCirle.y;
        return Math.sqrt(x * x + y * y);
    }

    public Float evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isBreak = false;
                float downX = event.getRawX();
                float downY = event.getRawY();
                updateDragCenter(downX, downY);
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getRawX();
                float moveY = event.getRawY();
                updateDragCenter(moveX, moveY);
                calculateDistance();
                break;
            case MotionEvent.ACTION_UP:
                judgeFinalState();
                break;
            case MotionEvent.ACTION_CANCEL:
                disAppear = true;
                break;
        }
        return true;
    }

    private void judgeFinalState() {
        if (isBreak) {
            double distanceByPoints = getDistanceByPoints(dragViewCirle, staticViewCirle);
            if (distanceByPoints > maxOffset) {
                disAppear = true;
                if (mOnDragEventListener != null) {
                    mOnDragEventListener.onDisappear(dragViewCirle);
                }
            } else {
                updateDragCenter(staticViewCirle.x, staticViewCirle.y);
                if (mOnDragEventListener != null) {
                    mOnDragEventListener.onReset(true);
                }
            }
        } else {
            //值动画控制 分数值
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f);
            valueAnimator.setDuration(200);
            valueAnimator.setInterpolator(new CycleInterpolator(4));
//            valueAnimator.setInterpolator(new BounceInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedFraction = animation.getAnimatedFraction();
                    PointF pointF = getPointByFraction(animatedFraction);
                    updateDragCenter(pointF.x, pointF.y);
                }
            });
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (mOnDragEventListener != null) {
                        mOnDragEventListener.onReset(true);
                    }
                }
            });
            valueAnimator.start();
        }
    }

    private PointF getPointByFraction(float animatedFraction) {
        FloatEvaluator mFloatEvaluator = new FloatEvaluator();
        Float xPoint = mFloatEvaluator.evaluate(animatedFraction, dragViewCirle.x, staticViewCirle.x);
        Float yPoint = mFloatEvaluator.evaluate(animatedFraction, dragViewCirle.y, staticViewCirle.y);
        return new PointF(xPoint, yPoint);
    }

    private void calculateDistance() {
        double distanceByPoints = getDistanceByPoints(dragViewCirle, staticViewCirle);
        if (distanceByPoints > maxOffset) {
            isBreak = true;
        } else {
            isBreak = false;
        }
    }

    private void updateDragCenter(float downX, float downY) {
        dragViewCirle.set(downX, downY);
        invalidate();
    }

    public void setStatusBarHeight(int statusBarHeight) {
        this.mStatusBarHeight = statusBarHeight;
    }
}
