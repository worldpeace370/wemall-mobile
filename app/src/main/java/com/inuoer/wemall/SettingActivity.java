package com.inuoer.wemall;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.inuoer.manager.ObserverManager;
import com.inuoer.util.ActivityManager;

/**
 * 退出登录,删除SharedPreferences对应数据
 */
public class SettingActivity extends Activity {
	private SharedPreferences sharedpreferences;
	private String username;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wode_setting);
		setTransparent();
		initToolBar();
		sharedpreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE); 
		username = sharedpreferences.getString("username", "");

		if (!TextUtils.isEmpty(username)) {
			findViewById(R.id.setting_exit).setVisibility(View.VISIBLE);
		}
		
		findViewById(R.id.setting_exit).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(SettingActivity.this)
					.setMessage("确认要退出账号吗？")
					.setPositiveButton("是", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							sharedpreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
							Editor editor = sharedpreferences.edit();  
			                editor.remove("username");
			                editor.remove("uid");
			                editor.commit();
							ObserverManager.getObserverManager().sendMessage("change");
			                finish();
						}
					}).setNegativeButton("否", null).show();
			}
		});
	}

	/**
	 * 初始化ToolBar
	 */
	private void initToolBar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("");
		if (!ActivityManager.hasKitKat()){//API<14
			ViewGroup.LayoutParams layoutParams =  toolbar.getLayoutParams();
			layoutParams.height = (int)(50 * getResources().getDisplayMetrics().density);//设置50dp高，height的值是px，所以需要转化
			toolbar.setLayoutParams(layoutParams);
		}

		ImageButton buttonBack = (ImageButton) toolbar.findViewById(R.id.button_back);
		buttonBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		TextView tabTitle = (TextView) toolbar.findViewById(R.id.tab_title);
		tabTitle.setText("设置");
	}

	/**
	 * 设置状态栏透明
	 */
	private void setTransparent() {
		if (ActivityManager.hasKitKat() && !ActivityManager.hasLollipop()){
			//透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//透明导航栏
			//getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}else if (ActivityManager.hasLollipop()){
			Window window = getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
					| WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
		}
	}
}
