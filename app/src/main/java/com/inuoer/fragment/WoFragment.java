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
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inuoer.impl.UploadDataImpl;
import com.inuoer.interfaces.UploadDataListener;
import com.inuoer.manager.ObserverManager;
import com.inuoer.util.Config;
import com.inuoer.wemall.EditAddressActivity;
import com.inuoer.wemall.OrderActivity;
import com.inuoer.wemall.R;
import com.inuoer.wemall.SettingActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Fragment的生命周期方法中只有onCreateView()不用调用super.XXXX()方法
 * 其他的都得调用。调用replace()方法来切换Fragment的时候，其所有的生命周期方法均会执行
 */
public class WoFragment extends Fragment implements OnClickListener ,Observer{
	private Intent intent;
	private LayoutInflater inflater;
	private EditText loginphonetv;
	private EditText loginpasswordtv;
	private String loginphone , username;
	private String loginpassword;
	private SharedPreferences sharedpreferences;
	private long lastClick = 0;  
	public Dialog dialog;
	public LinearLayout registerLayout;
	public TextView registerphonetv , call_text;
	public TextView registerpasswordtv;
	private FrameLayout mFrameLayout;
	private Dialog mRegisterDialog;
	//POST方式上传用户信息到服务器的实现类
	private UploadDataImpl mUploadDataImpl;
    private String TAG = "WoFragment";
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		ObserverManager.getObserverManager().addObserver(this);
		mUploadDataImpl = new UploadDataImpl(getActivity());
		Log.i(TAG, "onAttach: 执行了");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView: 执行了");
		this.inflater = inflater;
		
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mFrameLayout = new FrameLayout(getActivity());
		mFrameLayout.setLayoutParams(params);

		mFrameLayout.addView(inflater.inflate(R.layout.wode_fragment, container, false));

		mFrameLayout.findViewById(R.id.wode_address).setOnClickListener(this);
		mFrameLayout.findViewById(R.id.wode_call).setOnClickListener(this);
		mFrameLayout.findViewById(R.id.wode_order).setOnClickListener(this);
		mFrameLayout.findViewById(R.id.wode_login_btn).setOnClickListener(this);
		mFrameLayout.findViewById(R.id.setting_not_login).setOnClickListener(this);
		mFrameLayout.findViewById(R.id.setting_has_login).setOnClickListener(this);
		//设置客服电话号码
		call_text = (TextView) mFrameLayout.findViewById(R.id.wode_call_text);
		call_text.setText(Config.PHONE);

		showDifferLayout();

