package com.inuoer.wemall;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class SettingActivity extends Activity {
	private SharedPreferences sharedpreferences;
	private String username;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wode_setting);
		
		sharedpreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE); 
		username = sharedpreferences.getString("username", "");

		if (!("".equals(username))) {
			findViewById(R.id.setting_exit).setVisibility(View.VISIBLE);
		}
		
		findViewById(R.id.wode_setting_actionbar_left).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		findViewById(R.id.setting_exit).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				new AlertDialog.Builder(SettingActivity.this)
					.setMessage("确认要退出账号吗？")
					.setPositiveButton("是", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							sharedpreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE); 
							Editor editor = sharedpreferences.edit();  
			                editor.remove("username");
			                editor.remove("uid");
			                editor.commit(); 
			                
			                setResult(1);
			                finish();
						}
					}).setNegativeButton("否", null).show();
			}
		});
		findViewById(R.id.setting_about).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingActivity.this, AboutActivity.class);
				startActivity(intent);
			}
		});
	}

}
