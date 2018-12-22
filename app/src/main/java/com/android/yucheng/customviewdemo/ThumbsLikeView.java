package com.android.yucheng.customviewdemo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;

/**
 * Created by lingjiu on 2018/12/21.
 */
public class ThumbsLikeView extends View implements ValueAnimator.AnimatorUpdateListener {

    private static int DEFAULT_WIDTH, DEFAULT_HEIGHT;
    private Drawable likeSelectedDrawable;
    private Drawable likeUnSelectedDrawable;
    private Drawable likeShiningDrawable;
    private int mWidth;
    private int mHeight;
    //当前是否点赞
    private boolean isLike;
    private float outProgress, unLikeOutProgress, unLikeInProgress, likeInProgress, shinInProgress;
    private AnimatorSet outAnim;
    private AnimatorSet inAnim;
    private int padding;
    //动画缩放矩阵
    private Matrix matrix;
    private Rect likeBounds;
    private Rect shinBounds;
    //绘制中心点
    private Point point;
    private int squareSide;
    private float thumbsOffsetY;
    private float shinOffsetY;
    private Path path;


    public ThumbsLikeView(Context context) {
        this(context, null);
    }

    public ThumbsLikeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThumbsLikeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        likeSelectedDrawable = ContextCompat.getDrawable(context, R.drawable.ic_messages_like_selected);
        likeUnSelectedDrawable = ContextCompat.getDrawable(context, R.drawable.ic_messages_like_unselected);
        likeShiningDrawable = ContextCompat.getDrawable(context, R.drawable.ic_messages_like_selected_shining);

        ObjectAnimator likeOutAnim = ObjectAnimator.ofFloat(this, "outProgress", 0, 1);
        likeOutAnim.addUpdateListener(this);
        ObjectAnimator unLikeOutAnim = ObjectAnimator.ofFloat(this, "unLikeOutProgress", 0, 1);
        unLikeOutAnim.addUpdateListener(this);
        outAnim = new AnimatorSet();
        outAnim.play(likeOutAnim).before(unLikeOutAnim);
        ObjectAnimator unLikeInAnim = ObjectAnimator.ofFloat(this, "unLikeInProgress", 0, 1);
        ObjectAnimator likeInAnim = ObjectAnimator.ofFloat(this, "likeInProgress", 0, 1);
        likeInAnim.setInterpolator(new BounceInterpolator());
        ObjectAnimator shinInAnim = ObjectAnimator.ofFloat(this, "shinInProgress", 0, 1);
        unLikeInAnim.addUpdateListener(this);
        shinInAnim.addUpdateListener(this);
        inAnim = new AnimatorSet();
        inAnim.play(likeInAnim).with(shinInAnim).after(unLikeInAnim);