		return mFrameLayout;
	}

	/**
	 * 根据用户是否登录显示不同的布局
	 */
	private void showDifferLayout() {
		sharedpreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		username = sharedpreferences.getString("username", "");
		if (!TextUtils.isEmpty(username)) {
			mFrameLayout.findViewById(R.id.wode_not_login_layout).setVisibility(View.GONE);
			mFrameLayout.findViewById(R.id.wode_has_login_layout).setVisibility(View.VISIBLE);
			TextView usernametv = (TextView) mFrameLayout.findViewById(R.id.wode_username);
			usernametv.setText(username);
		}
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressLint("NewApi")
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {//用户登陆后更改布局
				showDifferLayout();
				dialog.dismiss();
			} else if (msg.what == 0x124) {
				Toast.makeText(getActivity(), "请检查网络连接", Toast.LENGTH_LONG)
						.show();
			} else if (msg.what == 0x125){
				Toast.makeText(getActivity(), "登录失败,请核对手机号密码", Toast.LENGTH_LONG)
						.show();
			}else if (msg.what == 0x126){
				registerLayout.findViewById(R.id.gologin).callOnClick();
				Toast.makeText(getActivity(), "注册成功,请登录！", Toast.LENGTH_LONG)
						.show();
			}else if (msg.what == 0x127){
				Toast.makeText(getActivity(), "网络原因，注册失败,请重新注册", Toast.LENGTH_LONG)
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
			loginlayout.findViewById(R.id.dialog_detail_close).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			WindowManager windowManager = getActivity().getWindowManager();
			final Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
			lp.width = display.getWidth(); //设置宽度
			dialog.getWindow().setAttributes(lp);
			
			final Button registerButton = (Button) loginlayout.findViewById(R.id.goregister);
			final Button loginButton = (Button) loginlayout.findViewById(R.id.login);

			registerButton.setOnClickListener(new OnClickListener() {
				@SuppressLint("NewApi")
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					
					registerLayout = (LinearLayout) inflater.inflate(R.layout.activity_register, null);
					mRegisterDialog = new Dialog(getActivity());
					mRegisterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					mRegisterDialog.setContentView(registerLayout);
					mRegisterDialog.show();
					registerLayout.findViewById(R.id.dialog_detail_close).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mRegisterDialog.dismiss();
						}
					});
					WindowManager.LayoutParams lpr = mRegisterDialog.getWindow().getAttributes();
					lpr.width = display.getWidth(); //设置宽度
					mRegisterDialog.getWindow().setAttributes(lpr);
					
					final TextView registerusernametv = (TextView) registerLayout.findViewById(R.id.registerusername);
					registerphonetv = (TextView) registerLayout.findViewById(R.id.registerphone);
					registerpasswordtv = (TextView) registerLayout.findViewById(R.id.registerpassword);
					final TextView registerrepeatpasswordtv = (TextView) registerLayout.findViewById(R.id.registerrepeatpassword);
					
					registerLayout.findViewById(R.id.gologin).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mRegisterDialog.dismiss();
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
								
								if ( registerpassord.equals(registerrepeatpassord) && !(registerusername.isEmpty() || registerpassord.isEmpty() || registerphone.isEmpty()) ) {
									/****************用Volley方法实现post上传用户注册信息************************/
									Map<String, String> userRegisterMap = new HashMap<>();
									try {
										userRegisterMap.put("phone", URLEncoder.encode(registerphone, "UTF-8"));
										userRegisterMap.put("username", URLEncoder.encode(registerusername, "UTF-8"));
										userRegisterMap.put("password", URLEncoder.encode(registerpassord, "UTF-8"));
									} catch (UnsupportedEncodingException e) {
										e.printStackTrace();
								    }
									mUploadDataImpl.uploadData(Config.API_REGISTER, userRegisterMap, new UploadDataListener() {
										//result 是向服务器提交用户注册信息时，服务器返回的数据
										@Override
										public void onSuccess(String result) {
											Log.i("WoFragment", "onSuccess: " + result);
											if (!TextUtils.isEmpty(result)) {
												//注册成功,请登录！
												handler.sendEmptyMessage(0x126);
											}else{
												//注册失败,请重新注册
												handler.sendEmptyMessage(0x127);
											}
										}

										@Override
										public void onError() {

										}
									});
									/****************用HttpURLConnection方法实现post上传用户注册信息*************/
//									new Thread(new Runnable() {
//										@Override
//										public void run() {
//											String result = null;
//											try {
//												result = HttpUtil.getPostJsonContent(Config.API_REGISTER + "?phone=" +
//														URLEncoder.encode(registerphone, "UTF-8") + "&username=" + URLEncoder.encode(registerusername, "UTF-8")
//														+ "&password=" + URLEncoder.encode(registerpassord, "UTF-8"));
//											} catch (UnsupportedEncodingException e) {
//												e.printStackTrace();
//											}
//											if (!TextUtils.isEmpty(result)) {
//												//注册成功,请登录！
//												handler.sendEmptyMessage(0x126);
//											}else{
//												//注册失败,请重新注册
//												handler.sendEmptyMessage(0x127);
//											}
//										}
//									}).start();
								}else {
									Toast.makeText(getActivity(), "请输入完整的用户信息！", Toast.LENGTH_SHORT).show();
								}
							}
							
						}
					});
				}
			});
			loginButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (System.currentTimeMillis() - lastClick >= 1000) {
						lastClick = System.currentTimeMillis();
						loginphonetv = (EditText) loginlayout.findViewById(R.id.loginphone);
						loginpasswordtv = (EditText) loginlayout.findViewById(R.id.loginpassword);
						loginphone = loginphonetv.getText().toString();
						loginpassword = loginpasswordtv.getText().toString();

						if (!TextUtils.isEmpty(loginphone) && !TextUtils.isEmpty(loginpassword)) {
							/****************用Volley方法实现post上传用户登录信息************************/
							Map<String, String> userLoginMap = new HashMap<>();
							//用户登录信息装在Map里面
							try {
								userLoginMap.put("phone", URLEncoder.encode(loginphone, "UTF-8"));
								userLoginMap.put("password", URLEncoder.encode(loginpassword, "UTF-8"));
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							//开始调用上传数据实现类的方法实现用户信息上传，上传成功后将服务器返回结果result回调到本类中
							mUploadDataImpl.uploadData(Config.API_LOGIN, userLoginMap, new UploadDataListener() {
								@Override
								public void onSuccess(String result) {
									if (!TextUtils.isEmpty(result)) {
										JSONObject jsonObject = JSON.parseObject(result);
										//记住用户名,保存本地SharedPreferences
										Editor editor = sharedpreferences.edit();
										try {
											editor.putString("username", URLDecoder.decode(jsonObject.get("username").toString(), "UTF-8"));
											editor.putString("uid", jsonObject.get("uid").toString());
										} catch (UnsupportedEncodingException e) {
											e.printStackTrace();
										}
										editor.commit();
										handler.sendEmptyMessage(0x123);
									} else {
										handler.sendEmptyMessage(0x125);
									}
								}

								@Override
								public void onError() {

								}
							});
							/****************用HttpURLConnection方法实现post上传用户登录信息*************/
							//							new Thread(new Runnable() {
							//								public void run() {
							//									String result = null;
							//									try {
							//										result = HttpUtil.getPostJsonContent(Config.API_LOGIN + "?phone=" +
							//												URLEncoder.encode(loginphone, "UTF-8") + "&password=" + URLEncoder.encode(loginpassword, "UTF-8"));
							//									} catch (UnsupportedEncodingException e) {
							//										e.printStackTrace();
							//									}
							//									if (!TextUtils.isEmpty(result)) {
							//										JSONObject jsonObject = JSON.parseObject(result);
							//										//记住用户名,保存本地SharedPreferences
							//										Editor editor = sharedpreferences.edit();
							//										editor.putString("username", jsonObject.get("username").toString());
							//										editor.putString("uid", jsonObject.get("uid").toString());
							//										editor.commit();
							//										handler.sendEmptyMessage(0x123);
							//									}else {
							//										handler.sendEmptyMessage(0x125);
							//									}
							//
							//								}
							//							}).start();
						} else {
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
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		if (data instanceof String){
			String content = (String) data;
			if (content.equals("change")){
				mFrameLayout.findViewById(R.id.wode_not_login_layout).setVisibility(View.VISIBLE);
				mFrameLayout.findViewById(R.id.wode_has_login_layout).setVisibility(View.GONE);
				Toast.makeText(getActivity(), "注销当前账号成功", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.i(TAG, "onDestroyView: 执行了");
		ObserverManager.getObserverManager().deleteObserver(this);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.i(TAG, "onDetach: 执行了");
	}
}
