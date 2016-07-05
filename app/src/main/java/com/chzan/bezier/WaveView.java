package com.chzan.bezier;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

/**
 * 水波
 * Created by chenzan on 2016/7/1.
 */
public class WaveView extends View {
    private static final String TAG = "WaveView";
    //view的宽高
    private int mWidth, mHeight;
    private float mWaterHeight;
    //波峰
    private float mWavePeak = 35f;
    //波谷
    private float mWaveTrough = 35f;
    private boolean viewSizeHas = false;

    private Paint mPaint;
    private Path mPath;
    private int mWaterColor = 0xBB0000FF;
    private PointF mOutOne;
    private PointF mOutTwo;
    private PointF mOutThree;
    private PointF mOutControlOne;
    private PointF mOutControlTwo;
    private PointF mInOne;
    private PointF mInTwo;
    private PointF mInThree;
    private PointF mInControlOne;
    private PointF mInControlTwo;
    private ValueAnimator valueAnimator;

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mWaterColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();
        mPath.moveTo(mOutOne.x, mOutOne.y);
        mPath.quadTo(mOutControlOne.x, mOutControlOne.y, mOutTwo.x, mOutTwo.y);
        mPath.quadTo(mOutControlTwo.x, mOutControlTwo.y, mOutThree.x, mOutThree.y);
        mPath.quadTo(mInControlOne.x, mInControlOne.y, mInTwo.x, mInTwo.y);
        mPath.quadTo(mInControlTwo.x, mInControlTwo.y, mInThree.x, mInThree.y);
        mPath.lineTo(mInThree.x, mHeight);
        mPath.lineTo(mOutOne.x, mHeight);
        mPath.lineTo(mOutOne.x, mOutOne.y);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e(TAG, "onSizeChanged: srcW--srcH:" + w + "--" + h + "()()()()" + "oldw---oldH:" +
                oldw + "--" + oldh);
        if (!viewSizeHas) {
            mWidth = w;
            mHeight = h;
            viewSizeHas = true;
            mWaterHeight = h * 2f / 3;
            initPath();
        }
    }

    private void initPath() {
        mOutOne = new PointF(-mWidth, mHeight - mWaterHeight);
        mOutTwo = new PointF(-mWidth / 2f, mHeight - mWaterHeight);
        mOutThree = new PointF(0, mHeight - mWaterHeight);

        mOutControlOne = new PointF(-mWidth + mWidth / 4, mOutOne.y + mWaveTrough);
        mOutControlTwo = new PointF(-mWidth / 4, mOutOne.y - mWavePeak);

        mInOne = new PointF(0, mHeight - mWaterHeight);
        mInTwo = new PointF(mWidth / 2, mHeight - mWaterHeight);
        mInThree = new PointF(mWidth, mHeight - mWaterHeight);

        mInControlOne = new PointF(mWidth / 4, mInOne.y + mWaveTrough);
        mInControlTwo = new PointF(mWidth * 3f / 4, mInOne.y - mWavePeak);
    }

    //获得焦点失去焦点的时候调用在这个时候可以获得宽高等值
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        // 开始波动
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(mOutOne.x, 0);
            valueAnimator.setDuration(2000);
            valueAnimator.setRepeatCount(Animation.INFINITE);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mOutOne.x = (float) animation.getAnimatedValue();
                    mOutTwo = new PointF(mOutOne.x + mWidth - mWidth / 2f, mHeight - mWaterHeight);
                    mOutThree = new PointF(mOutOne.x + mWidth, mHeight - mWaterHeight);

                    mOutControlOne = new PointF(mOutOne.x + mWidth - mWidth + mWidth / 4, mOutOne.y + mWaveTrough);
                    mOutControlTwo = new PointF(mOutOne.x + mWidth - mWidth / 4, mOutOne.y - mWavePeak);

                    mInOne = new PointF(mOutOne.x + mWidth, mHeight - mWaterHeight);
                    mInTwo = new PointF(mOutOne.x + mWidth + mWidth / 2, mHeight - mWaterHeight);
                    mInThree = new PointF(mOutOne.x + mWidth + mWidth, mHeight - mWaterHeight);

                    mInControlOne = new PointF(mOutOne.x + mWidth + mWidth / 4, mInOne.y + mWaveTrough);
                    mInControlTwo = new PointF(mOutOne.x + mWidth + mWidth * 3f / 4, mInOne.y - mWavePeak);
                    invalidate();
                }
            });
            valueAnimator.start();
        }
    }

}
