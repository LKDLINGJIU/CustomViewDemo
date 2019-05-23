package com.android.yucheng.customviewdemo.utils;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/**
 * 解决使用沉浸式的时候,键盘弹起不会弹起相应的edit内容区域的问题
 * <p>
 * 如果使用了自己增加顶部view高度的办法,则使用下面这个可以解决问题
 * 如果使用了系统提供的fitsSystemWindows = true,则已适配上述问题
 */
public class AndroidBug5497Workaround {
    public static void assistActivity(View content) {
        new AndroidBug5497Workaround(content);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private ViewGroup.LayoutParams frameLayoutParams;

    private AndroidBug5497Workaround(View content) {
        if (content != null) {
            mChildOfContent = content;
            mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    possiblyResizeChildOfContent();
                }
            });
            frameLayoutParams = mChildOfContent.getLayoutParams();
        }
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            //如果两次高度不一致
            //将计算的可视高度设置成视图的高度
            frameLayoutParams.height = usableHeightNow;
            mChildOfContent.requestLayout();//请求重新布局
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        //计算视图可视高度
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom);
    }
}
