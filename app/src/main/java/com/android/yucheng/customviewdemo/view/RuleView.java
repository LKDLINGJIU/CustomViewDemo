package com.android.yucheng.customviewdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.android.yucheng.customviewdemo.DimensionUtils;

/**
 * 仿薄荷健康直尺
 * Created by lingjiu on 2018/12/23.
 */
public class RuleView extends View {

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
    }

    float downX;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = event.getX();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float offsetX = event.getX() - downX;
            //offsetX大于这个临界值可视为再滚动
            if (offsetX>scaledTouchSlop) {
                scrollBy(-(int) offsetX,0);
            }
        }
        return super.onTouchEvent(event);
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
        for (int i = 0; curValue <= maxValue; i++) {
            curValue = minValue + 0.1f * i;
            //刻度高度
            float scaleHeight;
            //Log.i("yc","curValue = "+curValue+"  curValue % 10 ="+(curValue % 10 == 0) );
            Log.i("yc", "curValue = " + curValue + "  positionX = " + positionX + " unitWidth * i = " + unitWidth * i + "  positionX + unitWidth * i=" +
                    (positionX + unitWidth * i));
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
    }
}
