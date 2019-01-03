package com.android.yucheng.customviewdemo.material;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.yucheng.customviewdemo.R;

/**
 * Created by lingjiu on 2019/1/3.
 */
public class BehaviorPracticeActivity extends AppCompatActivity {

    private AppBarLayout appBarLayout;
    private View titleTv;
    private View titleIv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_behavior_practice);

        appBarLayout = ((AppBarLayout) findViewById(R.id.appBarLayout));
        titleTv = findViewById(R.id.title_tv);
        titleIv = findViewById(R.id.title_iv);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float percent = Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange();
                Log.i("yc", "percent = " + percent);
               /* ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) titleTv.getLayoutParams();
                layoutParams.topMargin = (int) DimensionUtils.dpToPx(20, BehaviorPracticeActivity.this);*/
            }
        });
    }
}
