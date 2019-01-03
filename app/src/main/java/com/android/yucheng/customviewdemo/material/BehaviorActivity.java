package com.android.yucheng.customviewdemo.material;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.yucheng.customviewdemo.R;
import com.android.yucheng.customviewdemo.view.VariableHeightFrameLayout;

/**
 * Created by lingjiu on 2019/1/3.
 */
public class BehaviorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_behavior);

    }


    public void practice1(View view) {
        startActivity(new Intent(this, BehaviorPracticeActivity.class));
    }

    boolean isExpand;

    public void changeHeight(View view) {
        VariableHeightFrameLayout varFrameLayout = findViewById(R.id.varFrameLayout);
        if (isExpand) {
            varFrameLayout.expand();
        } else {
            varFrameLayout.collapse();
        }
        isExpand = !isExpand;
    }
}
