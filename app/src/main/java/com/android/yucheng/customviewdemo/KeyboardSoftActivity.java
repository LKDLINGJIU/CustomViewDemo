package com.android.yucheng.customviewdemo;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.yucheng.customviewdemo.utils.AndroidBug5497Workaround;

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_key_board);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.GRAY);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
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
            }
        });

        edit_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("tag", " onClick");
            }
        });
        AndroidBug5497Workaround.assistActivity(findViewById(R.id.contentView));

    }

}
