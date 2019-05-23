package com.android.yucheng.customviewdemo.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.yucheng.customviewdemo.DimensionUtils;

import static com.android.yucheng.customviewdemo.utils.SharePreferenceUtils.SOFT_KEYBOARD_HEIGHT;


/**
 * 控制表情面板与键盘切换不闪烁
 * <p>
 * Created by lingjiu on 2018/7/24.
 * 思路:
 * 1点击键盘
 * 1>当前表情面板没显示
 * softmode = SOFT_INPUT_ADJUST_RESIZE 自动弹出即可,并记录键盘高度
 * 2>当前表情面板显示了
 * 固定上方contentView高度
 * 隐藏表情面板
 * 弹出键盘
 * 恢复上方contentView高度weight=1
 * 2点击表情面板
 * 1>当前键盘没显示
 * 表情面板显示,并从本地拿上次记录的键盘高度
 * 2>当前键盘显示了
 * 固定上方contentView高度
 * 隐藏键盘
 * 表情面板显示
 * 恢复上方contentView高度weight=1
 */
public class KeyboardUtils {
    public static final String TAG = KeyboardUtils.class.getName();

    private Activity mActivity;
    //文本输入框
    private EditText[] mEditTexts;
    //表情面板
    private View mEmojiPanelView;
    //内容View,即除了表情布局和输入框布局以外的布局
    //用于固定输入框一行的高度以防止跳闪
    private View mContentView;
    public static final int SOFT_KEYBOARD_HEIGHT_DEFAULT = 654;

    /**
     * @param activity
     * @param emojiPanelView       表情面板
     * @param emojiPanelSwitchView 表情切换按钮
     * @param contentView          上方内容区域Content
     * @param editText             可变参数,界面可获取焦点的EditText
     */
    public KeyboardUtils(Activity activity,
                         View emojiPanelView,
                         View emojiPanelSwitchView,
                         View contentView,
                         EditText... editText) {
        init(activity, emojiPanelView, emojiPanelSwitchView, contentView, editText);
    }

