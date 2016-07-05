package com.chzan.bezier.stick;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chzan.bezier.R;

/**
 * Created by chenzan on 2016/7/5.
 */
public class StickViewTouchListener implements View.OnTouchListener, StickView.OnDragEventListener {
    private View clickPoint;
    private Context mContext;
    private StickView stickView;
    private final WindowManager windowManager;
    private final WindowManager.LayoutParams layoutParams;

    public StickViewTouchListener(TextView clickPoint) {
        this.clickPoint = clickPoint;
        this.mContext = clickPoint.getContext();
        stickView = new StickView(mContext);
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.format = PixelFormat.TRANSLUCENT;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ViewParent parent = v.getParent();
                parent.requestDisallowInterceptTouchEvent(true);
                clickPoint.setVisibility(View.INVISIBLE);
                stickView.setStatusBarHeight(getStatusBarHeight(v));
                stickView.setNumber(Integer.valueOf((String) ("".equals(clickPoint.getTag()) ? 0 : clickPoint.getTag())));
                stickView.initCenter(event.getRawX(), event.getRawY());
                stickView.setDragRadius((int) (v.getWidth() / 2f));
                stickView.setmOnDragEventListener(this);
                windowManager.addView(stickView, layoutParams);
                break;
        }
        stickView.onTouchEvent(event);
        return true;
    }

    @Override
    public void onDisappear(PointF dragViewCenter) {
        if (windowManager != null && stickView.getParent() != null) {
            windowManager.removeView(stickView);
        }
        clickPoint.setVisibility(View.INVISIBLE);
        //容器
        final BubbleLayout bubbleLayout = new BubbleLayout(mContext);
        bubbleLayout.setCenter((int) dragViewCenter.x, (int) dragViewCenter.y - getStatusBarHeight(clickPoint));
        final ImageView imageView = new ImageView(mContext);
        bubbleLayout.addView(imageView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //动画
        imageView.setImageResource(R.drawable.anim_bubble_pop);
        AnimationDrawable mAnimDrawable = (AnimationDrawable) imageView
                .getDrawable();
        mAnimDrawable.start();
        windowManager.addView(bubbleLayout, layoutParams);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                windowManager.removeView(bubbleLayout);
            }
        }, 501);
    }


    @Override
    public void onReset(boolean isToReset) {
        if (windowManager != null && stickView.getParent() != null) {
            windowManager.removeView(stickView);
        }
        clickPoint.setVisibility(View.VISIBLE);
    }

    /**
     * 获取状态栏高度
     *
     * @param v
     * @return
     */
    public static int getStatusBarHeight(View v) {
        if (v == null) {
            return 0;
        }
        Rect frame = new Rect();
        v.getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }
}
