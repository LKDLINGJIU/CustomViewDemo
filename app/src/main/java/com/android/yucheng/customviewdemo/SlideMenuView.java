package com.android.yucheng.customviewdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by lingjiu on 2018/12/24.
 * <p>
 * 1摆放menu的位置
 * 2触摸边缘滑动时,移动menu的位置,临界值的判断
 * 3滑动开关中间点判断矫正(scroller自动滚过去)
 * Scroller用法
 * scroller.startScroll();(开始滚动)
 * computeScroll;//重绘回调(draw方法里面可以看到)
 * scroller.computeScrollOffset()(是否结束滚动)
 */
public class SlideMenuView extends ViewGroup {
    private final String TAG = getClass().getSimpleName();
    private View mainView;
    private View menuView;
    private float mMainViewWidth, mMenuViewWidth;
    //滑动临界值
    private int scaledEdgeSlop;
    //是否是滑动状态
    private boolean isTouchSlop;
    private Scroller scroller;

    public SlideMenuView(Context context) {
        super(context);
        init(context);
    }

    public SlideMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SlideMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        scaledEdgeSlop = ViewConfiguration.get(context).getScaledEdgeSlop();
        scroller = new Scroller(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mainView = getChildAt(0);
        menuView = getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mMainViewWidth = mainView.getMeasuredWidth();
        mMenuViewWidth = menuView.getMeasuredWidth();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mainView.layout(l, t, r, b);
        menuView.layout((int) (l - mMenuViewWidth), t, 0, b);
    }

    private float downX, downY;


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (MotionEvent.ACTION_DOWN == ev.getAction()) {
            downX = ev.getX();
            downY = ev.getY();

        } else if (MotionEvent.ACTION_MOVE == ev.getAction()) {

            float offsetX = downX - ev.getX();
            float offsetY = downY - ev.getY();
            if (isTouchSlop || Math.abs(offsetX) > scaledEdgeSlop && Math.abs(offsetX) > Math.abs(offsetY)) {
                //超过临界值且水平距离大于纵向距离
                isTouchSlop = true;
                //Log.i(TAG, "offsetx= " + offsetX + " getScrollX()" + getScrollX());
                //这边计算偏移量总值,一定要加上接下来滑动的值offsetX
                float scrollX = getScrollX() + offsetX;
                if (scrollX > 0) {
                    scrollTo(0, 0);
                } else if (scrollX < -mMenuViewWidth) {
                    scrollTo((int) -mMenuViewWidth, 0);
                } else {
                    scrollBy((int) offsetX, 0);
                }
                downX = ev.getX();
                downY = ev.getY();
            }
        } else if (MotionEvent.ACTION_UP == ev.getAction()) {
            Log.i(TAG, " getScrollX() = " + getScrollX());
            if (getScrollX() < 0 && getScrollX() > -mMenuViewWidth / 2) {
                scroller.startScroll(getScrollX(), 0, -getScrollX(), 0);
            } else if (getScrollX() > -mMenuViewWidth && getScrollX() < -mMenuViewWidth / 2) {
                scroller.startScroll(getScrollX(), 0, (int) (-mMenuViewWidth - getScrollX()), 0);
            }
            postInvalidate();
            isTouchSlop = false;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), 0);
            postInvalidate();
        }
    }
}
