package com.android.yucheng.customviewdemo.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by lingjiu on 2019/1/3.
 */
public class VariableHeightFrameLayout extends FrameLayout implements ValueAnimator.AnimatorUpdateListener {
    //展开
    public static final int EXPAND = 1;
    //收缩
    public static final int COLLAPSE = 0;
    private int mCurStatus;
    private ValueAnimator mAnimator;
    private int mHeight;


    public VariableHeightFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public VariableHeightFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VariableHeightFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void init() {
        mAnimator = new ValueAnimator();
        mAnimator.setDuration(8_000);
        mAnimator.addUpdateListener(this);
        post(new Runnable() {
            @Override
            public void run() {
                mHeight = getHeight();
            }
        });
    }

    public void expand() {
        if (mCurStatus == EXPAND || mAnimator.isRunning()) return;
        Log.i("yc", "totalHeight = " + mHeight);
        setVisibility(View.VISIBLE);
        mAnimator.setFloatValues(0, mHeight);
        mAnimator.start();
    }

    public void collapse() {
        if (mCurStatus == COLLAPSE || mAnimator.isRunning()) return;
        Log.i("yc", "totalHeight = " + mHeight);
        mAnimator.setFloatValues(mHeight, 0);
        mAnimator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        float percent = animation.getAnimatedFraction();
        float height = (Float) animation.getAnimatedValue();
        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.height = (int) height;
        Log.i("yc", "percent = " + percent + "  lp.height=" + percent * mHeight);
        setLayoutParams(lp);
        if (percent == 1) {
            mCurStatus = mCurStatus == COLLAPSE ? EXPAND : COLLAPSE;
        }
    }
}