    private void init(Activity activity, View emojiPanelView, View emojiPanelSwitchView, View contentView, EditText... editText) {
        mActivity = activity;
        mEditTexts = editText;
        mEmojiPanelView = emojiPanelView;
        mContentView = contentView;
        for (EditText mEditText : mEditTexts) {
            mEditText.setOnTouchListener(onMyTouchListener);
        }
        if (mContentView != null) {
            mContentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mEmojiPanelView.isShown()) {
                        hideEmojiPanel(mEditTexts[0], false);
                    } else if (isSoftKeyboardShown()) {
                        hideSoftKeyboard();
                    }
                }
            });
        }
        //用于弹出表情面板的View
        emojiPanelSwitchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmojiPanelView.isShown()) {
                    hideEmojiPanel(mEditTexts[0], false);
                  /*  lockContentViewHeight();
                    hideEmojiPanel(mEditTexts[1], false);
                    unlockContentViewHeight();*/
                } else {
                    if (isSoftKeyboardShown()) {
                        lockContentViewHeight();
                        showEmojiPanel();
                        unlockContentViewHeight();
                    } else {
                        showEmojiPanel();
                    }
                }
            }
        });
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        init(mEditTexts[0]);
    }

    private View.OnTouchListener onMyTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP && KeyboardUtils.this.mEmojiPanelView.isShown()) {
                lockContentViewHeight();
                hideEmojiPanel((EditText) v, true);
                unlockContentViewHeight();
            }
            return false;
        }
    };

    /**
     * 如果之前没有保存过键盘高度值
     * 则在进入Activity时自动打开键盘，并把高度值保存下来
     *
     * @param mEditText
     */
    private void init(final EditText mEditText) {
        if (!SharePreferenceUtils.contains(SOFT_KEYBOARD_HEIGHT)) {
            mEmojiPanelView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showSoftKeyboard(mEditText, true);
                }
            }, 200);
        }
    }

    /**
     * 当点击返回键时需要先隐藏表情面板
     */
    public boolean interceptBackPress() {
        if (mEmojiPanelView.isShown()) {
            //  hideEmojiPanel((EditText) editText, false);
            return true;
        }
        return false;
    }

    /**
     * 锁定内容View以防止跳闪
     */
    private void lockContentViewHeight() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        layoutParams.height = mContentView.getHeight();
        layoutParams.weight = 0;
    }

    /**
     * 释放锁定的内容View
     */
    private void unlockContentViewHeight() {
        mEmojiPanelView.postDelayed(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
                lp.weight = 1;
                lp.height = 0;
            }
        }, 200);
    }

    /**
     * 获取键盘的高度 getScreenHeight()- rect.bottom,不用额外考虑沉浸式,以及导航栏弹起
     * 1080*1920手机,可能下面有虚拟导航栏
     * getScreenHeight()  正常1920,虚拟导航栏出来后1812  屏幕可用高度：会根据导航栏调整
     * mActivity.getWindow().getDecorView().getRootView().getHeight 一直是1920 不会根据导航栏调整
     * rect.bottom 正常1920,虚拟导航栏出来后1812; 键盘弹出后(有导航栏1015,无导航栏1123) 会根据导航栏调整
     * rect.top 沉浸式 = 0 ,非沉浸式 = 72
     */
    private int getSoftKeyboardHeight() {
        Rect rect = new Rect();
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        //屏幕当前可见高度，不包括状态栏
        //int displayHeight = rect.bottom - rect.top;
        //屏幕可用高度
        int availableHeight = DimensionUtils.getScreenHeight(mActivity);
        //用于计算键盘高度
        //int softInputHeight = availableHeight - displayHeight - DimensionUtils.getStatusBarHeight(mActivity);
        int softInputHeight = availableHeight - rect.bottom;
        Log.e(TAG, "rect.bottom=" + rect.bottom + "  rect.top=" + rect.top + "  availableHeight=" + availableHeight);
        Log.e(TAG, softInputHeight + "");
        if (softInputHeight > 0) {
            // 因为考虑到用户可能会主动调整键盘高度，所以只能是每次获取到键盘高度时都将其存储起来
            SharePreferenceUtils.putData(SOFT_KEYBOARD_HEIGHT, softInputHeight);
        }
        return softInputHeight;
    }

    /**
     * 获取本地存储的键盘高度值或者是返回默认值
     */
    private int getSoftKeyboardHeightLocalValue() {
        return (int) SharePreferenceUtils.getData(SOFT_KEYBOARD_HEIGHT, SOFT_KEYBOARD_HEIGHT_DEFAULT);
    }

    /**
     * 判断是否显示了键盘
     */
    private boolean isSoftKeyboardShown() {
        return getSoftKeyboardHeight() > 0;
    }

    /**
     * 令编辑框获取焦点并显示键盘
     */
    private void showSoftKeyboard(EditText mEditText, boolean saveSoftKeyboardHeight) {
        mEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, 0);
        // inputMethodManager.showSoftInput(mEditTexts, 0);
        if (saveSoftKeyboardHeight) {
            mEmojiPanelView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getSoftKeyboardHeight();
                }
            }, 200);
        }
    }

    /**
     * 隐藏键盘
     */
    private void hideSoftKeyboard() {
        // inputMethodManager.hideSoftInputFromWindow(mEditTexts.getWindowToken(), 0);
        View view = mActivity.getWindow().peekDecorView();
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 显示表情面板
     */
    private void showEmojiPanel() {
        int softKeyboardHeight = getSoftKeyboardHeight();
        if (softKeyboardHeight == 0) {
            softKeyboardHeight = getSoftKeyboardHeightLocalValue();
        } else {
            hideSoftKeyboard();
        }
        mEmojiPanelView.getLayoutParams().height = softKeyboardHeight;
        mEmojiPanelView.setVisibility(View.VISIBLE);
        if (emojiPanelVisibilityChangeListener != null) {
            emojiPanelVisibilityChangeListener.onShowEmojiPanel();
        }
    }

    /**
     * 隐藏表情面板，同时指定是否随后开启键盘
     */
    private void hideEmojiPanel(EditText editText, boolean showSoftKeyboard) {
        if (mEmojiPanelView.isShown()) {
            mEmojiPanelView.setVisibility(View.GONE);
            if (showSoftKeyboard) {
                showSoftKeyboard(editText, true);
            }
            if (emojiPanelVisibilityChangeListener != null) {
                emojiPanelVisibilityChangeListener.onHideEmojiPanel();
            }
        }
    }

    public interface OnEmojiPanelVisibilityChangeListener {

        void onShowEmojiPanel();

        void onHideEmojiPanel();
    }

    private OnEmojiPanelVisibilityChangeListener emojiPanelVisibilityChangeListener;

    public void setEmoticonPanelVisibilityChangeListener(OnEmojiPanelVisibilityChangeListener emojiPanelVisibilityChangeListener) {
        this.emojiPanelVisibilityChangeListener = emojiPanelVisibilityChangeListener;
    }

}