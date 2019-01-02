package com.android.yucheng.customviewdemo.material;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.yucheng.customviewdemo.R;
import com.android.yucheng.customviewdemo.fragment.CustomFragment;

import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION.SDK_INT;

/**
 * Created by lingjiu on 2019/1/2.
 */
public class TestActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<String> mTabs = new ArrayList<>();
    private List<Fragment> mFragments = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_test);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        initView();

        if (SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN//内容延伸到全屏
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE //固定view的位置
                    // | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION //隐藏底部虚拟导航栏,直播的时候可以用
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//23以上可以标记状态栏为亮色,字体会自动反转为深色
            // 部分机型的statusbar会有半透明的黑色背景(19-21,21以上可以设置颜色)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            //window.setNavigationBarColor(Color.TRANSPARENT);//使其底部虚拟导航栏颜色透明,直播的时候可用
        } else if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);
        }

    }

    private void initView() {
        for (int i = 0; i < 10; i++) {
            mTabs.add("自定义view" + i);
            CustomFragment fragment = new CustomFragment();
            Bundle args = new Bundle();
            args.putString("title", mTabs.get(i));
            fragment.setArguments(args);
            mFragments.add(fragment);
        }
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }


    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs.get(position);
        }
    }
}
