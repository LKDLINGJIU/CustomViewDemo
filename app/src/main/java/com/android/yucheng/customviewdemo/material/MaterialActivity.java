package com.android.yucheng.customviewdemo.material;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.yucheng.customviewdemo.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;
import static android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED;
import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;

/**
 * Created by lingjiu on 2018/12/27.
 */
public class MaterialActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private View bottomSheet;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private BottomSheetDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.at_meterial);
        initRecycler();
        initBottomSheet();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    private void initBottomSheet() {
        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    private void initRecycler() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("条目:" + i);
        }
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BaseQuickAdapter<String, BaseViewHolder>(R.layout.simple_item_text, list) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv, item);
            }

        });
    }


    public void showDialog(View view) {
        if (dialog == null) {
            dialog = new BottomSheetDialog(this);
            View dialogView = LayoutInflater.from(this)
                    .inflate(R.layout.list_bottom, null);
            ListView listView = (ListView) dialogView.findViewById(R.id.listView);
            String[] array = new String[]{"item-1", "item-2", "item-3", "item-4", "item-5", "item-6", "item-7", "item-8", "item-9"};
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array);
            listView.setAdapter(adapter);
            dialog.setContentView(dialogView);
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(((View) dialogView.getParent()));
            behavior.setPeekHeight(400);
        }
        dialog.show();
    }


    public void hide(View view) {
        bottomSheetBehavior.setState(STATE_HIDDEN);
    }

    public void collapse(View view) {
        bottomSheetBehavior.setState(STATE_COLLAPSED);
    }

    public void expand(View view) {
        bottomSheetBehavior.setState(STATE_EXPANDED);
    }
}
