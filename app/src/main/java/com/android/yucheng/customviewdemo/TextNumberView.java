package com.android.yucheng.customviewdemo;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Arrays;

/**
 * Created by lingjiu on 2018/12/21.
 */
public class TextNumberView extends View implements ValueAnimator.AnimatorUpdateListener {

    private Paint mPaint, mAnimPaint;
    //当前绘制数字字符
    private String[] mNumberChar = new String[2];
    //之前绘制数字字符
    private String[] mPreNumberChar = new String[3];
    private float mWidth, mHeight;
    //总数
    private int mSumNumber;
    private ObjectAnimator mAnim;
    private float progress;

    public TextNumberView(Context context) {
        this(context, null);
    }

    public TextNumberView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextNumberView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mSumNumber = 100;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#666666"));
        mPaint.setTextAlign(Paint.Align.RIGHT);
        mPaint.setTextSize(DimensionUtils.spToPx(14, context));

        mAnimPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAnimPaint.setColor(Color.parseColor("#666666"));
        mAnimPaint.setTextAlign(Paint.Align.RIGHT);
        mAnimPaint.setTextSize(DimensionUtils.spToPx(14, context));
        mAnim = ObjectAnimator.ofFloat(this, "progress", 0f, 1f);
        mAnim.setDuration(500);
        mAnim.addUpdateListener(this);
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

    }

    public void changeNumber(int number, boolean needAnim) {
        mPreNumberChar = Arrays.copyOf(mNumberChar, mNumberChar.length);
        int numLength = String.valueOf(number).length();
        mNumberChar = new String[numLength];
        for (int i = 0; i < numLength; i++) {
            mNumberChar[i] = String.valueOf((int) (number / Math.pow(10, i) % 10));
            Log.e("TextNumberView", "mNumberChar = " + mNumberChar[i]);
        }
        mSumNumber = number;
        requestLayout();
        //invalidate();
        if (needAnim) {
            mAnim.start();
        } else {
            invalidate();
        }
    }


    public void like(boolean isLike) {
        changeNumber(mSumNumber + 1, true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int withMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int maxChar = mPreNumberChar.length > mNumberChar.length ? mPreNumberChar.length : mNumberChar.length;
        if (withMode == MeasureSpec.AT_MOST) {
            widthSize = (int) (mPaint.measureText("0") * maxChar);
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = (int) ((mPaint.getFontMetrics().bottom - mPaint.getFontMetrics().top) * 1.2);
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float singleCharWidth = mPaint.measureText("0");
        float heightOffset = mPaint.descent() / 2 + mPaint.ascent() / 2;
        for (int i = 0; i < mNumberChar.length; i++) {
            String nowChar = mNumberChar[i];
            if (TextUtils.isEmpty(nowChar))
                continue;
            String preChar = i < mPreNumberChar.length ? mPreNumberChar[i] : "";
            if (!TextUtils.isEmpty(preChar) && !TextUtils.equals(preChar, nowChar)) {
                mAnimPaint.setAlpha((int) ((1 - progress) * 255));
                canvas.drawText(preChar, mWidth - singleCharWidth * i, (mHeight / 2 - heightOffset) * (1 - progress), mAnimPaint);
                mAnimPaint.setAlpha((int) (progress * 255));
                canvas.drawText(nowChar, mWidth - singleCharWidth * i, mHeight - (mHeight / 2 + heightOffset) * (progress), mAnimPaint);
                continue;
            }
            canvas.drawText(nowChar, mWidth - singleCharWidth * i, mHeight / 2 - heightOffset, mPaint);

        }
    }

}
