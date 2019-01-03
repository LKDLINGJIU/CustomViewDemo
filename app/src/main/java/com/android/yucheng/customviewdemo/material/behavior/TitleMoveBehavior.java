package com.android.yucheng.customviewdemo.material.behavior;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.android.yucheng.customviewdemo.R;

/**
 * Created by lingjiu on 2019/1/3.
 */
public class TitleMoveBehavior extends CoordinatorLayout.Behavior<View> {

    private float mStartY;

    private float mFinalScaleRatio = 0.8f;

    public TitleMoveBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.title_behavior);
            mFinalScaleRatio = typedArray.getFloat(R.styleable.title_behavior_scale_ratio, 1);
            typedArray.recycle();
        }
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof Toolbar;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if (mStartY == 0) {
            mStartY = dependency.getY();
        }

        float percent = dependency.getY() / mStartY;
        child.setY(dependency.getY() + dependency.getHeight() / 2 - child.getHeight() / 2);
        child.setScaleX(1 - (1 - mFinalScaleRatio) * (1 - percent));
        child.setScaleY(1 - (1 - mFinalScaleRatio) * (1 - percent));
        return true;
    }
}
