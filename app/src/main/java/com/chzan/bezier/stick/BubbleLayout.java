package com.chzan.bezier.stick;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by chenzan on 2016/7/5.
 */
public class BubbleLayout extends FrameLayout {
    private int mCenterX;
    private int mCenterY;

    public BubbleLayout(Context context) {
        this(context, null);
    }

    public BubbleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCenter(int x, int y) {
        mCenterX = x;
        mCenterY = y;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        View childAt = getChildAt(0);
        if (childAt != null && childAt.getVisibility() != GONE) {
            int width = childAt.getMeasuredWidth() / 2;
            int height = childAt.getMeasuredHeight() / 2;
            childAt.layout(mCenterX - width, mCenterY - height, mCenterX + width, mCenterY + height);
        }
    }
}
