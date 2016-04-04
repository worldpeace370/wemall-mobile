package com.inuoer.fragment;

import java.net.URLEncoder;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inuoer.util.CartAdapter;
import com.inuoer.util.CartData;
import com.inuoer.util.Config;
import com.inuoer.util.HttpUtil;
import com.inuoer.wemall.EditAddressActivity;
import com.inuoer.wemall.OrderActivity;
import com.inuoer.wemall.R;

public class CartFragment extends Fragment implements OnClickListener {
	private Intent intent;
	private LayoutInflater inflater;
	private LinearLayout ll,layout,ll_footer,ll_userinfo;
	private Dialog dialog;
	private ListView lv;
	private SharedPreferences sharedpreferences;
	private String username , uid;
	private TextView usernametv,phonetv,addresstv;
	private long lastClick = 0;
	private Boolean addressFlag = false;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				
			}else if(msg.what == 0x124){
				Toast.makeText(getActivity(), "订单提交成功", Toast.LENGTH_SHORT).show();
				CartData.removeAllCart();
				
				getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_content, new CartFragment()).commit();
				intent = new Intent(getActivity(), OrderActivity.class);
				startActivity(intent);
			}else {
				ll.findViewById(R.id.confirmorder_address_empty).setVisibility(View.GONE);
				ll.findViewById(R.id.confirmorder_address_full).setVisibility(View.VISIBLE);
				
				addressFlag = true;
				usernametv.setText(msg.getData().get("username").toString());
				phonetv.setText(msg.getData().get("phone").toString());
				addresstv.setText(msg.getData().get("address").toString());
			}
		}
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		ll = (LinearLayout) inflater.inflate(R.layout.confirmorder_content, null);

		ll.findViewById(R.id.confirmorder_address_add).setOnClickListener(this);
//		ll_userinfo = (LinearLayout) ll.findViewById(R.id.confirmorder_address_full);
		ll.findViewById(R.id.confirmorder_address_full)
				.setOnClickListener(this);
		ll.findViewById(R.id.confirmorder_userinfo_remarks).setOnClickListener(
				this);
		usernametv = (TextView) ll.findViewById(R.id.confirmorder_address_username);
		phonetv = (TextView) ll.findViewById(R.id.confirmorder_address_userphone);
		addresstv = (TextView) ll.findViewById(R.id.confirmorder_address_addrdesc);
		
		ll_footer = (LinearLayout) inflater.inflate(R.layout.confirmorder_footer, null);
		ll_footer.findViewById(R.id.submit).setOnClickListener(this);
		
		lv = new ListView(getActivity());
		lv.addHeaderView(ll);
		lv.addFooterView(ll_footer);

		CartAdapter MyAdapter = new CartAdapter(getActivity(), CartData.getCartList() , ll_footer);// 得到一个MyAdapter对象
		lv.setAdapter(MyAdapter);// 为ListView绑定Adapter
		
		sharedpreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE); 
		username = sharedpreferences.getString("username", "");
		uid = sharedpreferences.getString("uid", "");
		
		if (!username.isEmpty()) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						String result = HttpUtil.getPostJsonContent(Config.API_DO_ADDRESS+"?uid="+uid+"&do=1");
						if (!result.isEmpty()) {
							JSONObject jsonObject = JSONObject.parseObject(result);
							
							Message msg = new Message();
							Bundle data = new Bundle();
							data.putString("username", jsonObject.getString("username").toString());
							data.putString("phone", jsonObject.getString("phone").toString());
							data.putString("address", jsonObject.getString("address").toString());
							msg.setData(data);
							if (jsonObject.getString("address").toString().isEmpty()) {
								handler.sendEmptyMessage(0x123);
							}else{
								handler.sendMessage(msg);
							}
						}
						
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}).start();
		}
		
		return lv;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.confirmorder_address_add:
			if (!("".equals(username))) {
				intent = new Intent(getActivity(), EditAddressActivity.class);
				startActivityForResult(intent, 0);
			}else{
				Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.confirmorder_address_full:
			intent = new Intent(getActivity(), EditAddressActivity.class);
			startActivityForResult(intent, 0);
			break;
		case R.id.confirmorder_userinfo_remarks:
			layout = (LinearLayout) inflater.inflate(
					R.layout.dialog_remark, null);
			dialog = new Dialog(getActivity());
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(layout);
			dialog.show();

			layout.findViewById(R.id.dialog_button_right).setOnClickListener(this);
			layout.findViewById(R.id.dialog_button_left).setOnClickListener(this);
			break;
		case R.id.dialog_button_right:
			dialog.dismiss();
			break;
		case R.id.dialog_button_left:
			dialog.dismiss();

			EditText remarks = (EditText) layout
					.findViewById(R.id.remarks_inputer);
			TextView textView = (TextView) ll
					.findViewById(R.id.confirmorder_userinfo_remarks);
			textView.setText(remarks.getText());
			break;
		case R.id.submit:
			if (System.currentTimeMillis() - lastClick >= 1000){
	        	lastClick = System.currentTimeMillis(); 
				if (!("".equals(username))) {
					if (addressFlag) {
						if (CartData.getCartCount() != 0) {
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									try {
										TextView notetv = (TextView) ll.findViewById(R.id.confirmorder_userinfo_remarks);
										String result = HttpUtil.getPostJsonContent(Config.API_DO_ORDER + "?uid="+uid+"&cartdata="+URLEncoder.encode(JSONArray.toJSON(CartData.getCartList()).toString())+"&note="+URLEncoder.encode(notetv.getText().toString())+"&do=1");
										if (!result.isEmpty()) {
											handler.sendEmptyMessage(0x124);
										}
									} catch (Exception e) {
										// TODO: handle exception
									}
								}
							}).start();
						}else{
							Toast.makeText(getActivity(), "请选择商品", Toast.LENGTH_SHORT).show();
						}
					}else {
						Toast.makeText(getActivity(), "请添加地址", Toast.LENGTH_SHORT).show();
					}
					
				}else{
					Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_LONG).show();
				}
			}
			
			break;
		default:
			break;
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == 0) {
			if (resultCode == 1) {
				getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_content, new CartFragment()).commit();
			}
		}
	}
}
