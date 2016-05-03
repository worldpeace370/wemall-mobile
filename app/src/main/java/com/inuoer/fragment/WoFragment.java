package com.inuoer.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inuoer.util.Config;
import com.inuoer.util.HttpUtil;
import com.inuoer.wemall.EditAddressActivity;
import com.inuoer.wemall.OrderActivity;
import com.inuoer.wemall.R;
import com.inuoer.wemall.SettingActivity;

import java.net.URLEncoder;

public class WoFragment extends Fragment implements OnClickListener {
	private Intent intent;
	private LayoutInflater inflater;
	private TextView loginphonetv;
	private TextView loginpasswordtv;
	private String loginphone , username;
	private String loginpassword;
	private SharedPreferences sharedpreferences;
	private long lastClick = 0;  
	public Dialog dialog;
	public LinearLayout registerLayout;
	public TextView registerphonetv , call_text;
	public TextView registerpasswordtv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		FrameLayout frameLayout = new FrameLayout(getActivity());
		frameLayout.setLayoutParams(params);

		frameLayout.addView(inflater.inflate(R.layout.wode_fragment, container, false));

		frameLayout.findViewById(R.id.wode_address).setOnClickListener(this);
		frameLayout.findViewById(R.id.wode_call).setOnClickListener(this);
		frameLayout.findViewById(R.id.wode_order).setOnClickListener(this);
		frameLayout.findViewById(R.id.wode_login_btn).setOnClickListener(this);
		frameLayout.findViewById(R.id.setting_not_login).setOnClickListener(this);
		frameLayout.findViewById(R.id.setting_has_login).setOnClickListener(this);
		//设置客服电话号码
		call_text = (TextView) frameLayout.findViewById(R.id.wode_call_text);
		call_text.setText(Config.PHONE);
		
		sharedpreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE); 
		username = sharedpreferences.getString("username", "");
		//根据用户是否登录显示不同的布局
		if (!TextUtils.isEmpty(username)) {
			frameLayout.findViewById(R.id.wode_not_login_layout).setVisibility(View.GONE);
			frameLayout.findViewById(R.id.wode_has_login_layout).setVisibility(View.VISIBLE);
			TextView usernametv = (TextView) frameLayout.findViewById(R.id.wode_username);
			usernametv.setText(username);
		}
		
		return frameLayout;
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressLint("NewApi")
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_content, new WoFragment()).commit();
				dialog.dismiss();
			} else if (msg.what == 0x124) {
				Toast.makeText(getActivity(), "请检查网络连接", Toast.LENGTH_LONG)
						.show();
			} else if (msg.what == 0x125){
				Toast.makeText(getActivity(), "登录失败,请核对手机号密码", Toast.LENGTH_LONG)
						.show();
			}else if (msg.what == 0x126){
				registerLayout.findViewById(R.id.gologin).callOnClick();
				Toast.makeText(getActivity(), "注册成功,请登录", Toast.LENGTH_LONG)
						.show();
			}else if (msg.what == 0x127){
				Toast.makeText(getActivity(), "注册失败,请重新注册", Toast.LENGTH_LONG)
					.show();
			}
		}
	};
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.wode_address:
			intent = new Intent(getActivity(), EditAddressActivity.class);
			startActivity(intent);
			break;
		case R.id.wode_call:
			intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ Config.PHONE));
			startActivity(intent);
			break;
		case R.id.wode_order:
			intent = new Intent(getActivity(), OrderActivity.class);
			startActivity(intent);
			break;
		case R.id.wode_login_btn:
			final LinearLayout loginlayout = (LinearLayout) inflater.inflate(R.layout.activity_login, null);
			dialog = new Dialog(getActivity());
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(loginlayout);
			dialog.show();
			
			WindowManager windowManager = getActivity().getWindowManager();
			final Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
			lp.width = (int)(display.getWidth())*4/5; //设置宽度
			dialog.getWindow().setAttributes(lp);
			
			final Button registerButton = (Button) loginlayout.findViewById(R.id.goregister);
			final Button loginButton = (Button) loginlayout.findViewById(R.id.login);

			registerButton.setOnClickListener(new OnClickListener() {
				@SuppressLint("NewApi")
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					
					registerLayout = (LinearLayout) inflater.inflate(R.layout.activity_register, null);
					final Dialog registerDialog = new Dialog(getActivity());
					registerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					registerDialog.setContentView(registerLayout);
					registerDialog.show();
					
					WindowManager.LayoutParams lpr = registerDialog.getWindow().getAttributes();
					lpr.width = (int)(display.getWidth())*4/5; //设置宽度
					registerDialog.getWindow().setAttributes(lpr);
					
					final TextView registerusernametv = (TextView) registerLayout.findViewById(R.id.registerusername);
					registerphonetv = (TextView) registerLayout.findViewById(R.id.registerphone);
					registerpasswordtv = (TextView) registerLayout.findViewById(R.id.registerpassword);
					final TextView registerrepeatpasswordtv = (TextView) registerLayout.findViewById(R.id.registerrepeatpassword);
					
					registerLayout.findViewById(R.id.gologin).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							registerDialog.dismiss();
							dialog.show();
						}
					});
					registerLayout.findViewById(R.id.register).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (System.currentTimeMillis() - lastClick >= 1000){
					        	lastClick = System.currentTimeMillis();  
								final String registerusername = registerusernametv.getText().toString();
								final String registerphone = registerphonetv.getText().toString();
								final String registerpassord = registerpasswordtv.getText().toString();
								String registerrepeatpassord = registerrepeatpasswordtv.getText().toString();
								
								if ( registerpassord.equals(registerrepeatpassord) && !(registerusername.isEmpty() && registerpassord.isEmpty() && registerphone.isEmpty()) ) {
									new Thread(new Runnable() {
										@Override
										public void run() {
											@SuppressWarnings("deprecation")
											String result = HttpUtil.getPostJsonContent(Config.API_REGISTER + "?phone="+URLEncoder.encode(registerphone)+"&username="+URLEncoder.encode(registerusername)+"&password="+URLEncoder.encode(registerpassord));
											if (!result.isEmpty()) {
												handler.sendEmptyMessage(0x126);
											}else{
												handler.sendEmptyMessage(0x127);
											}
										}
									}).start();
								}else {
									Toast.makeText(getActivity(), "请核对用户名手机号密码", Toast.LENGTH_SHORT).show();
								}
							}
							
						}
					});
				}
			});
			loginButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (System.currentTimeMillis() - lastClick >= 1000){
			        	lastClick = System.currentTimeMillis();  
						loginphonetv = (TextView) loginlayout.findViewById(R.id.loginphone);
						loginpasswordtv = (TextView) loginlayout.findViewById(R.id.loginpassword);
						loginphone = loginphonetv.getText().toString();
						loginpassword = loginpasswordtv.getText().toString();
	
						if( !(loginphone.isEmpty() && loginpassword.isEmpty()) ){
							new Thread(new Runnable() {
								public void run() {
									String result = HttpUtil.getPostJsonContent(Config.API_LOGIN + "?phone="+URLEncoder.encode(loginphone)+"&password="+URLEncoder.encode(loginpassword));
									if (!result.isEmpty()) {
										JSONObject jsonObject = JSON.parseObject(result);
										//记住用户名  
										Editor editor = sharedpreferences.edit();  
										editor.putString("username", jsonObject.get("username").toString());  
										editor.putString("uid", jsonObject.get("uid").toString());
										editor.commit();
										handler.sendEmptyMessage(0x123);
									}else {
										handler.sendEmptyMessage(0x125);
									}
									
								}
							}).start();
						}else {
							Toast.makeText(getActivity(), "手机号密码不为空", Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
			break;
		case R.id.setting_not_login:
			intent = new Intent(getActivity(), SettingActivity.class);
			startActivity(intent);
			break;
		case R.id.setting_has_login:
			intent = new Intent(getActivity(), SettingActivity.class);
			startActivityForResult(intent, 0);//1表示注销用户功能
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == 0) {
			Toast.makeText(getActivity(), "注销当前账号成功", Toast.LENGTH_SHORT).show();
			if (resultCode == 1) {
				getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_content, new WoFragment()).commit();
				Toast.makeText(getActivity(), "注销当前账号成功", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
}
