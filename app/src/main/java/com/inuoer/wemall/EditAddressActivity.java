package com.inuoer.wemall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.inuoer.manager.ObserverManager;
import com.inuoer.util.ActivityManager;
import com.inuoer.util.Config;
import com.inuoer.util.HttpUtil;

import java.net.URLEncoder;

public class EditAddressActivity extends Activity implements OnClickListener {

	private SharedPreferences sharedpreferences;
	private String uid;
	private TextView username, phone, address;
	private long lastClick = 0;  
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				Toast.makeText(EditAddressActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
				finish();
			}else if(msg.what == 0x456){
				username.setText(msg.getData().get("username").toString());
				phone.setText(msg.getData().get("phone").toString());
				address.setText(msg.getData().get("address").toString());
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address_addedit);
		setTransparent();
		initToolBar();

		sharedpreferences = this.getSharedPreferences("userInfo",
				Context.MODE_PRIVATE);
		uid = sharedpreferences.getString("uid", "");

		username = (TextView) findViewById(R.id.address_username);
		phone = (TextView) findViewById(R.id.address_userphone);
		address = (TextView) findViewById(R.id.address_useraddress);
		//向服务器下载用户的地址信息
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String result = HttpUtil
							.getPostJsonContent(Config.API_DO_ADDRESS + "?uid="
									+ uid + "&do=1");
					if (!TextUtils.isEmpty(result)) {
						JSONObject jsonObject = JSONObject.parseObject(result);
						
						Message msg = Message.obtain();
						msg.what = 0x456;
						Bundle data = new Bundle();
						data.putString("username", jsonObject.getString("username"));
						data.putString("phone", jsonObject.getString("phone"));
						data.putString("address", jsonObject.getString("address"));
						msg.setData(data);
						handler.sendMessage(msg);
					}

				} catch (Exception e) {

				}
			}
		}).start();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_back:
			finish();
			break;
		case R.id.button_save://将更改后的地址信息提交到服务器
			 //大于一秒方个通过  
	        if (System.currentTimeMillis() - lastClick >= 1000){
	        	lastClick = System.currentTimeMillis();  
	        	new Thread(new Runnable() {
	        		@Override
	        		public void run() {
	        			try {
	        				String result = HttpUtil.getPostJsonContent(Config.API_DO_ADDRESS + "?uid=" + uid + "&do=2&address="
									+ URLEncoder.encode(address.getText().toString(), "utf-8"));
							if (!TextUtils.isEmpty(result)) {
								handler.sendEmptyMessage(0x123);
							}
	        			} catch (Exception e) {

	        			}
	        		}
	        	}).start();
				//更改地址信息后，通知CartFragment中视图更改UI
				ObserverManager.getObserverManager().sendMessage(address.getText().toString());
	        }  
			
			break;
		default:
			break;
		}
	}

	private void initToolBar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("");
		if (!ActivityManager.hasKitKat()){//API<14
			ViewGroup.LayoutParams layoutParams =  toolbar.getLayoutParams();
			layoutParams.height = (int)(50 * getResources().getDisplayMetrics().density);//设置50dp高，height的值是px，所以需要转化
			toolbar.setLayoutParams(layoutParams);
		}
		ImageButton buttonBack = (ImageButton) toolbar.findViewById(R.id.button_back);
		buttonBack.setOnClickListener(this);

		Button buttonSave = (Button) toolbar.findViewById(R.id.button_save);
		buttonSave.setOnClickListener(this);
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
