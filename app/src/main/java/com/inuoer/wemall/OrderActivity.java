package com.inuoer.wemall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inuoer.util.ActivityManager;
import com.inuoer.util.Config;
import com.inuoer.util.HttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据用户的登录信息，从服务器下载用户对应的订单信息。
 * 点击订单信息，跳转到详情页面
 * 如果没有登录，则弹出吐司
 */
public class OrderActivity extends Activity {
	private SharedPreferences sharedpreferences;
	private String uid;
	private List<Map<String, String>> list;
	private Map<String, String> hashmap;
	private SimpleAdapter adapter;
	private ProgressDialog mProgressDialog;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				dismissProgressDialog();
				if (list.size() == 0){
					Toast.makeText(OrderActivity.this, "您还没有下单呢！", Toast.LENGTH_SHORT).show();
				}else {
					adapter.notifyDataSetChanged();
				}
			} else {
			}
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);
		setTransparent();

		initToolBar();

		sharedpreferences = this.getSharedPreferences("userInfo",
				Context.MODE_PRIVATE);
		uid = sharedpreferences.getString("uid", "");
		TextView textViewEmpty = ((TextView) findViewById(R.id.textView_empty));
		ListView listView = (ListView) findViewById(R.id.order_list);

		list = new ArrayList<Map<String, String>>();

		adapter = new SimpleAdapter(this, list,
				R.layout.itemlist_order, new String[] { "order_id",
						"order_status", "pay_status" }, new int[] {
						R.id.order_id, R.id.order_status, R.id.pay_status });

		listView.setAdapter(adapter);
		if (TextUtils.isEmpty(uid)){
			Toast.makeText(OrderActivity.this, "请登录", Toast.LENGTH_SHORT).show();
		}else {  //登录后
			showProgressDialog();
			/*网络下载订单数据，填充到ListView中去*/
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
						}
						handler.sendEmptyMessage(0x123);
					} catch (Exception e) {

					}
				}
			}).start();

			/*ListView的点击事件，进入到订单详情页面，将order_id传过去*/
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
					String order_id = list.get(position).get("order_id");
					Intent intent = new Intent(OrderActivity.this, OrderDetailActivity.class);
					intent.putExtra("order_id", order_id);
					startActivity(intent);
				}
			});
			listView.setEmptyView(textViewEmpty);
		}
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

	private void showProgressDialog(){
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setMessage("正在加载中...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	}

	private void dismissProgressDialog(){
		mProgressDialog.dismiss();
	}
}
