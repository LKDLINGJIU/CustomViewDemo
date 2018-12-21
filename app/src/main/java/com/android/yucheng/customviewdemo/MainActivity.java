package com.android.yucheng.customviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private TextNumberView textNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textNumber = ((TextNumberView) findViewById(R.id.textNumView));
    }

    public void changeLike(View view) {
        textNumber.changeNumber(6, false);
    }

    public void LikeJia(View view) {
        textNumber.like(true);
    }
}
