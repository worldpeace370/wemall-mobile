package com.inuoer.wemall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
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
				setResult(1);
				finish();
			}else{
				username.setText(msg.getData().get("username").toString());
				phone.setText(msg.getData().get("phone").toString());
				address.setText(msg.getData().get("address").toString());
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.address_addedit);

		sharedpreferences = this.getSharedPreferences("userInfo",
				Context.MODE_PRIVATE);
		uid = sharedpreferences.getString("uid", "");

		username = (TextView) findViewById(R.id.address_username);
		phone = (TextView) findViewById(R.id.address_userphone);
		address = (TextView) findViewById(R.id.address_useraddress);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String result = HttpUtil
							.getPostJsonContent(Config.API_DO_ADDRESS + "?uid="
									+ uid + "&do=1");
					if (!result.isEmpty()) {
						JSONObject jsonObject = JSONObject.parseObject(result);
						
						Message msg = new Message();
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

		findViewById(R.id.edit_address_actionbar_left_back).setOnClickListener(
				this);
		findViewById(R.id.edit_address_actionbar_save).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.edit_address_actionbar_left_back:
			finish();
			break;
		case R.id.edit_address_actionbar_save:
			 //大于一秒方个通过  
	        if (System.currentTimeMillis() - lastClick >= 1000){
	        	lastClick = System.currentTimeMillis();  
	        	new Thread(new Runnable() {
	        		
	        		@Override
	        		public void run() {
	        			try {
	        				String result = HttpUtil.getPostJsonContent(Config.API_DO_ADDRESS + "?uid="+uid+"&do=2&address="+URLEncoder.encode(address.getText().toString(), "utf-8"));
//						if (!result.isEmpty()) {
	        				handler.sendEmptyMessage(0x123);
//						}
	        			} catch (Exception e) {

	        			}
	        		}
	        	}).start();
	        
	        }  
			
			break;
		default:
			break;
		}
	}

}
