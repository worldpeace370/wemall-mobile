package com.inuoer.wemall;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.inuoer.adpater.StickyListAdapter;
import com.inuoer.util.ActivityManager;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by wuxiangkun on 2016/4/22 22:16.
 * Contacts wuxiangkun@live.com
 */
public class CityChoiceActivity extends FatherActivity{
    @Override
    protected int getContentView() {
        return R.layout.activity_city_choice;
    }

    @Override
    protected void initView() {
        final String[] countries = getResources().getStringArray(R.array.cities);
        StickyListHeadersListView stickyListView = findView(R.id.stickyListView);
        StickyListAdapter adapter = new StickyListAdapter(CityChoiceActivity.this, countries);
        stickyListView.setAdapter(adapter);
        stickyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String cityName = countries[position];
                Bundle bundle = new Bundle();
                bundle.putString("cityName", cityName);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(0x66, intent);
                finish();
            }
        });

        ImageButton buttonBack = findView(R.id.button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (!ActivityManager.hasKitKat()){//API<14
            ViewGroup.LayoutParams layoutParams =  toolbar.getLayoutParams();
            layoutParams.height = 70;
            toolbar.setLayoutParams(layoutParams);
        }
    }
}
