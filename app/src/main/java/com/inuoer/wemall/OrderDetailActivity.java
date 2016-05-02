package com.inuoer.wemall;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListAdapter;
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

/**
 * 订单详情页面，根据OrderActivity传来的订单编号，从服务器下载相应的详情
 */
public class OrderDetailActivity extends AppCompatActivity{
	private String order_id;
	private SharedPreferences sharedpreferences;
	private String uid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_order_detail);
		setTransparent();
		initToolBar();
		order_id = getIntent().getStringExtra("order_id");

		sharedpreferences = this.getSharedPreferences("userInfo",
				Context.MODE_PRIVATE);
		uid = sharedpreferences.getString("uid", "");
		
		Task task = new Task();
		task.execute( Config.API_DO_ORDER+"?orderid="+order_id+"&uid="+uid+"&do=3" );
		
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
		tabTitle.setText("订单详情");
	}

	class Task extends AsyncTask<String, Integer, String>{
		public TextView order_detail_username, order_detail_tel,
				order_detail_address, order_detail_note,
				order_detail_total_price, order_detail_order_status,
				order_detail_pay_style, order_detail_pay_status;
		public List<Map<String, String>> list;
		public Map<String, String> hashmap;
		public SimpleAdapter adapter;
		
		@Override
		protected String doInBackground(String... params) {

			String result = HttpUtil.getPostJsonContent(params[0]);
			
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			JSONObject jsonObject = JSONObject.parseObject(result);
		
			order_detail_username = (TextView) findViewById(R.id.order_detail_username);
			order_detail_tel = (TextView) findViewById(R.id.order_detail_tel);
			order_detail_address = (TextView) findViewById(R.id.order_detail_address);
			order_detail_note = (TextView) findViewById(R.id.order_detail_note);
			order_detail_total_price = (TextView) findViewById(R.id.order_detail_total_price);
			order_detail_order_status = (TextView) findViewById(R.id.order_detail_order_status);
			order_detail_pay_style = (TextView) findViewById(R.id.order_detail_pay_style);
			order_detail_pay_status = (TextView) findViewById(R.id.order_detail_pay_status);
			
			String order_status = jsonObject.getString("order_status").equals("0")?"未发货":"已发货";
			String pay_status = jsonObject.getString("pay_status").equals("0")?"未付款":"已付款";
			
			order_detail_note.setText(jsonObject.getString("note"));
			order_detail_total_price.setText(jsonObject.getString("totalprice"));
			order_detail_order_status.setText(order_status);
			order_detail_pay_style.setText(jsonObject.getString("pay_style"));
			order_detail_pay_status.setText(pay_status);
			order_detail_username.setText(jsonObject.getString("username"));
			order_detail_tel.setText(jsonObject.getString("phone"));
			order_detail_address.setText(jsonObject.getString("address"));
			
			list = new ArrayList<Map<String, String>>();
			JSONArray jsonArray = JSONArray.parseArray(jsonObject.getString("cartdata"));
			JSONObject myjObject;
			
			for (int i = 0; i < jsonArray.size(); i++) {
				myjObject = jsonArray.getJSONObject(i);
				hashmap = new HashMap<String, String>();
				hashmap.put("name", myjObject.getString("name"));
				hashmap.put("num", myjObject.getString("num"));
				hashmap.put("price", myjObject.getString("price"));
				list.add(hashmap);
			}
			
			ListView listView = (ListView) findViewById(R.id.order_detail_listview);
			adapter = new SimpleAdapter(OrderDetailActivity.this, list,
					R.layout.activity_order_detail_append, new String[] {
							"name", "num", "price" }, new int[] {
							R.id.order_detail_name, R.id.order_detail_num,
							R.id.order_detail_price });
			listView.setAdapter(adapter);
			fixListViewHeight(listView);
		}

		//ScrollView中listview的高度没法适配,所以必须通过代码计算出高度,也可以自定义ListView,重写onMeasure方法
		private void fixListViewHeight(ListView listView) {
		      // 如果没有设置数据适配器，则ListView没有子项，返回。
	        ListAdapter listAdapter = listView.getAdapter();  
	        int totalHeight = 0;   
	        if (listAdapter == null) {   
	            return;   
	        }   
	        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {     
	            View listViewItem = listAdapter.getView(i , null, listView);  
	            // 计算子项View 的宽高   
	            listViewItem.measure(0, 0);    
	            // 计算所有子项的高度和
	            totalHeight += listViewItem.getMeasuredHeight();    
	        }   
	   
	        ViewGroup.LayoutParams params = listView.getLayoutParams();   
	        // listView.getDividerHeight()获取子项间分隔符的高度   
	        // params.height设置ListView完全显示需要的高度    
	        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));   
	        listView.setLayoutParams(params);   
		}
		
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
