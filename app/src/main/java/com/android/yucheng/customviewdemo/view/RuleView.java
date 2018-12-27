package com.android.yucheng.customviewdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.BounceInterpolator;
import android.widget.Scroller;

import com.android.yucheng.customviewdemo.DimensionUtils;

/**
 * 仿薄荷健康直尺
 * Created by lingjiu on 2018/12/23.
 */
public class RuleView extends View {
    private final String TAG = getClass().getSimpleName();
    private Paint mPaint;
    private Paint mTextPaint;
    private int mWidth, mHeight;
    //0.1之间的距离(刻度稀疏程度)
    private float unitWidth;
    //最大值
    private float maxValue = 100;
    //最小值
    private float minValue;
    private int defaultHeight;
    //短线高度
    private float shortHeight;
    //长线高度
    private float longHeight;
    //三条线宽度
    private float strokeWidth, shortScaleWidth, longScaleWidth;
    private int scaledTouchSlop;
    private Scroller mScroller;
    private VelocityTracker velocityTracker;
    //可以滚动的最大距离
    private float mScrollMaxDistance;
    //直尺颜色
    private int scaleColor = Color.parseColor("#999999");
    //中心标准线颜色
    private int standerColor = Color.parseColor("#49BA72");
    private Scroller mBounceScroller;

    public RuleView(Context context) {
        this(context, null);
    }

    public RuleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RuleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#999999"));
        mPaint.setStrokeWidth(10);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setFakeBoldText(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        strokeWidth = 4;
        shortScaleWidth = 4;
        longScaleWidth = 8;
        unitWidth = DimensionUtils.dpToPx(10, context);
        defaultHeight = (int) DimensionUtils.dpToPx(60, context);
        scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context);
        mBounceScroller = new Scroller(context, new BounceInterpolator());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST)
            heightSize = defaultHeight;
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        //另外一半留着绘制文字
        shortHeight = mHeight / 4;
        longHeight = mHeight / 2;
        mTextPaint.setTextSize(mHeight / 2 - DimensionUtils.dpToPx(4, getContext()));

        mScrollMaxDistance = unitWidth * 10 * (maxValue - minValue) / 2;
    }

    float downX;
    //是否是滑动
    boolean isTouchSlop;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        createVelocityTracker(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = event.getX();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float offsetX = event.getX() - downX;
            //offsetX大于这个临界值可视为再滚动
            if (Math.abs(offsetX) > scaledTouchSlop || isTouchSlop) {
                isTouchSlop = true;
                //Log.i(TAG, "offsetx= " + offsetX + " getScrollX()" + getScrollX() + "   mScrollMaxDistance=" + mScrollMaxDistance);
                if (Math.abs(getScrollX() - offsetX) < mScrollMaxDistance) {
                    scrollBy(-(int) offsetX, 0);
                }
                downX = event.getX();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (isTouchSlop) {
                velocityTracker.computeCurrentVelocity(1000);
                float xVelocity = velocityTracker.getXVelocity();
                float scrollX = xVelocity * 0.4f;
                Log.i(TAG, "scrollX= " + scrollX + "   mScrollMaxDistance=" + mScrollMaxDistance + "   getScrollX()=" + getScrollX());
                Scroller scroller = mScroller;
                if (Math.abs(-scrollX + getScrollX()) > mScrollMaxDistance) {
                    if (xVelocity > 0) {
                        scrollX = (mScrollMaxDistance - Math.abs(getScrollX()));
                    } else {
                        scrollX = -(mScrollMaxDistance - Math.abs(getScrollX()));
                    }
                    scroller = mBounceScroller;
                }
                Log.i(TAG, "2222scrollX= " + scrollX + "   mScrollMaxDistance=" + mScrollMaxDistance);
                scroller.startScroll(getScrollX(), 0, (int) (-scrollX), 0);
                postInvalidate();
                isTouchSlop = false;
                recycleVelocity();
            }

        }
        return super.onTouchEvent(event);
    }

    private VelocityTracker createVelocityTracker(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
        return velocityTracker;
    }

    private void recycleVelocity() {
        velocityTracker.recycle();
        velocityTracker = null;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            Log.i(TAG, "mScroller.getCurrX() = " + mScroller.getCurrX());
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();
        } else if (!isTouchSlop) {
            adjustPosition(getScrollX());
        }
    }

    private void adjustPosition(float offset) {
        //Log.i(TAG, "offset = " + offset + "  unitWidth=  " + unitWidth + "     " + Math.ceil(offset % unitWidth));
        if (offset % unitWidth == 0) return;
        float adjustOffset = (float) Math.ceil(offset % unitWidth);
        mScroller.startScroll(getScrollX(), 0, -(int) adjustOffset, 0);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setStrokeWidth(strokeWidth);
        canvas.drawLine(0, 0, mWidth, 0, mPaint);
        float textOffset = (mTextPaint.descent() + mTextPaint.ascent()) / 2;
        //文字绘制y坐标
        float drawTextY = mHeight / 2 + mHeight / 4 - textOffset;
        float centerValue = (maxValue + minValue) / 2;
        //当前刻度值
        float curValue = minValue;
        String drawText;
        //开始绘制x的位置
        float positionX = mWidth / 2 - unitWidth * 10 * (maxValue - minValue) / 2;
        for (int i = 0; curValue < maxValue; i++) {
            curValue = minValue + 0.1f * i;
            //刻度高度
            float scaleHeight;
            //Log.i("yc","curValue = "+curValue+"  curValue % 10 ="+(curValue % 10 == 0) );
           /* Log.i("yc", "curValue = " + curValue + "  positionX = " + positionX + " unitWidth * i = " + unitWidth * i + "  positionX + unitWidth * i=" +
                    (positionX + unitWidth * i));*/
            mPaint.setColor(scaleColor);
            if (curValue * 10 % 10 == 0) {

                mPaint.setStrokeWidth(longScaleWidth);
                scaleHeight = longHeight;
                drawText = String.valueOf((int) curValue);
                canvas.drawText(drawText, positionX + unitWidth * i, drawTextY, mTextPaint);
            } else {
                mPaint.setStrokeWidth(shortScaleWidth);
                scaleHeight = shortHeight;
            }
            canvas.drawLine(positionX + unitWidth * i, 0,
                    positionX + unitWidth * i, scaleHeight, mPaint);
        }

        int scrollX = getScrollX();
        //划中心标准线
        mPaint.setColor(standerColor);
        mPaint.setStrokeWidth(longScaleWidth);
        canvas.drawLine(mWidth / 2 + scrollX, 0,
                mWidth / 2 + scrollX, longHeight + 6, mPaint);
    }
}
