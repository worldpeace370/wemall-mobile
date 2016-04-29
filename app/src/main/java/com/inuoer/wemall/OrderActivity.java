package com.inuoer.wemall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inuoer.util.ActivityManager;
import com.inuoer.util.Config;
import com.inuoer.util.HttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderActivity extends AppCompatActivity{
	private SharedPreferences sharedpreferences;
	private String uid;
	private List<Map<String, String>> list;
	private Map<String, String> hashmap;
	private SimpleAdapter adapter;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				adapter.notifyDataSetChanged();
			} else {
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_order);
		setTransparent();

		initToolBar();

		sharedpreferences = this.getSharedPreferences("userInfo",
				Context.MODE_PRIVATE);
		uid = sharedpreferences.getString("uid", "");

		ListView lv = (ListView) findViewById(R.id.order_list);

		list = new ArrayList<Map<String, String>>();

		adapter = new SimpleAdapter(this, list,
				R.layout.itemlist_order, new String[] { "order_id",
						"order_status", "pay_status" }, new int[] {
						R.id.order_id, R.id.order_status, R.id.pay_status });

		lv.setAdapter(adapter);

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String result = HttpUtil.getPostJsonContent(Config.API_DO_ORDER
							+ "?uid=" + uid + "&do=2");
					if (!TextUtils.isEmpty(result)) {
						JSONArray jsonArray = JSONArray.parseArray(result);
						JSONObject myjObject;
						String order_status , pay_status;
						
						for (int i = 0; i < jsonArray.size(); i++) {
							myjObject = jsonArray.getJSONObject(i);
							hashmap = new HashMap<String, String>();
							hashmap.put("order_id", myjObject.getString("orderid"));
							
							order_status = myjObject.getString("order_status").equals("0")?"未发货":"已发货";
							pay_status = myjObject.getString("pay_status").equals("0")?"未付款":"已付款";
							
							hashmap.put("order_status", order_status);
							hashmap.put("pay_status", pay_status);
							list.add(hashmap);
						}

						handler.sendEmptyMessage(0x123);
					}

				} catch (Exception e) {

				}
			}
		}).start();
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String order_id = list.get(position).get("order_id");
				Intent intent = new Intent(OrderActivity.this, OrderDetailActivity.class);
				intent.putExtra("order_id", order_id);
				startActivity(intent);
			}
		});
	}

	/**
	 * 初始化ToolBar
	 */
	private void initToolBar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
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
		tabTitle.setText("我的订单");
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
