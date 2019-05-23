package com.android.yucheng.customviewdemo;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingjiu on 2019/5/14.
 * 防止键盘表情切换闪动
 */
public class KeyboardSoftActivity extends AppCompatActivity {

    private View emoj_panel_layout;
    private View edit_text;
    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_key_board);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            datas.add("数字" + i);
        }
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datas));
        listView.setStackFromBottom(true);
        edit_text = findViewById(R.id.edit_text);
        emoj_panel_layout = findViewById(R.id.emoj_panel_layout);
        findViewById(R.id.emoj_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isActive()) {
                    lockContentHeight();
                }
                emoj_panel_layout.setVisibility(View.VISIBLE);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                unlockContentHeight();
                /*edit_text.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        emoj_panel_layout.setVisibility(View.VISIBLE);
                    }
                }, 200);*/
            }
        });

        edit_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("tag", " event =" + event.getAction());
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (emoj_panel_layout.getVisibility() == View.VISIBLE) {
                        lockContentHeight();
                    }
                    emoj_panel_layout.setVisibility(View.GONE);
                    unlockContentHeight();
                }
                return false;
            }
        });

        edit_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("tag", " onClick");
            }
        });
    }

    public boolean isActive() {
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int height = getWindow().getDecorView().getHeight();
        int heightPixels = getResources().getDisplayMetrics().heightPixels;
        Log.i("tag", "height =" + height + "  rect.bottom=" + rect.bottom + "  rect.top=" + rect.top + "   heightPixels=" + heightPixels);
        return height != rect.bottom;
    }

    /**
     * 的获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    private void unlockContentHeight() {
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) listView.getLayoutParams();
                layoutParams.height = 0;
                layoutParams.weight = 1;
            }
        }, 200);
    }

    private void lockContentHeight() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) listView.getLayoutParams();
        layoutParams.height = listView.getHeight();
        layoutParams.weight = 0;
    }
}
