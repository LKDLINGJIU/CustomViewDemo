package com.android.yucheng.customviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private TextNumberView textNumber;
    private ThumbsLikeView thumbsView;
    private View thumbLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textNumber = ((TextNumberView) findViewById(R.id.textNumView));
        thumbsView = ((ThumbsLikeView) findViewById(R.id.thumbView));
        thumbLayout = findViewById(R.id.thumbLayout);
    }

    public void changeLike(View view) {
        //textNumber.changeNumber(6, false);
    }

    boolean isLike =false;
    public void LikeJia(View view) {
         isLike =!isLike;
        /*textNumber.like(isLike);
        thumbsView.like(isLike);*/
        thumbLayout.setSelected(isLike);
    }
}
