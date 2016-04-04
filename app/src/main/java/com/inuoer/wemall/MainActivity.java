package com.inuoer.wemall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inuoer.fragment.CartFragment;
import com.inuoer.fragment.MainFragment;
import com.inuoer.fragment.WoFragment;
import com.inuoer.util.CartData;
import com.inuoer.util.Config;
import com.inuoer.util.HttpUtil;
import com.inuoer.util.MainAdapter;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements OnClickListener{
	public String jsonString;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	public TextView title;
	public List<Fragment> fragments = new ArrayList<Fragment>();
	public List<RadioButton> Tabs = new ArrayList<RadioButton>();
	public ArrayList<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();
	public MainAdapter MyAdapter;
	public ProgressDialog progressDialog;
//	public LinearLayout popmenull;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				MyAdapter.notifyDataSetChanged();
			} else if (msg.what == 0x124) {
				Toast.makeText(MainActivity.this, "请检查网络连接!", Toast.LENGTH_LONG)
						.show();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		RelativeLayout actionbar = (RelativeLayout) this.getLayoutInflater()
				.inflate(R.layout.fragment_actionbar, null);
		title = (TextView) actionbar
				.findViewById(R.id.fragment_actionbar_title);
//		popmenull = (LinearLayout) actionbar.findViewById(R.id.fragment_actionbar_menu);
//		popmenull.setOnClickListener(this);
		
		LinearLayout ll = (LinearLayout) findViewById(R.id.fragment_actionbar_linearlayout);
		ll.addView(actionbar);
	        
		MyAdapter = new MainAdapter(this, listItem);
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("正在加载中...");
		progressDialog.setCancelable(true);
		progressDialog.show();
		initData();

		Tabs.add((RadioButton)findViewById(R.id.shop));
		Tabs.add((RadioButton)findViewById(R.id.cart));
		Tabs.add((RadioButton)findViewById(R.id.wode));
		
		Tabs.get(0).setOnClickListener(this);
		Tabs.get(1).setOnClickListener(this);
		Tabs.get(2).setOnClickListener(this);

		fragments.add(new MainFragment(listItem , MyAdapter));
		fragments.add(new CartFragment());
		fragments.add(new WoFragment());
		
		fragmentManager = getSupportFragmentManager();
		Tabs.get(0).callOnClick();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.shop:
			fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.framelayout_content, fragments.get(0));
			title.setText("wemall");
			fragmentTransaction.commit();

			break;
		case R.id.cart:
			fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.framelayout_content, fragments.get(1));
			title.setText("提交订单");
			fragmentTransaction.commit();
			
			break;
		case R.id.wode:
			fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.framelayout_content, fragments.get(2));
			title.setText("我的");
			fragmentTransaction.commit();
			
			break;
//		case R.id.fragment_actionbar_menu:
//			Toast.makeText(this, "11111111", Toast.LENGTH_SHORT).show();
//			
//			break;
		}
	}
	
	public void initData() {
		new Thread(new Runnable() {
			public void run() {
				try {
					jsonString = HttpUtil
							.getGetJsonContent(Config.API_GET_GOODS);
					
					JSONArray jsonArr = JSON.parseArray(jsonString);
					for (int i = 0; i < jsonArr.size(); i++) {
						// 获取每一个JsonObject对象
						JSONObject myjObject = jsonArr.getJSONObject(i);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("id", i);
						//new GetBitmapUtil().getBitmapByUrl(Config.API_UPLOADS+myjObject.getString("image"));
						map.put("image", Config.API_UPLOADS+myjObject.getString("image"));
						map.put("name", myjObject.getString("name"));
						map.put("price", myjObject.getString("price"));
						map.put("num", CartData.findCart(i));
						listItem.add(map);
					}
//
					handler.sendEmptyMessage(0x123);
					progressDialog.dismiss();
				} catch (Exception e) {
					// TODO: handle exception
//					System.out.println(e);
					handler.sendEmptyMessage(0x124);
					// System.out.println("请检查网络连接");
				}

			}
		}).start();

	}
}