        //留一个内边距做动画
        padding = (int) DimensionUtils.dpToPx(6, getContext());
        DEFAULT_WIDTH = likeSelectedDrawable.getIntrinsicWidth() + padding;
        DEFAULT_HEIGHT = likeSelectedDrawable.getIntrinsicHeight() + likeShiningDrawable.getIntrinsicHeight();
        matrix = new Matrix();
        likeBounds = new Rect(padding, padding, likeSelectedDrawable.getIntrinsicWidth() - padding,
                likeSelectedDrawable.getIntrinsicHeight() - padding);
        shinBounds = new Rect(padding, padding, likeShiningDrawable.getIntrinsicWidth() - padding,
                likeShiningDrawable.getIntrinsicHeight() - padding);
        point = new Point();
        path = new Path();
    }

    public void setOutProgress(float progress) {
        outProgress = progress;
        Log.i("tag", " outProgress= " + outProgress);
    }

    public void setUnLikeOutProgress(float progress) {
        unLikeOutProgress = progress;
        Log.i("tag", " unLikeOutProgress= " + unLikeOutProgress);
    }

    public void setUnLikeInProgress(float progress) {
        unLikeInProgress = progress;
        Log.i("tag", " unLikeInProgress= " + unLikeInProgress);
    }

    public void setLikeInProgress(float progress) {
        likeInProgress = progress;
        Log.i("tag", " likeInProgress= " + likeInProgress);
    }

    public void setShinInProgress(float progress) {
        shinInProgress = progress;
        Log.i("tag", " shinInProgress= " + shinInProgress);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        like(isSelected(),true);
    }

    /**
     * 点赞
     *
     * @param isLike
     */
    public void like(boolean isLike, boolean needAnim) {
        this.isLike = isLike;
        if (isLike) {
            if (needAnim) {
                inAnim.start();
            } else {
                outProgress = 1;
                invalidate();
            }
        } else {
            if (needAnim) {
                outAnim.start();
            } else {
                unLikeInProgress = 1;
                likeInProgress=1;
                shinInProgress=1;
                invalidate();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int withMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (withMode == MeasureSpec.AT_MOST) {
            widthSize = DEFAULT_WIDTH;
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = DEFAULT_HEIGHT;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        point.x = mWidth / 2;
        point.y = mHeight / 2;
        squareSide = (int) (Math.min(mWidth, mHeight) * 0.8);
        thumbsOffsetY = squareSide * 0.3f;
        shinOffsetY = squareSide * 0.2f;
        path.moveTo(point.x, point.y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int left = point.x - squareSide / 2;
        int top = (int) (point.y - squareSide / 2 + thumbsOffsetY);
        int right = point.x + squareSide / 2;
        int bottom = (int) (point.y + squareSide / 2 + thumbsOffsetY);
        if (isLike) {
            likeDraw(canvas, left, top, right, bottom);
        } else {
            int unSelectedDrawableChangeSize = (int) (outProgress * squareSide * 0.1f);
            likeBounds.set(left + unSelectedDrawableChangeSize, top + unSelectedDrawableChangeSize,
                    right - unSelectedDrawableChangeSize, bottom - unSelectedDrawableChangeSize);
            if (outProgress < 1) {
                //闪光延时退出
                if (outProgress < 0.4) {
                    top = (int) (point.y - squareSide / 2 - shinOffsetY);
                    bottom = (int) (point.y + squareSide / 2 - shinOffsetY);
                    shinBounds.set(left, top, right, bottom);
                    likeShiningDrawable.setBounds(shinBounds);
                    likeShiningDrawable.draw(canvas);
                }
                likeSelectedDrawable.setBounds(likeBounds);
                likeSelectedDrawable.draw(canvas);
            } else if (outProgress == 1) {
                int selectedDrawableChangeSize = (int) (unLikeOutProgress * squareSide * 0.1f);
                likeBounds.set(likeBounds.left - selectedDrawableChangeSize, likeBounds.top - selectedDrawableChangeSize,
                        likeBounds.right + selectedDrawableChangeSize, likeBounds.bottom + selectedDrawableChangeSize);
                likeUnSelectedDrawable.setBounds(likeBounds);
                likeUnSelectedDrawable.draw(canvas);
            }

        }


    }

    private void likeDraw(Canvas canvas, int left, int top, int right, int bottom) {
        //进入的动画
        int unSelectedDrawableChangeSize = (int) (unLikeInProgress * squareSide * 0.1f);
        likeBounds.set(left + unSelectedDrawableChangeSize, top + unSelectedDrawableChangeSize,
                right - unSelectedDrawableChangeSize, bottom - unSelectedDrawableChangeSize);
        if (unLikeInProgress < 1) {
            Log.e("onDraw", "likeBounds = " + likeBounds.toString() + "unSelectedDrawableChangeSize =" + unSelectedDrawableChangeSize);
            likeUnSelectedDrawable.setBounds(likeBounds);
            likeUnSelectedDrawable.draw(canvas);
        } else if (unLikeInProgress == 1) {
            int selectedDrawableChangeSize = (int) (likeInProgress * squareSide * 0.1f);
            likeBounds.set(likeBounds.left - selectedDrawableChangeSize, likeBounds.top - selectedDrawableChangeSize,
                    likeBounds.right + selectedDrawableChangeSize, likeBounds.bottom + selectedDrawableChangeSize);
            //Log.e("onDraw","likeBounds2 = "+likeBounds.toString());
            likeSelectedDrawable.setBounds(likeBounds);
            likeSelectedDrawable.draw(canvas);
            //闪光靠上画
            left = point.x - squareSide / 2;
            top = (int) (point.y - squareSide / 2 - shinOffsetY);
            right = point.x + squareSide / 2;
            bottom = (int) (point.y + squareSide / 2 - shinOffsetY);
            shinBounds.set(left, top, right, bottom);
            //path.moveTo(point.x,(bottom+top)/2);
            path.reset();
            path.addCircle(point.x, (bottom + top) / 2, (float) (shinInProgress * squareSide), Path.Direction.CW);
            canvas.save();
            canvas.clipPath(path);
            likeShiningDrawable.setBounds(shinBounds);
            likeShiningDrawable.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * <p>Notifies the occurrence of another frame of the animation.</p>
     *
     * @param animation The animation which was repeated.
     */
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        invalidate();
    }
}
